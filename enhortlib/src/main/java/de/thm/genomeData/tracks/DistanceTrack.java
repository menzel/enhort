package de.thm.genomeData.tracks;


import de.thm.misc.Genome;

import java.util.Arrays;
import java.util.List;

/**
 * Track for genomic positions to which a distance will be calculated.
 *
 * Created by menzel on 9/23/16.
 */
public class DistanceTrack extends Track{

    private final int uid = UID.incrementAndGet();
    private final long[] intervalsStart;
    private final String name;
    private final Genome.Assembly assembly;
    private final String cellLine;
    private final String description;

    DistanceTrack(long[] starts, String name, String description, Genome.Assembly assembly, String cellLine) {

        if (starts != null) {
            intervalsStart = starts;
        } else intervalsStart = new long[0];

        this.description = description;
        this.name = name;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }




    DistanceTrack(List<Long> starts, String name, String description, Genome.Assembly assembly, String cellLine) {

        if(starts != null) {
            intervalsStart = new long[starts.size()];
            for (int i = 0; i < starts.size(); i++)
                intervalsStart[i] = starts.get(i);
        } else intervalsStart = new long[0];

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
    public long[] getStarts() {
        return this.intervalsStart;
    }

    @Override
    public long[] getEnds() {
        return getStarts();
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
        result = 31 * result + intervalsStart.length;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DistanceTrack)) return false;

        DistanceTrack interval = (DistanceTrack) o;

        if (!Arrays.equals(intervalsStart, interval.intervalsStart)) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);
    }

    @Override
    public Genome.Assembly getAssembly() {
        return assembly;
    }

    @Override
    public String getCellLine() {
        return cellLine;
    }
}
