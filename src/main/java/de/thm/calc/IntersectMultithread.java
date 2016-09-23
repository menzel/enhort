package de.thm.calc;

import de.thm.genomeData.*;
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
public final class IntersectMultithread {

    private static final int threadCount = 32;
    private final ExecutorService exe;

    public IntersectMultithread() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1024);
        exe = new ThreadPoolExecutor(4, threadCount, 5L, TimeUnit.SECONDS, queue);
    }


    /**
     * Executes the intersect algorithm with two sets of sites on a given map of intervals.
     * *
     *
     * @param intervals         map of intervals with <K,V> <Name, Interval reference>
     * @param measuredPositions - positions supplied from outside
     * @param randomPositions   - positions created by a background model
     * @return Collector of all results computed by the differen threads
     */
    public ResultCollector execute(List<Track> intervals, Sites measuredPositions, Sites randomPositions) {


        ResultCollector collector = new ResultCollector(randomPositions);

        for (Track track : intervals) {

            if (track instanceof InOutTrack) {
                IntersectWrapper<InOutTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (InOutTrack) track, collector);
                exe.execute(wrapper);

                DistanceWrapper dWrapper = new DistanceWrapper(measuredPositions, randomPositions, (InOutTrack) track, collector);
                exe.execute(dWrapper);

            } else if (track instanceof ScoredTrack) {
                IntersectWrapper<ScoredTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (ScoredTrack) track, collector);
                exe.execute(wrapper);
            } else if (track instanceof NamedTrack) {
                IntersectWrapper<NamedTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, (NamedTrack) track, collector);
                exe.execute(wrapper);
            }
        }


        exe.shutdown();


        try {
            exe.awaitTermination(20, TimeUnit.SECONDS);

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
        private final Track track;
        private final ResultCollector collector;

        /**
         * Constructor for the wrapper object
         *
         * @param measuredPos - positions from the outside of the program
         * @param randomPos   - positions to match against made up by a background model
         * @param track       - interval to match against
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

            TestTrackResult result1 = intersec1.searchTrack((T) track, measuredPos);
            TestTrackResult result2 = intersec2.searchTrack((T) track, randomPos);

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
        private final InOutTrack track;
        private final ResultCollector collector;

        /**
         * Constructor for the wrapper object
         *
         * @param measuredPos - positions from the outside of the program
         * @param randomPos   - positions to match against made up by a background model
         * @param track       - interval to match against
         * @param collector   - collector to collect results in
         */
        private DistanceWrapper(Sites measuredPos, Sites randomPos, InOutTrack track, ResultCollector collector) {

            this.randomPos = randomPos;
            this.measuredPos = measuredPos;

            // create a copy of the track for the results, so that the names do not interfere later:
            this.track = TrackFactory.getInstance().createInOutTrack(track.getIntervalsStart(), track.getIntervalsEnd(), "Distance to " +track.getName(), track.getDescription());

            this.collector = collector;
        }

        @Override
        public void run() {
            TestTrack<InOutTrack> dist1 = new Distances();
            TestTrack<InOutTrack> dist2 = new Distances();

            TestTrackResult result1 = dist1.searchTrack(track, measuredPos);
            TestTrackResult result2 = dist2.searchTrack(track, randomPos);

            IndependenceTest tester = new IndependenceTest();


            TestResult statTestResult = tester.testScoredTrack(result1, result2, track);

            collector.addResult(statTestResult);

        }
    }


}
