package de.thm.genomeData;

import de.thm.logo.GenomeFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Track object that defines interval with are identified by a name.
 *
 * Created by Michael Menzel on 26/2/16.
 */
public class NamedTrack extends Track {

    private final int uid = UID.incrementAndGet();
    private final String name;
    private final GenomeFactory.Assembly assembly;
    private final int cellLine;
    private final String description;
    private final long[] intervalsStart;
    private final long[] intervalsEnd;
    private final String[] intervalName;

    NamedTrack(long[] starts, long[] ends, String[] names, String name, String description, GenomeFactory.Assembly assembly, int cellLine) {

        intervalsStart = starts;
        intervalsEnd = ends;

        this.intervalName = names;
        this.description = description;
        this.name = name;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }

    NamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description, GenomeFactory.Assembly assembly, int cellLine) {


        intervalsStart = new long[starts.size()];
        intervalsEnd = new long[ends.size()];
        this.intervalName = new String[names.size()];

        for (int i = 0; i < starts.size(); i++)
            intervalsStart[i] = starts.get(i);
        for (int i = 0; i < ends.size(); i++)
            intervalsEnd[i] = ends.get(i);
        for (int i = 0; i < names.size(); i++)
            intervalName[i] = names.get(i);

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

        return new NamedTrack(
                intervalsStart,
                intervalsEnd,
                intervalName,
                name,
                description,
                assembly,
                cellLine
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedTrack)) return false;

        NamedTrack interval = (NamedTrack) o;
        if (!Arrays.equals(intervalsStart, interval.intervalsStart)) return false;
        if (!Arrays.equals(intervalsEnd, interval.intervalsEnd)) return false;
        return intervalName.equals(interval.intervalName) && !(description != null ? !description.equals(interval.description) : interval.description != null);
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsEnd.length;
        return result;
    }


    @Override
    public GenomeFactory.Assembly getAssembly() {
        return assembly;
    }

    @Override
    public int getCellLine() {
        return cellLine;
    }


    @Override
    public long[] getStarts() {
        return intervalsStart;
    }

    @Override
    public long[] getEnds() {
        return intervalsEnd;
    }

    public String[] getIntervalName() {
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
