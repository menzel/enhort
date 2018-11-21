// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.genomeData.tracks;

import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Collection of utils for track objects.
 * <p>
 * Created by Michael Menzel on 13/1/16.
 */
public final class Tracks {

    private static final Logger logger = LoggerFactory.getLogger(Tracks.class);

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
    public static ScoredTrack bin(ScoredTrack track, int count){

        Percentile percentile = new Percentile();
        //use method R 3 to set real values as bin bounds
        percentile = percentile.withEstimationType(Percentile.EstimationType.R_3);

        List<Double> scores = DoubleStream.of(track.getIntervalScore())
                .boxed()
                .collect(Collectors.toList());

        double[] values = new HashSet<>(scores) //create set to remove duplicates
                .parallelStream()
                            .mapToDouble(i -> i)
                            .toArray();
        Arrays.sort(values);

        double lowerBound = Collections.min(scores);

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

        return new ScoredTrack(track.getStarts(),
                track.getEnds(),
                track.getIntervalName(),
                scores.parallelStream().mapToDouble(d -> d).toArray(),
                track.getName(),
                track.getDescription(),
                track.getAssembly(),
                track.getCellLine());
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

        long[] starts1 = intv1.getStarts();
        long[] starts2 = intv2.getStarts();

        long[] ends1 = intv1.getEnds();
        long[] ends2 = intv2.getEnds();

        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();

        if (starts1.length > starts2.length) { //if start2 is smaller swap lists

            starts2 = intv1.getStarts();
            starts1 = intv2.getStarts();

            ends2 = intv1.getEnds();
            ends1 = intv2.getEnds();
        }

        // iterator through one of the intervals, check if a track in the other track is overlapping with the current. if not proceed if yes create new track
        int i2 = 0;

        for (int i1 = 0; i1 < starts1.length; i1++) {
            for (; i2 < ends2.length; i2++) {

                if (starts1[i1] < ends2[i2]) {
                    if (starts2[i2] < ends1[i1]) {
                        long start = Math.max(starts1[i1], starts2[i2]);
                        long end = Math.min(ends1[i1], ends2[i2]);

                        result_start.add(start);
                        result_end.add(end);

                    }
                }

                if (i1 < starts1.length - 1 && ends2[i2] > starts1[i1 + 1])
                    break;
            }
        }

        String name = intv1.getName() + "_" + intv2.getName();
        String desc = intv1.getDescription() + "_" + intv2.getDescription();
        String cellLine;
        if (intv1.getCellLine() != null && intv2.getCellLine() != null && intv1.getCellLine().equals(intv2.getCellLine()))
            cellLine = intv1.getCellLine();
        else
            cellLine = "Hybrid";

        return new InOutTrack(result_start, result_end, new TrackEntry(name, desc, intv1.getAssembly().toString(), cellLine, intv1.getPack()));
    }


