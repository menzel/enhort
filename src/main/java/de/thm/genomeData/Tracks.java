package de.thm.genomeData;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collection of utils for track objects.
 * <p>
 * Created by Michael Menzel on 13/1/16.
 */
public final class Tracks {

    //prevent init of Intervals object with private constructor
    private Tracks() {
    }


    /**
     * Bins the scores of a given track.
     * The count value defines the number of bins where values are put into.
     * Bin-borders and values are set by the 100/count percent percentiles.
     *
     * @param track - scored track to bin
     * @param count - counts of bins
     *
     * @return scored track with binned scores
     */
    public static void bin(ScoredTrack track, int count){

        Percentile percentile = new Percentile();
        //use method R 3 to set real values as bin bounds
        percentile = percentile.withEstimationType(Percentile.EstimationType.R_3);

        List<Double> scores = track.getIntervalScore();
        double[] values = scores.stream().mapToDouble(d -> d).toArray();
        Arrays.sort(values);

        double lowerBound = 0.0;

        for(int i = 100/count; i <= 100; i+= 100/count){
            double upperBound = percentile.evaluate(values, i);

            for (int j = 0; j < scores.size(); j++) {
                Double score = scores.get(j);

                if (score > lowerBound && score <= upperBound) {
                    scores.set(j, upperBound);
                }
            }

            lowerBound = upperBound;
        }
    }



    /**
     * TestTrack a list of intervals. Resulting track has only starts/stop where all input track were marked.
     * *
     *
     * @param tracks - list of Interval Objects
     * @return track object. Can be empty
     */
    public static Track intersect(List<Track> tracks) {
        if (tracks.size() == 0) {
            return null;

        } else if (tracks.size() == 1) {
            return tracks.get(0);

        } else if (tracks.size() == 2) {
            return intersect(tracks.get(0), tracks.get(1));

        } else {
            List<Track> newList = new ArrayList<>();
            newList.addAll(tracks.subList(2, tracks.size()));

            newList.add(intersect(tracks.get(0), tracks.get(1)));

            return intersect(newList);
        }
    }

    /**
     * TestTrack two intervals. Resulting track has only starts/stop where both input track were marked.
     *
     * @param intv1 - first input  track object
     * @param intv2 - second input track object
     * @return track with marked intervals were both input intervals were marked. Type is set to inout, names and scores get lost in intersect
     */
    public static InOutTrack intersect(Track intv1, Track intv2) {

        List<Long> starts1 = intv1.getStarts();
        List<Long> starts2 = intv2.getStarts();

        List<Long> ends1 = intv1.getEnds();
        List<Long> ends2 = intv2.getEnds();

        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();

        if (starts1.size() > starts2.size()) { //if start2 is smaller swap lists

            starts2 = intv1.getStarts();
            starts1 = intv2.getStarts();

            ends2 = intv1.getEnds();
            ends1 = intv2.getEnds();
        }

        // iterator through one of the intervals, check if a track in the other track is overlapping with the current. if not proceed if yes create new track
        int i2 = 0;

        for (int i1 = 0; i1 < starts1.size(); i1++) {
            for (; i2 < ends2.size(); i2++) {

                if (starts1.get(i1) < ends2.get(i2)) {
                    if (starts2.get(i2) < ends1.get(i1)) {
                        long start = Math.max(starts1.get(i1), starts2.get(i2));
                        long end = Math.min(ends1.get(i1), ends2.get(i2));

                        result_start.add(start);
                        result_end.add(end);

                    }
                }

                if (i1 < starts1.size() - 1 && ends2.get(i2) > starts1.get(i1 + 1))
                    break;
            }
        }

        String name = intv1.getName() + "_" + intv2.getName();
        String desc = intv1.getDescription() + "_" + intv2.getDescription();

        return new InOutTrack(result_start, result_end, name, desc, intv1.getAssembly(), intv1.getCellLine());
    }


