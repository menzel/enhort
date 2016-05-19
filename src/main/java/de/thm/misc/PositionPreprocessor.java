package de.thm.misc;

import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.NamedTrack;
import de.thm.genomeData.ScoredTrack;
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

    public static ScoredTrack preprocessData(ScoredTrack track) {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();
        List<Double> newScore = new ArrayList<>();

        List<Long> intervalsStart = track.getIntervalsStart();
        List<Long> intervalsEnd = track.getIntervalsEnd();
        List<Double> scores = track.getIntervalScore();

        if (intervalsStart.isEmpty()) return track;

        long start = intervalsStart.get(0);
        long end = intervalsEnd.get(0);
        double score = scores.get(0);

        double tmpScore = score;
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


        return TrackFactory.getInstance().createScoredTrack(newStart, newEnd, track.getIntervalName().subList(0,newStart.size()), newScore ,track.getName(), track.getDescription());
    }


    public static NamedTrack preprocessData(NamedTrack track) {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();
        List<String> newNames = new ArrayList<>();

        List<Long> intervalsStart = track.getIntervalsStart();
        List<Long> intervalsEnd = track.getIntervalsEnd();
        List<String> names = track.getIntervalName();

        if (intervalsStart.isEmpty()) return track;

        long start = intervalsStart.get(0);
        long end = intervalsEnd.get(0);
        String name = names.get(0);

        for (int i = 0; i < intervalsStart.size(); i++) {

            if (i < intervalsStart.size() - 1 && end > intervalsStart.get(i + 1)) { // overlap

                if (end < intervalsEnd.get(i + 1))
                    end = intervalsEnd.get(i + 1);

                name += names.get(i + 1);

            } else {  //do not overlap
                newStart.add(start);
                newEnd.add(end);
                newNames.add(name);

                if (i >= intervalsStart.size() - 1) break; // do not get next points if this was the last

                start = intervalsStart.get(i + 1);
                end = intervalsEnd.get(i + 1);
                name = names.get(i + 1);

            }
        }

        intervalsStart.clear();
        intervalsEnd.clear();


        return TrackFactory.getInstance().createNamedTrack(newStart, newEnd, newNames,track.getName(), track.getDescription());
    }
}
