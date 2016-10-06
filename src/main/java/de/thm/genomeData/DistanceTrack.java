package de.thm.genomeData;

import java.util.List;

/**
 * Track for genomic positions to which a distance will be calculated.
 *
 * Created by menzel on 9/23/16.
 */
public class DistanceTrack extends Track{

    private final int uid = UID.incrementAndGet();
    private final List<Long> intervalsStart;
    private final List<Long> intervalsEnd;
    private final String name;
    private final Assembly assembly;
    private final CellLine cellLine;
    private final String description;

    DistanceTrack(List<Long> starts, String name, String description, Assembly assembly, CellLine cellLine) {

        intervalsStart = starts;
        intervalsEnd = starts;
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
        if (!(o instanceof DistanceTrack)) return false;

        DistanceTrack interval = (DistanceTrack) o;

        if (!intervalsStart.equals(interval.intervalsStart)) return false;
        if (!intervalsEnd.equals(interval.intervalsEnd)) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);
    }

    @Override
    public Assembly getAssembly() {
        return assembly;
    }

    @Override
    public CellLine getCellLine() {
        return cellLine;
    }
}
