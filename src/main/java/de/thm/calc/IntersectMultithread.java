package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Michael Menzel on 11/1/16.
 */
public class IntersectMultithread {

    ExecutorService exe = Executors.newFixedThreadPool(8);
    List<IntersectWrapper> wrappers = new ArrayList<>();

    public IntersectMultithread(Map<String, Interval> intervals, Sites measuredPositions, Sites randomPositions) {

        Set<String> tracks = intervals.keySet();

        for(String track: tracks){

            IntersectWrapper wrapper = new IntersectWrapper(measuredPositions, randomPositions, intervals.get(track), track);
            wrappers.add(wrapper);
            exe.execute(wrapper);
        }

        exe.shutdown();

        try {
            exe.awaitTermination(1, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