    /**
     * Sums up a list of intervals. The result has a track were any of the input intervals were marked
     *
     * @param tracks - list of intervals
     * @return track with the sum of positions
     */
    public static Track sum(List<Track> tracks) {

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
    public static Track sum(Track intv1, Track intv2) {
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

        long[] starts1 = intv1.getStarts();
        long[] starts2 = intv2.getStarts();

        long[] ends1 = intv1.getEnds();
        long[] ends2 = intv2.getEnds();

        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();
        long previousEnd = 0;

        int j = 0;
        int i = 0;

        for (; i < starts1.length; i++) {
            long start = starts1[i];
            long end = ends1[i];
            long nextStart;
            boolean s = false;

            if (start > starts2[j]) {
                s = true;
            }

            if (s && j < starts2.length - 1) {
                nextStart = starts1[i];
                start = starts2[j];
                end = ends2[j++];

            } else {
                nextStart = starts2[j];
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

        String cellLine;
        if (intv1.getCellLine().equals(intv2.getCellLine()))
            cellLine = intv1.getCellLine();
        else
            cellLine = "Hybrid";

        return new InOutTrack(result_start, result_end, new TrackEntry(name, desc, intv1.getAssembly().toString(), cellLine, ""));
    }


    /**
     * Sums up the size of all intervals of a given track
     *
     * @param track - track with the intervals to sum up
     * @return sum of track length inside the intervals
     */
    public static long sumOfIntervals(Track track) {

        long size = 0;

        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();

        for (int i = 0; i < intervalStart.length; i++)
            size += intervalEnd[i] - intervalStart[i];

        return size;
    }

    public static Track subsetScore(ScoredTrack interval, double score) {

        List<Long> intervalStart = new ArrayList<>();
        List<Long> intervalEnd = new ArrayList<>();

        double[] intervalScore = interval.getIntervalScore();
        List<Double> intervalScore_n = new ArrayList<>();

        List<String> names = new ArrayList<>();

        for (int i = 0; i < intervalScore.length; i++) {

            if (intervalScore[i] == score) {
                intervalStart.add(interval.getStarts()[i]);
                intervalEnd.add(interval.getEnds()[i]);
                intervalScore_n.add(score);
                names.add(interval.getIntervalName()[i]);
            }
        }

        return new ScoredTrack(intervalStart, intervalEnd, names, intervalScore_n, new TrackEntry(interval.getName(), interval.getDescription(), interval.getAssembly().toString(), interval.getCellLine(), interval.getPack()));
    }


    /**
     * Inverts track. Scored and named intervals loose their Type because scores and names cannot be kept.
     *
     * @param track - track to invert
     * @return inverted track
     */
    public static InOutTrack invert(Track track) {
        Genome.Assembly assembly = track.getAssembly();

        if (track.getStarts().length == 0)
            return (InOutTrack) track.clone();

        // copy start to end and end to start list
        List<Long> starts = Arrays.stream(track.getEnds()).boxed().collect(Collectors.toList());
        List<Long> ends = Arrays.stream(track.getStarts()).boxed().collect(Collectors.toList());

        if (track.getStarts()[0] != 0L)
            starts.add(0, 0L);
        else ends.remove(0);


        if (track.getEnds()[track.getEnds().length - 1] == ChromosomSizes.getInstance().getGenomeSize(assembly))
            starts.remove(starts.size() - 1);
        else
            ends.add(ChromosomSizes.getInstance().getGenomeSize(assembly));

        return new InOutTrack(starts, ends, new TrackEntry(track.getName(), track.getDescription(), track.getAssembly().toString(), track.getCellLine(), track.getPack()));
    }

    /**
     * Converts a non score track to a scored track with score 1.0 for each track.
     *
     * @param track input track
     * @return intervals of type score with score values
     */
    public static ScoredTrack cast(InOutTrack track) {

        List<Double> scores = new ArrayList<>(Collections.nCopies(track.getStarts().length, 1.0));
        List<String> names = new ArrayList<>(Collections.nCopies(track.getStarts().length, ""));

        return new ScoredTrack(track.getStarts(),
                track.getEnds(),
                names.toArray(new String[0]),
                scores.parallelStream().mapToDouble(d -> d).toArray(),
                track.getName(),
                track.getDescription(),
                track.getAssembly(),
                track.getCellLine());
    }

    public static ScoredTrack cast(NamedTrack track) {

        List<Double> scores = Arrays.stream(track.getIntervalName()).map(name -> (double) name.hashCode()).collect(Collectors.toList());

        return new ScoredTrack(track.getStarts(),
                track.getEnds(),
                track.getIntervalName(),
                scores.parallelStream().mapToDouble(d -> d).toArray(),
                track.getName(),
                track.getDescription(),
                track.getAssembly(),
                track.getCellLine());
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

        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();

        if (intervalEnd.length != intervalStart.length) {

            logger.warn("Different start and ends lenght in" + track.getName() + " " + track.getCellLine() + " " + track.getDbid());
            return false;
        }

        Long lastStart = 0L;
        Long lastEnd = 0L;

        for (int i = 0; i < intervalEnd.length; i++) {
            Long start = intervalStart[i];
            Long end = intervalEnd[i];

            if (start > end) {
                logger.warn("Start larger than end " + track.getName() + " " + track.getCellLine() + " " + track.getDbid());
                return false;
            }
            if (start < lastEnd) {
                logger.warn("next start is smaller than last end " + track.getName() + " " + track.getCellLine() + " " + track.getDbid());
                return false;
            }

            if (lastStart > start) {
                logger.warn("overlapping starts " + track.getName() + " " + track.getCellLine() + " " + track.getDbid());
                return false;
            }

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

        return TrackFactory.getInstance().createInOutTrack(start, end, track.getName() + " as inout ", track.getDescription(), track.getAssembly());
    }

    public static DistanceTrack createDistFromInOut(InOutTrack track) {

        List<Long> midpoints = IntStream.range(0, track.getStarts().length)
                .mapToLong(p -> track.getEnds()[p] - track.getStarts()[p])
                .boxed()
                .collect(Collectors.toList());

        return TrackFactory.getInstance().createDistanceTrack(midpoints, "Distance from " + track.getName(), "Distance from " + track.getName(), Genome.Assembly.hg19, track.getCellLine());

    }


    /**
     * Returns a track created from given sites object.
     * The site positions mark the start, each position +1 is the end
     *
     * @param sites - a user sites object
     * @return track object with the positions from the sites
     */
    public static Track getTrack(Sites sites){

        return TrackFactory.getInstance().createInOutTrack(sites.getPositions(),
                sites.getPositions().stream().map(p -> p+1).collect(Collectors.toList()),
                "Sites",
                "Sites",
                sites.getAssembly());
    }

}
