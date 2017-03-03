package de.thm.calc;

import de.thm.genomeData.*;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.logo.LogoCreator;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import de.thm.stat.EffectSize;
import de.thm.stat.IndependenceTest;
import de.thm.stat.ResultCollector;
import de.thm.stat.TestResult;

import java.util.List;
import java.util.concurrent.*;

/**
 * Implements multithreading of the intersect call.
 * Increase thread count on biggger machines.
 * <p>
 * Created by Michael Menzel on 11/1/16.
 */
public final class CalcCaller {
    private static final int threadCount;

    static {
        if(System.getenv("HOME").contains("menzel")) {
            threadCount = 16; //local
        } else {
            threadCount = 64; //remote
        }
    }
    private final ExecutorService exe;

    public CalcCaller() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1024);
        exe = new ThreadPoolExecutor(4, threadCount, 30L, TimeUnit.SECONDS, queue);
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

        ResultCollector collector = new ResultCollector(randomPositions, tracks.get(0).getAssembly()); // get assembly from the first track

        for (Track track : tracks) {

            if (track instanceof InOutTrack) {
                IntersectWrapper<InOutTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (InOutTrack) track, collector);
                exe.execute(wrapper);
            } else if (track instanceof ScoredTrack) {
                IntersectWrapper<ScoredTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (ScoredTrack) track, collector);
                exe.execute(wrapper);
            } else if (track instanceof NamedTrack) {
                IntersectWrapper<NamedTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (NamedTrack) track, collector);
                exe.execute(wrapper);
            } else if (track instanceof DistanceTrack){
                DistanceWrapper dWrapper = new DistanceWrapper(measuredPositions, randomPositions, (DistanceTrack) track, collector);
                exe.execute(dWrapper);
            }
        }

        ////////////  Tracks intersect ////////////////

        //////////// Logo ////////////////

        if(createLogo && measuredPositions.getAssembly().equals(GenomeFactory.Assembly.hg19)){
            LogoWrapper logoWrapper = new LogoWrapper(measuredPositions,collector, tracks.get(0).getAssembly(), "user data");
            exe.execute(logoWrapper);

            LogoWrapper logoWrapper2 = new LogoWrapper(randomPositions, collector, tracks.get(0).getAssembly(), "random");
            exe.execute(logoWrapper2);
        }

        //////////// Logo ////////////////

        //////////// Hotspots ////////////////
        HotspotWrapper hotspotWrapper = new HotspotWrapper(measuredPositions, collector);
        exe.execute(hotspotWrapper);

        //////////// Hotspots ////////////////


        exe.shutdown();

        try {
            exe.awaitTermination(2, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            e.printStackTrace();
            exe.shutdownNow();
        } finally {
            if(!exe.isTerminated())
                System.err.println("Killing all tasks now");
            exe.shutdownNow();
        }

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
        private final GenomeFactory.Assembly assembly;
        private String name;

        /**
         * Constructor for the wrapper object
         *  @param measuredPos - positions from the outside of the program
         * @param collector   - collector to collect results in
         * @param name -  filename or random
         */
        private LogoWrapper(Sites measuredPos, ResultCollector collector, GenomeFactory.Assembly assembly, String name) {

            this.measuredPos = measuredPos;
            this.collector = collector;
            this.assembly = assembly;
            this.name = name;
        }

        @Override
        public void run() {
            GenomeFactory genome = GenomeFactory.getInstance();
            int width = 8;//TODO use user set value
            int count = 300;//TODO use user set value

            Logo logo = LogoCreator.createLogo(genome.getSequence(assembly, measuredPos, width, count));
            logo.setName(name);

            collector.addLogo(logo);
        }
    }

    private class HotspotWrapper implements Runnable{
        private Sites measuredPositions;
        private ResultCollector collector;

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
