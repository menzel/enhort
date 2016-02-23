package de.thm.genomeData;

import de.thm.misc.ChromosomSizes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Collection of utils for interval objects.
 *
 * Created by Michael Menzel on 13/1/16.
 */
public class Intervals {

    //prevent init of Intervals object with private constructor
    private Intervals(){}

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
     *
     * @return interval with marked intervals were both input intervals were marked. Type is set to inout, names and scores get lost in intersect
     *
     */
    public static Interval intersect(Interval intv1, Interval intv2){

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        Interval result = new Interval();
        //result.setType(intv1.getType());
        result.setType(Interval.Type.inout); // scores and names are lost


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
        return intersect(intv1.invert(), intv2.invert()).invert();
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

        //return result;
        return null;
    }


    /**
     * Sums up the size of all intervals. Either all intervals or the space between them
     *
     * @param interval - intervals to sum up
     * @param mode - either "in" or "out".
     *
     * @return sum of interval length inside or outside the intervals
     */
    public static long sumOfIntervals(Interval interval, String mode) {

        long size = 0;
        int io = (mode.equals("in"))? 0: 1;

        List<Long> intervalStart = interval.getIntervalsStart();
        List<Long> intervalEnd = interval.getIntervalsEnd();

        for(int i = 0; i < intervalStart.size()-io; i++){
            if(mode.equals("in"))
                size += intervalEnd.get(i) - intervalStart.get(i);
            else
                size +=  intervalStart.get(i+1) - intervalEnd.get(i);
        }

        return size;
    }

    public static Interval subsetScore(Interval interval, double score) {
        Interval n = new Interval();

        List<Long> intervalStart = new ArrayList<>();
        List<Long> intervalEnd = new ArrayList<>();

        List<Double> intervalScore = interval.getIntervalScore();
        List<Double> intervalScore_n = new ArrayList<>();

        for (int i = 0; i < intervalScore.size(); i++) {

            if (intervalScore.get(i) == score) {
                intervalStart.add(interval.getIntervalsStart().get(i));
                intervalEnd.add(interval.getIntervalsEnd().get(i));
                intervalScore_n.add(score);
            }
        }

        n.setIntervalScore(intervalScore_n);
        n.setIntervalsStart(intervalStart);
        n.setIntervalsEnd(intervalEnd);

        return n;

    }

    public static Interval combine(List<Interval> intervals, Map<String, Double> score_map) {
        if(intervals.size() == 0){
            return null;

        } else if(intervals.size() == 1){
            return null; // TODO method to set score for one interval

        } else if(intervals.size() == 2){
            return combine(intervals.get(0), intervals.get(1), score_map);

        }else{
            List<Interval> newList = new ArrayList<>();
            newList.addAll(intervals.subList(2, intervals.size()));

            newList.add(combine(intervals.get(0), intervals.get(1), score_map));

            return combine(newList, score_map);
        }

    }

    /**
     * Combines two intervals to a probability interval.
     * Probablities are given in a map with (k,v): (score, probability)
     *
     * @param intv1
     * @param intv2
     * @param score_map
     * @return
     */
    public static Interval combine(Interval intv1, Interval intv2, Map<String, Double> score_map) {

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        List<Double> scores1 = intv1.getIntervalScore();
        List<Double> scores2 = intv2.getIntervalScore();

        Interval result = new Interval();
        result.setType(intv1.getType());
        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();
        List<Double> result_score = new ArrayList<>();
        List<String> result_names = new ArrayList<>();

        if(starts1.size() > starts2.size()){ //if start2 is smaller swap lists

            starts2 = intv1.getIntervalsStart();
            starts1 = intv2.getIntervalsStart();

            ends2 = intv1.getIntervalsEnd();
            ends1 = intv2.getIntervalsEnd();

            scores1 = intv2.getIntervalScore();
            scores2 = intv1.getIntervalScore();
        }


        int i2 = 0;
        int i1 = 0;

        result_start.add(0L);

        while(i1 < starts1.size()){
            while(i2 < ends2.size()){
                long s1 = Long.MAX_VALUE;
                long e1 = Long.MAX_VALUE;

                if(i1 < starts1.size()){
                    s1 = starts1.get(i1);
                    e1 = ends1.get(i1);
                }

                long e2 = ends2.get(i2);
                long s2 = starts2.get(i2);

                if(s1 < s2 && e1 < s2) {// no overlap, interval from 1 is next
                    result_score.add(score_map.get("||"));
                    result_names.add("||");
                    result_end.add(s1);

                    result_start.add(s1);
                    result_end.add(e1);

                    result_start.add(e1);

                    String ref = "|".concat(scores1.get(i1).toString()).concat("|");
                    result_score.add(score_map.get(ref));
                    result_names.add(ref);

                    i1++;
                } else if(s1 > s2 && e2 < s1) { //no overlap, interval from 2 comes first

                    result_score.add(score_map.get("||"));
                    result_names.add("||");
                    result_end.add(s2);

                    result_start.add(s2);
                    result_end.add(e2);

                    result_start.add(e2);

                    String ref = "||".concat(scores2.get(i2).toString());
                    result_score.add(score_map.get(ref));
                    result_names.add(ref);

                    i2++;
                } else if((s1 < e2 && e1 > e2) || s2 < e1 && e2 > e1){ //overlap

                    //outside part
                    result_score.add(score_map.get("||"));
                    result_names.add("||");
                    result_end.add(s1);

                    //first part
                    result_start.add(s1);
                    result_end.add(s2);
                    String ref = "|".concat(scores1.get(i1).toString()).concat("|");
                    result_score.add(score_map.get(ref));
                    result_names.add(ref);

                    //overlapping part
                    result_start.add(s2);
                    result_end.add(e1);
                    String refm = "|".concat(scores1.get(i1).toString()).concat("|").concat(scores2.get(i2).toString());
                    result_score.add(score_map.get(refm));
                    result_names.add(refm);

                    //second part
                    result_start.add(e1);
                    result_end.add(e2);
                    String ref2 = "||".concat(scores2.get(i2).toString());
                    result_score.add(score_map.get(ref2));
                    result_names.add(ref2);


                    //outside part
                    result_start.add(e2);

                    i1++;
                    i2++;

                }  else { //second interval is inside the first

                    //outside part
                    result_score.add(score_map.get("||"));
                    result_names.add("||");
                    result_end.add(s1);

                    if(s1 != s2) {
                        result_start.add(s1);
                        result_end.add(s2);
                        String ref = "|".concat(scores1.get(i1).toString()).concat("|");
                        result_score.add(score_map.get(ref));
                        result_names.add(ref);
                    }

                    //overlapping part
                    result_start.add(s2);
                    result_end.add(e2);
                    String refm = "|".concat(scores1.get(i1).toString()).concat("|").concat(scores2.get(i2).toString());
                    result_score.add(score_map.get(refm));
                    result_names.add(refm);

                    if(e1 != e2) {
                        result_start.add(e2);
                        result_end.add(e1);
                        String ref2 = "||".concat(scores2.get(i2).toString());
                        result_score.add(score_map.get(ref2));
                        result_names.add(ref2);
                    }

                    //outside part

                    result_start.add(e1);

                    i1++;
                    i2++;
                }
            }
        }

        result_score.add(score_map.get("||"));
        result_names.add("||");
        result_end.add(ChromosomSizes.getInstance().getGenomeSize());

        result.setIntervalsStart(result_start);
        result.setIntervalsEnd(result_end);
        result.setIntervalScore(result_score);
        result.setIntervalName(result_names);

        return result;
    }

    public static Interval convertToScore(Interval interval) {
        return null;
    }
}
