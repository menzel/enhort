package de.thm.genomeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class Intervals {

    /**
     * Intersect a list of intervals. Resulting interval has only starts/stop where all input interval were marked.
     * *
     * @param intervals - list of Interval Objects
     * @return interval object. Can be empty
     */
    public static Interval intersect(List<Interval> intervals){
        if(intervals.size() == 0){
            return null;

        } else if(intervals.size() == 1){
            return intervals.get(0);

        } else if(intervals.size() == 2){
            return intersect(intervals.get(0), intervals.get(1));

        }else{
            List<Interval> newList = new ArrayList<>();
            newList.addAll(intervals.subList(2, intervals.size()));

            newList.add(intersect(intervals.get(0), intervals.get(1)));

            return intersect(newList);
        }
    }

    /**
     * Intersect two intervals. Resulting interval has only starts/stop where both input interval were marked.
     *
     * @param intv1 - first input  interval object
     * @param intv2 - second input interval object
     * @return interval with marked intervals were both input intervals were marked.
     */
    public static Interval intersect(Interval intv1, Interval intv2){

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        Interval result = new Interval();
        result.setType(intv1.getType());
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

                if(i1 < starts1.size()-1 && ends2.get(i2) > starts1.get(i1+1))
                    break;
            }
        }

        result.setIntervalsStart(result_start);
        result.setIntervalsEnd(result_end);

        return result;
    }


    /**
     * Sums up a list of intervals. The result has a interval were any of the input intervals were marked
     *
     * @param intervals - list of intervals
     * @return interval with the sum of positions
     */
    public static Interval sum(List<Interval> intervals) {
        if(intervals.size() == 0){
            return null;

        } else if(intervals.size() == 1){
            return intervals.get(0);

        } else if(intervals.size() == 2){
            return sum(intervals.get(0), intervals.get(1));

        }else{
            List<Interval> newList = new ArrayList<>();
            newList.addAll(intervals.subList(2, intervals.size()));
            newList.add(sum(intervals.get(0), intervals.get(1)));

            return sum(newList);
        }
    }

    /**
     * Sums up two intervals. The result has a interval were any of the input intervals were marked
     *
     * @param intv1  - first interval for sum
     * @param intv2 - second interval for sum
     *
     * @return sum of intv1 and intv2
     */
    public static Interval sum(Interval intv1, Interval intv2) {
        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        Interval result = new Interval();
        result.setType(intv1.getType());
        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();

        int j = 0;
        int i = 0;

        long start;
        long end;

        for(; i < starts1.size(); i++){

            if(j > starts2.size() - 1)
                break;

            start = starts1.get(i);
            end = ends1.get(i);
            long nextStart = starts2.get(j);
            boolean s = false;
            boolean x = false;

            if(start > starts2.get(j)){
                nextStart = start;
                start = starts2.get(j);
                end = ends2.get(j);
                s = true;
                x = true;
            }

            while (end >= nextStart && i < starts1.size() - 1 && j < starts2.size() - 1){

                if (s) {
                    end = ends1.get(i);
                    i++;
                    nextStart = starts1.get(i);

                    s = false;

                } else {
                    end = ends2.get(j);
                    j++;
                    nextStart = starts2.get(j);

                    s = true;
                }
            }

            if(x){ //if the interval from start1(i) was omitted
                i--;
                j++;
            }

            if(!(result_end.size() > 1 && end < result_end.get(result_end.size()-1))){ // test for special case of omitted interval was complete in last interval
                result_start.add(start);
                result_end.add(end);
            }
        }

        //add remaining intervals
        for(; j < starts2.size(); j++){
            result_start.add(starts2.get(j));
            result_end.add(ends2.get(j));
        }

        for(; i < starts1.size(); i++){
            result_start.add(starts1.get(i));
            result_end.add(ends1.get(i));
        }


        result.setIntervalsStart(result_start);
        result.setIntervalsEnd(result_end);

        return result;
    }

    /**
     * Xor a list of intervals. The result has a interval were one of each was marked
     *
     * @param intervals - list of intervals
     * @return interval with the xor of positions
     */
    public static Interval xor(List<Interval> intervals) {
        if(intervals.size() == 0){
            return null;

        } else if(intervals.size() == 1){
            return intervals.get(0);

        } else if(intervals.size() == 2){
            return xor(intervals.get(0), intervals.get(1));

        }else{
            List<Interval> newList = new ArrayList<>();
            newList.addAll(intervals.subList(2, intervals.size()));
            newList.add(xor(intervals.get(0), intervals.get(1)));

            return xor(newList);
        }
    }

    /**
     * Xor of two intervals
     *
     * @param intv1 - first interval
     * @param intv2 - second interval
     *
     * @return xor(interval1, interval2)
     */
    public static Interval xor(Interval intv1, Interval intv2) {

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        Interval result = new Interval();
        result.setType(intv1.getType());
        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();
        long previousEnd = 0;

        int j = 0;
        int i = 0;

        for(; i < starts1.size(); i++){
            long start = starts1.get(i);
            long end = ends1.get(i);
            long nextStart;
            boolean s = false;

            if(start > starts2.get(j)){
                s = true;
            }

            if(s && j < starts2.size()-1) {
                nextStart = starts1.get(i);
                start = starts2.get(j);
                end = ends2.get(j++);

            } else {
                nextStart = starts2.get(j);
            }

            if(start < previousEnd){
                start = previousEnd;
            }

            previousEnd = end;

            if(end > nextStart){
                end = nextStart;
            }


            if(end != start){
                result_start.add(start);
                result_end.add(end);
            }

            if(s) i--;

        }

        result.setIntervalsStart(result_start);
        result.setIntervalsEnd(result_end);

        return result;
    }


}
