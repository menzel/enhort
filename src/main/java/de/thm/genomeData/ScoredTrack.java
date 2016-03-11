package de.thm.genomeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Track with scored values for each interval.
 *
 * Created by Michael Menzel on 25/2/16.
 */
public class ScoredTrack extends Track {

    private final int uid = UID.incrementAndGet();
    private final List<Long> intervalsStart;
    private final List<Long> intervalsEnd;
    private final List<String> intervalName;
    private final List<Double> intervalScore;
    private final String name;
    private final String description;


    ScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description) {

        intervalsStart = starts;
        intervalsEnd = ends;
        intervalName = names;
        intervalScore = scores;
        this.description = description;
        this.name = name;
    }

    @Override
    public int getUid() {
        return uid;
    }


    @Override
    public Track clone() {

        return new ScoredTrack(
                new ArrayList<>(intervalsStart),
                new ArrayList<>(intervalsEnd),
                new ArrayList<>(intervalName),
                new ArrayList<>(intervalScore),
                name,
                description
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScoredTrack)) return false;

        ScoredTrack interval = (ScoredTrack) o;
        if (!intervalsStart.equals(interval.intervalsStart)) return false;
        if (!intervalsEnd.equals(interval.intervalsEnd)) return false;
        if (!intervalName.equals(interval.intervalName)) return false;
        if (!intervalScore.equals(interval.intervalScore)) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsEnd.size();
        return result;
    }

    @Override
    public List<Long> getIntervalsStart() {
        return intervalsStart;
    }

    @Override
    public List<Long> getIntervalsEnd() {
        return intervalsEnd;
    }

    public List<String> getIntervalName() {
        return intervalName;
    }

    public List<Double> getIntervalScore() {
        return intervalScore;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String getDescription() {
        return description;
    }
}
