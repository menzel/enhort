package de.thm.calc;

import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.NamedTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import de.thm.positionData.Sites;
import de.thm.stat.ResultCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implements multithreading of the intersect call.
 * Increase thread count on biggger machines.
 * <p>
 * Created by Michael Menzel on 11/1/16.
 */
public final class IntersectMultithread {

    private static final int threadCount = 8;
    private final ExecutorService exe;
    private final List<IntersectWrapper> wrappers;

    public IntersectMultithread() {
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
                IntersectWrapper<InOutTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, track, collector);
                wrappers.add(wrapper);
                exe.execute(wrapper);

            } else if (track instanceof ScoredTrack) {
                IntersectWrapper<ScoredTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, track, collector);
                wrappers.add(wrapper);
                exe.execute(wrapper);
            } else if (track instanceof NamedTrack) {
                IntersectWrapper<NamedTrack> wrapper = new IntersectWrapper<>(measuredPositions, randomPositions, track, collector);
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
}
