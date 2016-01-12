package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Michael Menzel on 11/1/16.
 */
public class IntersectMultithread {

    ExecutorService exe = Executors.newFixedThreadPool(4);
    List<IntersectWrapper> wrappers = new ArrayList<>();

    public IntersectMultithread(Map<String, Interval> intervals, Sites measuredPositions, Sites randomPositions) {

        Set<String> tracks = intervals.keySet();

        for(String track: tracks){

            IntersectWrapper wrapper = new IntersectWrapper(measuredPositions, randomPositions, intervals.get(track), track);
            wrappers.add(wrapper);
            exe.execute(wrapper);
        }

        exe.shutdown();

        //for(IntersectWrapper wrapper: wrappers){ System.out.println(wrapper.getTestResult().toString()); }

    }
}
