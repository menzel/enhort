package de.thm.calc;

import de.thm.genomeData.tracks.DistanceTrack;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.ScoredTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.logo.LogoCreator;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.stat.IndependenceTest;
import de.thm.stat.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Implements multithreading of the intersect call.
 * Increase thread count on biggger machines.
 * <p>
 * Created by Michael Menzel on 11/1/16.
 */
public final class CalcCaller {

    private final Logger logger = LoggerFactory.getLogger(CalcCaller.class);
    private final ExecutorPool exe;

    public CalcCaller() {
        exe = ExecutorPool.getInstance();
    }


    /**
     * Executes the intersect algorithm with two sets of sites on a given map of tracks.
     * *
     *
     * @param tracks         map of tracks with <K,V> <Name, Interval reference>
     * @param measuredPositions - positions supplied from outside
     * @param randomPositions   - positions created by a background model
     * @return Collector of all results computed by the different threads
     */
    public ResultCollector execute(List<Track> tracks, Sites measuredPositions, Sites randomPositions, boolean createLogo) {

        ////////////  Tracks intersect ////////////////

        ResultCollector collector = new ResultCollector(randomPositions, tracks.get(0).getAssembly(), tracks); // get assembly from the first track

        List<Future> futures = Collections.synchronizedList(new ArrayList<>());

        if(measuredPositions.getPositions().size() < 1){
            return collector; // TODO inform user that there are no sites, fileformat wrong?
        }

        for (Track track : tracks) {

            IntersectWrapper<? extends Track> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, track, collector);
            futures.add(exe.submit(wrapper));
        }

        ////////////  Tracks intersect ////////////////

        //////////// Sequencelogo ////////////////

        if(createLogo && measuredPositions.getAssembly().equals(Genome.Assembly.hg19)){
            String first = ((UserData) measuredPositions).getFilename();
            LogoWrapper logoWrapper = new LogoWrapper(measuredPositions,collector, tracks.get(0).getAssembly(), first);
            futures.add(exe.submit(logoWrapper));

            //String second = randomPositions.getFilename();
            String second = "Background";
            LogoWrapper logoWrapper2 = new LogoWrapper(randomPositions, collector, tracks.get(0).getAssembly(), second);
            futures.add(exe.submit(logoWrapper2));
        }

        //////////// Sequencelogo ////////////////

        //////////// Hotspots ////////////////
        HotspotWrapper hotspotWrapper = new HotspotWrapper(measuredPositions, collector);
        futures.add(exe.submit(hotspotWrapper));
        //////////// Hotspots ////////////////


