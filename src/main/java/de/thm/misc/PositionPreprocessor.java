package de.thm.misc;

import de.thm.genomeData.*;

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

        List<Long> intervalsStart = interval.getStarts();
        List<Long> intervalsEnd = interval.getEnds();

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

        return TrackFactory.getInstance().createInOutTrack(newStart, newEnd, interval.getName(), interval.getDescription(), interval.getAssembly(), interval.getCellLine());
    }

    public static ScoredTrack preprocessData(ScoredTrack track) {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();
        List<Double> newScore = new ArrayList<>();

        List<Long> intervalsStart = track.getStarts();
        List<Long> intervalsEnd = track.getEnds();
        List<Double> scores = track.getIntervalScore();

        if (intervalsStart.isEmpty()) return track;

        long start = intervalsStart.get(0);
        long end = intervalsEnd.get(0);

        double tmpScore = scores.get(0);
        int iCount = 1;


        for (int i = 0; i < intervalsStart.size(); i++) {

            if (i < intervalsStart.size() - 1 && end > intervalsStart.get(i + 1)) { // overlap

                if (end < intervalsEnd.get(i + 1))
                    end = intervalsEnd.get(i + 1);

                tmpScore += scores.get(i + 1);
                iCount++;

            } else {  //do not overlap
                newStart.add(start);
                newEnd.add(end);
                newScore.add(tmpScore / iCount);

                if (i >= intervalsStart.size() - 1) break; // do not get next points if this was the last

                start = intervalsStart.get(i + 1);
                end = intervalsEnd.get(i + 1);
                tmpScore = scores.get(i + 1);
                iCount = 1;

            }
        }

        intervalsStart.clear();
        intervalsEnd.clear();

        ScoredTrack tmp = TrackFactory.getInstance().createScoredTrack(newStart, newEnd, track.getIntervalName().subList(0,newStart.size()), newScore ,track.getName(), track.getDescription());
        tmp = Tracks.bin(tmp, 50);

        return tmp;
    }



    public static NamedTrack preprocessData(NamedTrack track) {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();
        List<String> newNames = new ArrayList<>();

        List<Long> intervalsStart = track.getStarts();
        List<Long> intervalsEnd = track.getEnds();
        List<String> names = track.getIntervalName();

        //if (intervalsStart.isEmpty()) return track;
        if(true) return track; // do not preprocess Named Track for now. TODO Fix

        long start = intervalsStart.get(0);
        long end = intervalsEnd.get(0);
        String name = names.get(0);

        for (int i = 0; i < intervalsStart.size(); i++) {

            if (i < intervalsStart.size() - 1 && end > intervalsStart.get(i + 1)) { // overlap

                if (end < intervalsEnd.get(i + 1))
                    end = intervalsEnd.get(i + 1);

                name = name.equals(names.get(i)) ? name : names.get(i) + "_" + name;

            } else {  //do not overlap
                newStart.add(start);
                newEnd.add(end);
                newNames.add(name.intern());

                if (i >= intervalsStart.size() - 1) break; // do not get next points if this was the last

                start = intervalsStart.get(i + 1);
                end = intervalsEnd.get(i + 1);
                name = names.get(i + 1);

            }
        }

        intervalsStart.clear();
        intervalsEnd.clear();


        return TrackFactory.getInstance().createNamedTrack(newStart, newEnd, newNames,track.getName(), track.getDescription(), track.getAssembly(), track.getCellLine());
    }
}
