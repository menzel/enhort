package de.thm.genomeData;

import de.thm.logo.GenomeFactory;

import java.util.ArrayList;
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
    private final CellLine cellLine;
    private final String description;
    private List<Long> intervalsStart;
    private List<Long> intervalsEnd;
    private List<String> intervalName;

    NamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description, GenomeFactory.Assembly assembly, CellLine cellLine) {

        this.intervalsStart= starts;
        this.intervalsEnd= ends;
        this.intervalName= names;
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
                new ArrayList<>(intervalsStart),
                new ArrayList<>(intervalsEnd),
                new ArrayList<>(intervalName),
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
        if (!intervalsStart.equals(interval.intervalsStart)) return false;
        if (!intervalsEnd.equals(interval.intervalsEnd)) return false;
        return intervalName.equals(interval.intervalName) && !(description != null ? !description.equals(interval.description) : interval.description != null);
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsEnd.size();
        return result;
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
    public List<Long> getStarts() {
        return intervalsStart;
    }

    @Override
    public List<Long> getEnds() {
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
