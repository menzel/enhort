package de.thm.genomeData;

import de.thm.logo.GenomeFactory;

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

    private final long[] intervalsStart;
    private final long[] intervalsEnd;
    private final String name;
    private final String description;
    private final GenomeFactory.Assembly assembly;
    private final CellLine cellLine;

    AbstractTrack(List<Long> starts, List<Long> ends, String name, String description, GenomeFactory.Assembly assembly, CellLine cellLine) {

        intervalsStart = starts;
        intervalsEnd = ends;
        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }


    @Override
    public abstract Track clone();

    @Override
    public List<Long> getStarts() {
        return intervalsStart;
    }

    @Override
    public List<Long> getEnds() {
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
    public GenomeFactory.Assembly getAssembly() {
        return assembly;
    }


    @Override
    public CellLine getCellLine() {
        return cellLine;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;

        Track track = (Track) o;

        if (!intervalsStart.equals(track.getStarts())) return false;
        if (!intervalsEnd.equals(track.getEnds())) return false;
        return !(description != null ? !description.equals(track.getDescription()) : track.getDescription() != null);

    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsStart.size();
        result = 31 * result + intervalsEnd.size();
        return result;
    }
}
