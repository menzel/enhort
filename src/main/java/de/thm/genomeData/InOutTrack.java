package de.thm.genomeData;

import de.thm.calc.GenomeFactory;

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

    private GenomeFactory.Assembly assembly;
    private CellLine cellLine;

    InOutTrack(List<Long> starts, List<Long> ends, String name, String description, GenomeFactory.Assembly assembly, CellLine cellLine) {

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
        return new InOutTrack(this.getStarts(), this.getEnds(), this.getName(), this.getDescription(), this.assembly, this.cellLine);
    }

    @Override
    public List<Long> getStarts() {
        return intervalsStart;
    }

    @Override
    public List<Long> getEnds() {
        return intervalsEnd;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
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

        InOutTrack track = (InOutTrack) o;

        if (!intervalsStart.equals(track.intervalsStart)) return false;
        if (!intervalsEnd.equals(track.intervalsEnd)) return false;
        return !(description != null ? !description.equals(track.description) : track.description != null);
    }


}
