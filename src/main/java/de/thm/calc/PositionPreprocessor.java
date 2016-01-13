package de.thm.calc;

import de.thm.genomeData.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Data preprocessor for merging overlaping intervals
 *
 * Created by Michael Menzel on 8/12/15.
 */
public class PositionPreprocessor {


    /**
    * Preprocesses data. Join intervals which cover the same positions.
    *
    * @param interval to process
    */
    public static void preprocessData(Interval interval) {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();
        List<String> newName = new ArrayList<>();
        List<Long> newScore = new ArrayList<>();

        List<Long> intervalsStart = interval.getIntervalsStart();
        List<Long> intervalsEnd = interval.getIntervalsEnd();
        List<String> intervalName = interval.getIntervalName();
        List<Long> intervalsScore = interval.getIntervalScore();

        if(intervalsStart.isEmpty()) return; long start = intervalsStart.get(0);
        long end = intervalsEnd.get(0);

        String name = intervalName.get(0);
        long score = 0;
        int count = 0;


        for (int i = 0; i < intervalsStart.size(); i++) {

            if(i < intervalsStart.size()-1 && end > intervalsStart.get(i+1)) { // overlap

                if(end < intervalsEnd.get(i+1)){
                    end = intervalsEnd.get(i+1);
                }
                score += intervalsScore.get(i);
                count++;

            }else{  //do not overlap
                newStart.add(start);
                newEnd.add(end);
                newName.add(name);
                newScore.add((count != 0)?score/count : 0);

                if(i >= intervalsStart.size()-1) break; // do not get next points if this was the last

                start = intervalsStart.get(i+1);
                end = intervalsEnd.get(i+1);
                name = intervalName.get(i+1);

                score = 0;
                count = 0;

            }
        }

        intervalsStart.clear();
        interval.setIntervalsStart(newStart);

        intervalsEnd.clear();
        interval.setIntervalsEnd(newEnd);

        intervalName.clear();
        interval.setIntervalName(newName);
    }

}
