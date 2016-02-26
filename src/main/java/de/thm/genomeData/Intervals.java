package de.thm.genomeData;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.misc.ChromosomSizes;

import java.util.*;

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
    public static InOutInterval intersect(Interval intv1, Interval intv2){

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        //result.setType(intv1.getType());
        //result.setType(Interval.Type.inout); // scores and names are lost


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

        String name = intv1.getName() + "_" + intv2.getName();
        String desc = intv1.getDescription() + "_" + intv2.getDescription();

        return new InOutInterval(result_start, result_end, name, desc);
    }


    /**
     * Sums up a list of intervals. The result has a interval were any of the input intervals were marked
     *
     * @param intervals - list of intervals
     * @return interval with the sum of positions
     */
    public static Interval sum(List<Interval> intervals) throws IntervalTypeNotAllowedExcpetion {

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
    public static Interval sum(Interval intv1, Interval intv2) throws IntervalTypeNotAllowedExcpetion {
        return invert(intersect(invert(intv1), invert(intv2)));
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
    public static InOutInterval xor(Interval intv1, Interval intv2) {

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

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

        String name = intv1.getName() + "_" + intv2.getName();
        String desc = intv1.getDescription() + "_" + intv2.getDescription();

        return new InOutInterval(result_start, result_end, name, desc);
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

    public static Interval subsetScore(ScoredTrack interval, double score) {

        List<Long> intervalStart = new ArrayList<>();
        List<Long> intervalEnd = new ArrayList<>();

        List<Double> intervalScore = interval.getIntervalScore();
        List<Double> intervalScore_n = new ArrayList<>();

        List<String> names = new ArrayList<>();

        for (int i = 0; i < intervalScore.size(); i++) {

            if (intervalScore.get(i) == score) {
                intervalStart.add(interval.getIntervalsStart().get(i));
                intervalEnd.add(interval.getIntervalsEnd().get(i));
                intervalScore_n.add(score);
                names.add(interval.getIntervalName().get(i));
            }
        }

        return new ScoredTrack(intervalStart, intervalEnd, names, intervalScore_n, interval.getName(), interval.getDescription());
    }


    /**
     * Combines a list of intervals to a probability interval.
     * Probablities are given in a map with (k,v): (score, probability)
     *
     * The intervals between the given intervals are also filled with scores.
     *
     * @param intervals - list of intervals
     * @param score_map - map of score names to probabilities. The score names should match the scores in intv1 and intv2
     *
     * @return Interval of type GenomeInterval
     */
    public static ScoredTrack combine(List<ScoredTrack> intervals, Map<String, Double> score_map) {
        if(intervals.size() == 0){
            return null;

        } else if(intervals.size() == 1){
            return combine(intervals.get(0), score_map);

        } else if(intervals.size() == 2){
            return combine(intervals.get(0), intervals.get(1), score_map);

        }else{
            List<ScoredTrack> newList = new ArrayList<>();
            newList.addAll(intervals.subList(2, intervals.size()));

            newList.add(combine(intervals.get(0), intervals.get(1), score_map));

            return combine(newList, score_map);
        }

    }

    private static ScoredTrack combine(ScoredTrack inputInterval, Map<String, Double> score_map) {

        InOutInterval tmp = invert(inputInterval.clone());

        //convert outsider interval to scored interval with specific score value
        List<Double> outsideProb = new ArrayList<>(Collections.nCopies(tmp.getIntervalsStart().size(), score_map.get("|")));
        List<String> outsideNames = new ArrayList<>(Collections.nCopies(tmp.getIntervalsStart().size(), ""));

        ScoredTrack outsideInterval = new ScoredTrack(tmp.getIntervalsStart(),
                    tmp.getIntervalsEnd(),
                    outsideNames, outsideProb,
                    "outside_" + inputInterval.getName(),
                    "outside_of_"+ inputInterval.getDescription());

        Map<String, Double> newMap = new HashMap<>(score_map.size());

        //convert score map to have values for dual interval list
        for(String key: score_map.keySet()){
            double value = score_map.get(key);
            newMap.put(key.concat("|"), value);
        }

        //do default combine
        return combine(outsideInterval, inputInterval, newMap);
    }

    /**
     * Combines two intervals to a probability interval.
     * Probablities are given in a map with (k,v): (score, probability)
     *
     * The intervals between the given intervals are also filled with scores.
     *
     * @param intv1 - first interval to combine
     * @param intv2 - second interval to combine
     * @param score_map - map of score names to probabilities. The score names should match the scores in intv1 and intv2
     *
     * @return Interval of type GenomeInterval
     */
    public static ScoredTrack combine(ScoredTrack intv1, ScoredTrack intv2, Map<String, Double> score_map) {

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        List<Double> scores1 = intv1.getIntervalScore();
        List<Double> scores2 = intv2.getIntervalScore();

        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();
        List<Double> result_score = new ArrayList<>();
        List<String> result_names = new ArrayList<>();

        long genomeSize = ChromosomSizes.getInstance().getGenomeSize();


        int i2 = 0;
        int i1 = 0;

        if(intv1.getIntervalsStart().get(0) != 0L)
            result_start.add(0L);

        while(i1 < starts1.size()){

            while(true){
                long s1 = genomeSize;
                long e1 = genomeSize;

                long e2 = genomeSize;
                long s2 = genomeSize;


                if(i1 < starts1.size()){
                    s1 = starts1.get(i1);
                    e1 = ends1.get(i1);
                }

                if(i2 < starts2.size()){
                    e2 = ends2.get(i2);
                    s2 = starts2.get(i2);
                }


                if(s1 == genomeSize && s1 == s2 && e1 == genomeSize && e1 == e2){
                    //break out of both loops because this is the last iteration. both intervals are equals genome size
                    i1 = Integer.MAX_VALUE;
                    break;
                }

                result_score.add(score_map.get("||"));
                result_names.add("||");

                if(s1 < s2 && e1 <= s2) {// no overlap, interval from 1 is next
                    result_end.add(s1);

                    result_start.add(s1);
                    result_end.add(e1);

                    result_start.add(e1);

                    String ref = "|".concat(scores1.get(i1).toString()).concat("|");
                    result_score.add(score_map.get(ref));
                    result_names.add(ref);

                    i1++;
                } else if(s1 > s2 && e2 <= s1) { //no overlap, interval from 2 comes first

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


        if(result_end.get(result_end.size() - 1) != genomeSize){
            result_score.add(score_map.get("||"));
            result_names.add("||");
            result_end.add(genomeSize);
        }

        //set null values to 0.0
        result_score.stream().filter(val -> val == null).forEach(val -> val = 0.0);


        String name = intv1.getName() + "_" + intv2.getName();
        String desc = intv1.getDescription() + "_" + intv2.getDescription();
        return new ScoredTrack(result_start, result_end, result_names, result_score,name, desc);
    }


    /**
     * Inverts interval. Scored and named intervals loose their Type because scores and names cannot be kept.
     *
     * @param interval - interval to invert
     * @return inverted interval
     */
    public static InOutInterval invert(Interval interval) {

        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();


        if(interval.getIntervalsStart().size() == 0)
            return cast(interval.clone());



        starts = new ArrayList<>(interval.getIntervalsEnd());
        ends = new ArrayList<>(interval.getIntervalsStart());

        if(interval.getIntervalsStart().get(0) != 0L) {
            starts.add(0, 0L);
        } else {
            ends.remove(0);
        }

        if(interval.getIntervalsEnd().get(interval.getIntervalsEnd().size()-1) == ChromosomSizes.getInstance().getGenomeSize()) {
            starts.remove(starts.size()-1);

        } else {
            ends.add(ChromosomSizes.getInstance().getGenomeSize());
        }


        return new InOutInterval(starts,ends,interval.getName(),interval.getDescription());
    }

    private static InOutInterval cast(Interval track) {
        return new InOutInterval(track.getIntervalsStart(), track.getIntervalsEnd(), track.getName(), track.getDescription());
    }


    /**
     * Converts a non score interval to a scored interval with score 1.0 for each interval.
     *
     * @param interval input interval
     *
     * @return intervals of type score with score values
     */
    public static ScoredTrack cast(InOutInterval interval) {

        List<Double> scores = new ArrayList<>(Collections.nCopies(interval.getIntervalsStart().size(), 1.0));
        List<String> names = new ArrayList<>(Collections.nCopies(interval.getIntervalsStart().size(), ""));

        return new ScoredTrack(interval.getIntervalsStart(), interval.getIntervalsEnd(), names, scores, interval.getName(), interval.getDescription());
    }
}
