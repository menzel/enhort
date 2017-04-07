package de.thm.genomeData;

import de.thm.logo.GenomeFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Track for intervals that define inside and outside of intervals.
 *
 * Created by Michael Menzel on 25/2/16.
 */
public class InOutTrack extends Track {

    private final int uid = UID.incrementAndGet();
    private final long[] intervalsStart;
    private final long[] intervalsEnd;
    private final String name;
    private final String description;

    private final GenomeFactory.Assembly assembly;
    private final CellLine cellLine;

    InOutTrack(List<Long> starts, List<Long> ends, String name, String description, GenomeFactory.Assembly assembly, CellLine cellLine) {

        intervalsStart =  new long[starts.size()];
        intervalsEnd = new long[ends.size()];

        for (int i = 0; i < starts.size(); i++)
            intervalsStart[i] = starts.get(i);
        for (int i = 0; i < ends.size(); i++)
            intervalsEnd[i] = ends.get(i);

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
    public long[] getStarts() {
        return this.intervalsStart;
    }


    @Override
    public long[] getEnds() {
        return this.intervalsEnd;
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
        result = 31 * result + intervalsEnd.length;
        result = 31 * result + intervalsStart.length;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InOutTrack)) return false;

        InOutTrack track = (InOutTrack) o;

        if (!Arrays.equals(intervalsStart, track.intervalsStart)) return false;
        if (!Arrays.equals(intervalsEnd, track.intervalsEnd)) return false;
        return !(description != null ? !description.equals(track.description) : track.description != null);
    }

}