    /**
     * Sums up a list of intervals. The result has a track were any of the input intervals were marked
     *
     * @param tracks - list of intervals
     * @return track with the sum of positions
     */
    public static Track sum(List<Track> tracks) throws IntervalTypeNotAllowedExcpetion {

        if (tracks.size() == 0) {
            return null;

        } else if (tracks.size() == 1) {
            return tracks.get(0);

        } else if (tracks.size() == 2) {
            return sum(tracks.get(0), tracks.get(1));

        } else {
            List<Track> newList = new ArrayList<>();
            newList.addAll(tracks.subList(2, tracks.size()));
            newList.add(sum(tracks.get(0), tracks.get(1)));

            return sum(newList);
        }
    }

    /**
     * Sums up two intervals. The result has a track were any of the input intervals were marked
     *
     * @param intv1 - first track for sum
     * @param intv2 - second track for sum
     * @return sum of intv1 and intv2
     */
    public static Track sum(Track intv1, Track intv2) throws IntervalTypeNotAllowedExcpetion {
        return invert(intersect(invert(intv1), invert(intv2)));
    }

    /**
     * Xor a list of intervals. The result has a track were one of each was marked
     *
     * @param tracks - list of intervals
     * @return track with the xor of positions
     */
    @SuppressWarnings("unused")
    public static Track xor(List<Track> tracks) {
        if (tracks.size() == 0) {
            return null;

        } else if (tracks.size() == 1) {
            return tracks.get(0);

        } else if (tracks.size() == 2) {
            return xor(tracks.get(0), tracks.get(1));

        } else {
            List<Track> newList = new ArrayList<>();
            newList.addAll(tracks.subList(2, tracks.size()));
            newList.add(xor(tracks.get(0), tracks.get(1)));

            return xor(newList);
        }
    }

    /**
     * Xor of two intervals
     *
     * @param intv1 - first track
     * @param intv2 - second track
     * @return xor(interval1, interval2)
     */
    public static InOutTrack xor(Track intv1, Track intv2) {

        List<Long> starts1 = intv1.getStarts();
        List<Long> starts2 = intv2.getStarts();

        List<Long> ends1 = intv1.getEnds();
        List<Long> ends2 = intv2.getEnds();

        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();
        long previousEnd = 0;

        int j = 0;
        int i = 0;

        for (; i < starts1.size(); i++) {
            long start = starts1.get(i);
            long end = ends1.get(i);
            long nextStart;
            boolean s = false;

            if (start > starts2.get(j)) {
                s = true;
            }

            if (s && j < starts2.size() - 1) {
                nextStart = starts1.get(i);
                start = starts2.get(j);
                end = ends2.get(j++);

            } else {
                nextStart = starts2.get(j);
            }

            if (start < previousEnd) {
                start = previousEnd;
            }

            previousEnd = end;

            if (end > nextStart) {
                end = nextStart;
            }


            if (end != start) {
                result_start.add(start);
                result_end.add(end);
            }

            if (s) i--;

        }

        String name = intv1.getName() + "_" + intv2.getName();
        String desc = intv1.getDescription() + "_" + intv2.getDescription();

        return new InOutTrack(result_start, result_end, name, desc, intv1.getAssembly(), intv2.getCellLine());
    }


    /**
     * Sums up the size of all intervals of a given track
     *
     * @param track - track with the intervals to sum up
     * @return sum of track length inside the intervals
     */
    public static long sumOfIntervals(Track track) {

        long size = 0;

        List<Long> intervalStart = track.getStarts();
        List<Long> intervalEnd = track.getEnds();

        for (int i = 0; i < intervalStart.size(); i++)
                size += intervalEnd.get(i) - 1 - intervalStart.get(i);

        return size;
    }

