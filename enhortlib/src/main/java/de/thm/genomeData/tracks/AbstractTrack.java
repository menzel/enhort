package de.thm.genomeData.tracks;


import de.thm.misc.Genome;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract skeletal class.
 * <p>
 * Created by Michael Menzel on 24/2/16.
 */
@SuppressWarnings("unused")
public abstract class AbstractTrack extends Track {

    private static final long serialVersionUID = 30624951L;
    private final int uid = UID.incrementAndGet();

    private transient final long[] intervalsStart;
    private transient final long[] intervalsEnd;
    private final String name;
    private final String description;
    private final Genome.Assembly assembly;
    private final String cellLine;

    AbstractTrack(List<Long> starts, List<Long> ends, String name, String description, Genome.Assembly assembly, String cellLine) {

        intervalsStart = new long[starts.size()];
        intervalsEnd = new long[ends.size()];

        for (int i = 0; i < starts.size(); i++)
            intervalsStart[i] = starts.get(i);
        for (int i = 0; i < ends.size(); i++)
            intervalsEnd[i] = ends.get(i);

        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }


    @Override
    public abstract Track clone();

    @Override
    public long[] getEnds() {
        return intervalsEnd;
    }

    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Genome.Assembly getAssembly() {
        return assembly;
    }


    @Override
    public String getCellLine() {
        return cellLine;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;

        Track track = (Track) o;

        if (!Arrays.equals(intervalsStart, track.getStarts())) return false;
        if (!Arrays.equals(intervalsEnd, track.getEnds())) return false;
        return !((description != null) ? !description.equals(track.getDescription()) : (track.getDescription() != null));

    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsStart.length;
        result = 31 * result + intervalsEnd.length;
        return result;
    }
}
