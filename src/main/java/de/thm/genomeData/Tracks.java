package de.thm.genomeData;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.misc.ChromosomSizes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collection of utils for interval objects.
 * <p>
 * Created by Michael Menzel on 13/1/16.
 */
public class Tracks {

    //prevent init of Intervals object with private constructor
    private Tracks() {
    }


    /**
     * Intersect a list of intervals. Resulting interval has only starts/stop where all input interval were marked.
     * *
     *
     * @param tracks - list of Interval Objects
     * @return interval object. Can be empty
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
     * Intersect two intervals. Resulting interval has only starts/stop where both input interval were marked.
     *
     * @param intv1 - first input  interval object
     * @param intv2 - second input interval object
     * @return interval with marked intervals were both input intervals were marked. Type is set to inout, names and scores get lost in intersect
     */
    public static InOutTrack intersect(Track intv1, Track intv2) {

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();

        if (starts1.size() > starts2.size()) { //if start2 is smaller swap lists

            starts2 = intv1.getIntervalsStart();
            starts1 = intv2.getIntervalsStart();

            ends2 = intv1.getIntervalsEnd();
            ends1 = intv2.getIntervalsEnd();
        }

        // iterator through one of the intervals, check if a interval in the other track is overlapping with the current. if not proceed if yes create new interval
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

        return new InOutTrack(result_start, result_end, name, desc);
    }


    /**
     * Sums up a list of intervals. The result has a interval were any of the input intervals were marked
     *
     * @param tracks - list of intervals
     * @return interval with the sum of positions
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
     * Sums up two intervals. The result has a interval were any of the input intervals were marked
     *
     * @param intv1 - first interval for sum
     * @param intv2 - second interval for sum
     * @return sum of intv1 and intv2
     */
    public static Track sum(Track intv1, Track intv2) throws IntervalTypeNotAllowedExcpetion {
        return invert(intersect(invert(intv1), invert(intv2)));
    }

    /**
     * Xor a list of intervals. The result has a interval were one of each was marked
     *
     * @param tracks - list of intervals
     * @return interval with the xor of positions
     */
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
     * @param intv1 - first interval
     * @param intv2 - second interval
     * @return xor(interval1, interval2)
     */
    public static InOutTrack xor(Track intv1, Track intv2) {

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

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

        return new InOutTrack(result_start, result_end, name, desc);
    }


    /**
     * Sums up the size of all intervals. Either all intervals or the space between them
     *
     * @param track - intervals to sum up
     * @param mode  - either "in" or "out".
     * @return sum of interval length inside or outside the intervals
     */
    public static long sumOfIntervals(Track track, String mode) {

        long size = 0;
        int io = (mode.equals("in")) ? 0 : 1;

        List<Long> intervalStart = track.getIntervalsStart();
        List<Long> intervalEnd = track.getIntervalsEnd();

        for (int i = 0; i < intervalStart.size() - io; i++) {
            if (mode.equals("in"))
                size += intervalEnd.get(i) - intervalStart.get(i);
            else
                size += intervalStart.get(i + 1) - intervalEnd.get(i);
        }

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
                intervalStart.add(interval.getIntervalsStart().get(i));
                intervalEnd.add(interval.getIntervalsEnd().get(i));
                intervalScore_n.add(score);
                names.add(interval.getIntervalName().get(i));
            }
        }

        return new ScoredTrack(intervalStart, intervalEnd, names, intervalScore_n, interval.getName(), interval.getDescription());
    }


    /**
     * Inverts interval. Scored and named intervals loose their Type because scores and names cannot be kept.
     *
     * @param track - interval to invert
     * @return inverted interval
     */
    public static InOutTrack invert(Track track) {

        if (track.getIntervalsStart().size() == 0)
            return cast(track.clone());

        List<Long> starts = new ArrayList<>(track.getIntervalsEnd());
        List<Long> ends = new ArrayList<>(track.getIntervalsStart());

        if (track.getIntervalsStart().get(0) != 0L)
            starts.add(0, 0L);
        else
            ends.remove(0);


        if (track.getIntervalsEnd().get(track.getIntervalsEnd().size() - 1) == ChromosomSizes.getInstance().getGenomeSize())
            starts.remove(starts.size() - 1);
        else
            ends.add(ChromosomSizes.getInstance().getGenomeSize());


        return new InOutTrack(starts, ends, track.getName(), track.getDescription());
    }

    private static InOutTrack cast(Track track) {
        return new InOutTrack(track.getIntervalsStart(), track.getIntervalsEnd(), track.getName(), track.getDescription());
    }


    /**
     * Converts a non score interval to a scored interval with score 1.0 for each interval.
     *
     * @param interval input interval
     * @return intervals of type score with score values
     */
    public static ScoredTrack cast(InOutTrack interval) {

        List<Double> scores = new ArrayList<>(Collections.nCopies(interval.getIntervalsStart().size(), 1.0));
        List<String> names = new ArrayList<>(Collections.nCopies(interval.getIntervalsStart().size(), ""));

        return new ScoredTrack(interval.getIntervalsStart(), interval.getIntervalsEnd(), names, scores, interval.getName(), interval.getDescription());
    }
}