    public static Track subsetScore(ScoredTrack interval, double score) {

        List<Long> intervalStart = new ArrayList<>();
        List<Long> intervalEnd = new ArrayList<>();

        List<Double> intervalScore = interval.getIntervalScore();
        List<Double> intervalScore_n = new ArrayList<>();

        List<String> names = new ArrayList<>();

        for (int i = 0; i < intervalScore.size(); i++) {

            if (intervalScore.get(i) == score) {
                intervalStart.add(interval.getStarts().get(i));
                intervalEnd.add(interval.getEnds().get(i));
                intervalScore_n.add(score);
                names.add(interval.getIntervalName().get(i));
            }
        }

        return new ScoredTrack(intervalStart, intervalEnd, names, intervalScore_n, interval.getName(), interval.getDescription(), interval.getAssembly(), interval.getCellLine());
    }


    /**
     * Inverts track. Scored and named intervals loose their Type because scores and names cannot be kept.
     *
     * @param track - track to invert
     * @return inverted track
     */
    public static InOutTrack invert(Track track) {
        GenomeFactory.Assembly assembly = track.getAssembly();

        if (track.getStarts().size() == 0)
            return (InOutTrack) track.clone();

        // copy start to end and end to start list
        List<Long> starts = new ArrayList<>(track.getEnds());
        List<Long> ends = new ArrayList<>(track.getStarts());

        if (track.getStarts().get(0) != 0L)
            starts.add(0, 0L);
        else ends.remove(0);


        if (track.getEnds().get(track.getEnds().size() - 1) == ChromosomSizes.getInstance().getGenomeSize(assembly))
            starts.remove(starts.size() - 1);
        else
            ends.add(ChromosomSizes.getInstance().getGenomeSize(assembly));

        return new InOutTrack(starts, ends, track.getName(), track.getDescription(), track.getAssembly(), track.getCellLine());
    }

    /**
     * Converts a non score track to a scored track with score 1.0 for each track.
     *
     * @param track input track
     * @return intervals of type score with score values
     */
    public static ScoredTrack cast(InOutTrack track) {

        List<Double> scores = new ArrayList<>(Collections.nCopies(track.getStarts().size(), 1.0));
        List<String> names = new ArrayList<>(Collections.nCopies(track.getStarts().size(), ""));

        return new ScoredTrack(track.getStarts(), track.getEnds(), names, scores, track.getName(), track.getDescription(), track.getAssembly(), track.getCellLine());
    }

    public static ScoredTrack cast(NamedTrack track) {

        List<Double> scores = track.getIntervalName().stream().map(name -> (double) name.hashCode()).collect(Collectors.toList());

        return new ScoredTrack(track.getStarts(), track.getEnds(), track.getIntervalName(), scores, track.getName(), track.getDescription(), track.getAssembly(), track.getCellLine());

    }


    /**
     * Test if the intervals of a track are correct.
     * Correct means in order. not overlapping and the start and end list are of same size.
     *
     * @param track - track to test
     * @return true if track is good. false if not.
     *
     */
    public static boolean checkTrack(Track track){

        List<Long> intervalStart = track.getStarts();
        List<Long> intervalEnd = track.getEnds();

        if(intervalEnd.size() != intervalStart.size()) return false;

        Long lastStart = 0L;
        Long lastEnd = 0L;

        for(int i = 0; i < intervalEnd.size(); i++){
            Long start = intervalStart.get(i);
            Long end = intervalEnd.get(i);

            if(start > end) return false;
            if(start < lastEnd) return false;
            if(lastStart > start) return false;

            lastEnd = end;
            lastStart = start;
        }

        return true;
    }

    /**
     * Converts a distance track to a inout track by a given range.
     * Range is substraced for start and added to each position for the end
     *
     * @param track - track to convert
     * @param range - range to set inout window
     *
     * @return inout track with the ranges as inside intervals
     */
    public static InOutTrack convertByRange(DistanceTrack track, int range) {
        List<Long> start = new ArrayList<>();
        List<Long> end = new ArrayList<>();

        for(Long s: track.getStarts()){
            start.add(s - range);
            end.add(s + range);
        }

        return  TrackFactory.getInstance().createInOutTrack(start, end, track.getName() + " as inout ", track.getDescription(), track.getAssembly());
    }
}
