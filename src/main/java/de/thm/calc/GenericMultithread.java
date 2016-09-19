package de.thm.calc;

import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.NamedTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import de.thm.positionData.Sites;
import de.thm.stat.EffectSize;
import de.thm.stat.ResultCollector;
import de.thm.stat.Test;
import de.thm.stat.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Skeletal class for multithreading of any testTrack call.
 * Increase thread count on biggger machines.
 * <p>
 * Created by Michael Menzel on 11/1/16.
 */
public final class GenericMultithread<T extends Track> {


    private static final int threadCount = 8;
    private final ExecutorService exe;
    private final List<Wrapper> wrappers;
    private final TestTrack<T> testToDo;
    private final Test statTest;

    public GenericMultithread(TestTrack<T> testToDo, Test statTest) {
        this.testToDo = testToDo;
        this.statTest = statTest;
        exe = Executors.newFixedThreadPool(threadCount);
        wrappers = new ArrayList<>();
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
                Wrapper wrapper = new Wrapper(measuredPositions, randomPositions, track, collector);
                wrappers.add(wrapper);
                exe.execute(wrapper);

            } else if (track instanceof ScoredTrack) {
                Wrapper wrapper = new Wrapper(measuredPositions, randomPositions, track, collector);
                wrappers.add(wrapper);
                exe.execute(wrapper);
            } else if (track instanceof NamedTrack) {
                Wrapper wrapper = new Wrapper(measuredPositions, randomPositions, track, collector);
                wrappers.add(wrapper);
                exe.execute(wrapper);
            }
        }


        exe.shutdown();


        try {
            exe.awaitTermination(20, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return collector;
    }

    private final class Wrapper implements Runnable {


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
        private Wrapper(Sites measuredPos, Sites randomPos, Track track, ResultCollector collector) {

            this.randomPos = randomPos;
            this.measuredPos = measuredPos;
            this.track = track;
            this.collector = collector;
        }

        @Override
        public void run() {

            TestTrackResult result1 = testToDo.searchTrack((T) track, measuredPos);
            TestTrackResult result2 = testToDo.searchTrack((T) track, randomPos);

            EffectSize effectSize = new EffectSize();

            TestResult statTestResult = statTest.test(result1, result2, track);
            effectSize.test(result1, result2);

            collector.addResult(statTestResult);

        }

    }


}
