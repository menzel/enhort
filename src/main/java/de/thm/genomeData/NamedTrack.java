package de.thm.genomeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 26/2/16.
 */
public class NamedTrack extends Track {

    private final int uid = ++UID;
    private final List<Long> intervalsStart;
    private final List<Long> intervalsEnd;
    private final List<String> intervalName;
    private final String name;
    private final String description;


    NamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description) {

        intervalsStart = starts;
        intervalsEnd = ends;
        intervalName = names;
        this.description = description;
        this.name = name;
    }

    @Override
    public int getUid() {
        return uid;
    }


    @Override
    public Track clone() {

        return new NamedTrack(
            new ArrayList<>(intervalsStart),
            new ArrayList<>(intervalsEnd),
            new ArrayList<>(intervalName),
            name,
            description
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedTrack)) return false;

        NamedTrack interval = (NamedTrack) o;
        if (!intervalsStart.equals(interval.intervalsStart)) return false;
        if (!intervalsEnd.equals(interval.intervalsEnd)) return false;
        if (!intervalName.equals(interval.intervalName)) return false;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
