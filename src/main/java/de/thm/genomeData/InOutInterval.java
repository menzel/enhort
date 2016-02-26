package de.thm.genomeData;

import java.util.List;

/**
 * Created by Michael Menzel on 25/2/16.
 */
public class InOutInterval extends Interval{

    private final int uid = ++UID;
    private final List<Long> intervalsStart;
    private final List<Long> intervalsEnd;
    private final String name;
    private final String description;

    InOutInterval(List<Long> starts, List<Long> ends, String name, String description) {

        intervalsStart = starts;
        intervalsEnd = ends;
        this.description = description;
        this.name = name;
    }


    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public Interval clone() {
        return null;
    }

    @Override
    public List<Long> getIntervalsStart() {
        return intervalsStart;
    }

    @Override
    public List<Long> getIntervalsEnd() {
        return intervalsEnd;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsEnd.size();
        result = 31 * result + intervalsStart.size();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InOutInterval)) return false;

        InOutInterval interval = (InOutInterval) o;

        if (!intervalsStart.equals(interval.intervalsStart)) return false;
        if (!intervalsEnd.equals(interval.intervalsEnd)) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);
    }


}
