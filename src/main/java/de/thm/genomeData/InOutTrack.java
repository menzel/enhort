package de.thm.genomeData;

import java.util.List;

/**
 * Track for intervals that define inside and outside of intervals.
 *
 * Created by Michael Menzel on 25/2/16.
 */
public class InOutTrack extends Track {

    private final int uid = UID.incrementAndGet();
    private final List<Long> intervalsStart;
    private final List<Long> intervalsEnd;
    private final String name;
    private final String description;

    private Assembly assembly;
    private CellLine cellLine;

    InOutTrack(List<Long> starts, List<Long> ends, String name, String description, Assembly assembly, CellLine cellLine) {

        intervalsStart = starts;
        intervalsEnd = ends;
        this.description = description;
        this.name = name;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }


    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public Track clone() {
        return new InOutTrack(this.getIntervalsStart(), this.getIntervalsEnd(), this.getName(), this.getDescription(), this.assembly, this.cellLine);
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
    public Assembly getAssembly() {
        return this.assembly;
    }

    @Override
    public CellLine getCellLine() {
        return this.cellLine;
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
        if (!(o instanceof InOutTrack)) return false;

        InOutTrack interval = (InOutTrack) o;

        if (!intervalsStart.equals(interval.intervalsStart)) return false;
        if (!intervalsEnd.equals(interval.intervalsEnd)) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);
    }


}
