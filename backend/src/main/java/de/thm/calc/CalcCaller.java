package de.thm.calc;

import de.thm.genomeData.tracks.*;
import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.logo.Logo;
import de.thm.logo.LogoCreator;
import de.thm.positionData.Sites;
import de.thm.result.ResultCollector;
import de.thm.stat.EffectSize;
import de.thm.stat.IndependenceTest;
import de.thm.stat.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thm.misc.Genome;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

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
     * @return Collector of all results computed by the differen threads
     */
    public ResultCollector execute(List<Track> tracks, Sites measuredPositions, Sites randomPositions, boolean createLogo) {

        ////////////  Tracks intersect ////////////////

        ResultCollector collector = new ResultCollector(randomPositions, tracks.get(0).getAssembly(), tracks); // get assembly from the first track
        logger.debug("Running the calc now");

        List<Future> futures = Collections.synchronizedList(new ArrayList<>());

        if(measuredPositions.getPositions().size() < 1){
            return collector; // TODO inform user that there are no sites, fileformat wrong?
        }

        for (Track track : tracks) {

            if (track instanceof InOutTrack) {
                IntersectWrapper<InOutTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (InOutTrack) track, collector);
                futures.add(exe.submit(wrapper));
            } else if (track instanceof StrandTrack) {
                IntersectWrapper<StrandTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (StrandTrack) track, collector);
                futures.add(exe.submit(wrapper));
            } else if (track instanceof ScoredTrack) {
                IntersectWrapper<ScoredTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (ScoredTrack) track, collector);
                futures.add(exe.submit(wrapper));
            } else if (track instanceof NamedTrack) {
                IntersectWrapper<NamedTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (NamedTrack) track, collector);
                futures.add(exe.submit(wrapper));
            } else if (track instanceof DistanceTrack){
                DistanceWrapper dWrapper = new DistanceWrapper(measuredPositions, randomPositions, (DistanceTrack) track, collector);
                futures.add(exe.submit(dWrapper));
            }
        }

        ////////////  Tracks intersect ////////////////

        //////////// Logo ////////////////

        if(createLogo && measuredPositions.getAssembly().equals(Genome.Assembly.hg19)){
            String first = "";//TODO FIX measuredPositions.getFilename();
            LogoWrapper logoWrapper = new LogoWrapper(measuredPositions,collector, tracks.get(0).getAssembly(), first);
            futures.add(exe.submit(logoWrapper));

            //String second = randomPositions.getFilename();
            String second = "";//TODO FIX measuredPositions.getFilename();
            LogoWrapper logoWrapper2 = new LogoWrapper(randomPositions, collector, tracks.get(0).getAssembly(), second);
            futures.add(exe.submit(logoWrapper2));
        }

        //////////// Logo ////////////////

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
            EffectSize effectSize = new EffectSize();

            TestResult statTestResult = tester.test(result1, result2, track);
            effectSize.test(result1, result2);

            if(statTestResult !=null)
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

            Logo logo = LogoCreator.createLogo(genome.getSequence(assembly, measuredPos, width, count));
            logo.setName(name);

            collector.addLogo(logo);
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
            ScoredTrack hotspots = hotspot.findHotspots(measuredPositions, (int) (ChromosomSizes.getInstance().getGenomeSize(measuredPositions.getAssembly())/60));
            collector.addHotspot(hotspots);
        }
    }
}