        while(futures.stream().filter(f -> ! f.isDone()).count() > 0){

            try {
                Thread.sleep(100); //wait some time and check if the results are ready
                logger.debug("Waiting for " +  futures.stream().filter(f -> ! f.isDone()).count() + " futures");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        futures.forEach(f -> f.cancel(true));

        //////////// PCA ////////////////
        PCA pca = new PCA();
        pca.createBedPCA(collector);
        //////////// PCA ////////////////


        //////////// circ plot ////////////////

        List<Track> top = collector.getResults().stream() // filter tracks, only check the highest scoring ones
                .filter(t -> t.getTrack() instanceof InOutTrack)
                .filter(t -> t.getpValue() < 0.05 / tracks.size())
                .filter(t -> !t.getTrack().getName().equals("Blacklisted Regions"))
                .filter(testResult -> testResult.getMeasuredIn() >= (testResult.getMeasuredIn() + testResult.getMeasuredOut()) / 200
                        && testResult.getExpectedIn() >= (testResult.getExpectedIn() + testResult.getExpectedOut()) / 200)
                .sorted(Comparator.comparingDouble(TestResult::getEffectSize))
                .map(TestResult::getTrack)
                .collect(Collectors.toList());

        if (top.size() == 0)
            return collector;

        top = top.subList(Math.max(top.size() - 7, 0), top.size() - 1); // take top 6 tracks

        PositionalIntersect positionalIntersect = new PositionalIntersect();

        for (Track t : top) {
            collector.addResult(positionalIntersect.searchTrack((InOutTrack) t, measuredPositions));
        }

        //////////// circ plot ////////////////

        return collector;
    }

    private final class IntersectWrapper<T extends Track> implements Runnable {


        private final Sites randomPos;
        private final Sites measuredPos;
        private final T track;
        private final ResultCollector collector;

        /**
         * Constructor for the wrapper object
         *
         * @param measuredPos - positions from the outside of the program
         * @param randomPos   - positions to match against made up by a background model
         * @param track       - track to match against
         * @param collector   - collector to collect results in
         */
        private IntersectWrapper(Sites measuredPos, Sites randomPos, T track, ResultCollector collector) {

            this.randomPos = randomPos;
            this.measuredPos = measuredPos;
            this.track = track;
            this.collector = collector;
        }

        @Override
        public void run() {
            TestTrack<T> intersec1 = new Intersect<>();
            TestTrack<T> intersec2 = new Intersect<>();

            TestTrackResult result1 = intersec1.searchTrack(track, measuredPos);
            TestTrackResult result2 = intersec2.searchTrack(track, randomPos);

            IndependenceTest tester = new IndependenceTest();

            TestResult statTestResult = tester.test(result1, result2, track);

            Objects.requireNonNull(statTestResult);
            collector.addResult(statTestResult);
        }
    }



    private final class DistanceWrapper implements Runnable {


        private final Sites randomPos;
        private final Sites measuredPos;
        private final DistanceTrack track;
        private final ResultCollector collector;

        /**
         * Constructor for the wrapper object
         *
         * @param measuredPos - positions from the outside of the program
         * @param randomPos   - positions to match against made up by a background model
         * @param track       - track to match against
         * @param collector   - collector to collect results in
         */
        private DistanceWrapper(Sites measuredPos, Sites randomPos, DistanceTrack track, ResultCollector collector) {

            this.randomPos = randomPos;
            this.measuredPos = measuredPos;

            // create a copy of the track for the results, so that the names do not interfere later:
            this.track = track;

            this.collector = collector;
        }

        @Override
        public void run() {
            TestTrack<DistanceTrack> dist1 = new Distances();
            TestTrack<DistanceTrack> dist2 = new Distances();

            TestTrackResult result1 = dist1.searchTrack(track, measuredPos);
            TestTrackResult result2 = dist2.searchTrack(track, randomPos);

            IndependenceTest tester = new IndependenceTest();


            TestResult statTestResult = tester.testScoredTrack(result1, result2, track);

            collector.addResult(statTestResult);

        }
    }


    private final class LogoWrapper implements Runnable {


        private final Sites measuredPos;
        private final ResultCollector collector;
        private final Genome.Assembly assembly;
        private final String name;

        /**
         * Constructor for the wrapper object
         *  @param measuredPos - positions from the outside of the program
         * @param collector   - collector to collect results in
         * @param name -  filename or random
         */
        private LogoWrapper(Sites measuredPos, ResultCollector collector, Genome.Assembly assembly, String name) {

            this.measuredPos = measuredPos;
            this.collector = collector;
            this.assembly = assembly;
            this.name = name;
        }

        @Override
        public void run() {
            GenomeFactory genome = GenomeFactory.getInstance();
            int width = 12;//TODO use user set value
            int count = 300;//TODO use user set value

            Logo sequencelogo = LogoCreator.createLogo(Objects.requireNonNull(genome.getSequence(assembly, measuredPos, width, count)));
            sequencelogo.setName(name);

            collector.addLogo(sequencelogo);
        }
    }

    private class HotspotWrapper implements Runnable{
        private final Sites measuredPositions;
        private final ResultCollector collector;

        private HotspotWrapper(Sites measuredPositions, ResultCollector collector) {

            this.measuredPositions = measuredPositions;
            this.collector = collector;
        }

        @Override
        public void run() {
            Hotspot hotspot = new Hotspot();
            ScoredTrack hotspots = hotspot.findHotspots(measuredPositions, (int) (ChromosomSizes.getInstance().getGenomeSize(measuredPositions.getAssembly()) / 200));

            List<Double> hs = Arrays.stream(hotspots.getIntervalScore()).boxed().collect(Collectors.toList());

            double factor = 50 / Collections.max(hs);
            // change score by calc relative score to 50 (where 50 is the max), add 50 to have values ranging from 50 to 100. Then invert values to have highest values as 50% and lowest values as 100%
            // The calculated score is used as 'x' in hsl(100,100,x). Where the third param is the lightness

            collector.addHotspots(hs.stream().map(i -> i * factor + 50).map(i -> (100 - i) + 50).map(Double::intValue).collect(Collectors.toList()));
        }
    }
}
