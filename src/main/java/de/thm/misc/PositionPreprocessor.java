package de.thm.misc;

import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.TrackFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data preprocessor for merging overlaping intervals
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
public final class PositionPreprocessor {


    /**
     * Preprocesses data. Join intervals which cover the same positions.
     *
     * @param interval to process
     */
    public static InOutTrack preprocessData(InOutTrack interval) {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();

        List<Long> intervalsStart = interval.getIntervalsStart();
        List<Long> intervalsEnd = interval.getIntervalsEnd();

        if (intervalsStart.isEmpty()) return interval;

        long start = intervalsStart.get(0);
        long end = intervalsEnd.get(0);

        List<Long> tmp = new ArrayList<>(intervalsStart);
        Collections.sort(tmp);
        if(!intervalsStart.equals(tmp)){
            System.err.println("Interval was not sorted. Uses ./sort_bed script first");
            return null;
        }

        for (int i = 0; i < intervalsStart.size(); i++) {

            if (i < intervalsStart.size() - 1 && end >= intervalsStart.get(i + 1)) { // overlap

                if (end < intervalsEnd.get(i + 1))
                    end = intervalsEnd.get(i + 1);

            } else {  //do not overlap
                newStart.add(start);
                newEnd.add(end);

                if (i >= intervalsStart.size() - 1) break; // do not get next points if this was the last

                start = intervalsStart.get(i + 1);
                end = intervalsEnd.get(i + 1);

            }
        }

        intervalsStart.clear();
        intervalsEnd.clear();

        return TrackFactory.getInstance().createInOutTrack(newStart, newEnd, interval.getName(), interval.getDescription());
    }

}
