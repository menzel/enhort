package de.thm.genomeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class Intervals {

    public static Interval intersect(List<Interval> intervals){
        if(intervals.size() == 0){
            return null;

        } else if(intervals.size() == 1){
            return intervals.get(0);

        } else if(intervals.size() == 2){
            return intersect(intervals.get(0), intervals.get(1));

        }else{
            List<Interval> newList = intervals.subList(2, intervals.size());
            newList.add(intersect(intervals.get(0), intervals.get(1)));

            return intersect(newList);
        }
    }

    public static Interval intersect(Interval intv1, Interval intv2){

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        Interval result = new Interval();
        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();

        if(starts1.size() > starts2.size()){ //if start2 is smaller swap lists

            starts2 = intv1.getIntervalsStart();
            starts1 = intv2.getIntervalsStart();

            ends2 = intv1.getIntervalsEnd();
            ends1 = intv2.getIntervalsEnd();
        }

        // iterator through one of the intervals, check if a interval in the other track is overlapping with the current. if not proceed if yes create new interval
        int i2 = 0;

        for(int i1 = 0; i1 < starts1.size(); i1++){
            for(; i2 < ends2.size(); i2++){

                if(starts1.get(i1) < ends2.get(i2)){
                    if(starts2.get(i2) < ends1.get(i1)){
                        long start = Math.max(starts1.get(i1), starts2.get(i2));
                        long end = Math.min(ends1.get(i1), ends2.get(i2));

                        result_start.add(start);
                        result_end.add(end);

                    }
                }

                if(ends1.get(i1) < starts2.get(i2))
                    break;
            }
        }

        result.setIntervalsStart(result_start);
        result.setIntervalsEnd(result_end);

        return result;
    }


    public static Interval sum(List<Interval> intervals) {
        if(intervals.size() == 0){
            return null;

        } else if(intervals.size() == 1){
            return intervals.get(0);

        } else if(intervals.size() == 2){
            return sum(intervals.get(0), intervals.get(1));

        }else{
            List<Interval> newList = intervals.subList(2, intervals.size());
            newList.add(sum(intervals.get(0), intervals.get(1)));

            return intersect(newList);
        }
    }

    private static Interval sum(Interval intv1, Interval intv2) {
        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        Interval result = new Interval();
        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();


        int i2 = 0;

        for(int i1 = 0; i1 < starts1.size(); i1++) {
            for(; i2 < ends2.size(); i2++) {

                if(starts1.get(i1) < ends2.get(i2)){
                    if(starts2.get(i2) < ends1.get(i1)){
                        long start = Math.min(starts1.get(i1), starts2.get(i2));
                        long end = Math.max(ends1.get(i1), ends2.get(i2));

                        result_start.add(start);
                        result_end.add(end);
                    }
                }

                if(ends1.get(i1) < starts2.get(i2))
                    break;
            }
        }

        result.setIntervalsStart(result_start);
        result.setIntervalsEnd(result_end);

        return result;
    }
}
