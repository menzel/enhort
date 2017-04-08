package de.thm.genomeData;

import de.thm.logo.GenomeFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Track with scored values for each interval.
 *
 * Created by Michael Menzel on 25/2/16.
 */
public class ScoredTrack extends Track {

    private final int uid = UID.incrementAndGet();
    private final long[] intervalsStart;
    private final long[] intervalsEnd;
    private final String[] intervalName;
    private final double[] intervalScore;
    private final String name;
    private final GenomeFactory.Assembly assembly;
    private final CellLine cellLine;
    private final String description;

    ScoredTrack(long[] starts, long[] ends, String[] names, double[] scores, String name, String description, GenomeFactory.Assembly assembly, CellLine cellLine) {

        if (starts != null) {
            intervalsStart = starts;
        } else intervalsStart = new long[0];

        if (ends != null) {
            intervalsEnd = ends;
        } else intervalsEnd = new long[0];

        if (names != null) {
            intervalName = names;
        } else intervalName = new String[0];

        if (scores != null) {
            intervalScore = scores;
        } else intervalScore = new double[0];

        this.description = description;
        this.name = name;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }

    ScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, GenomeFactory.Assembly assembly, CellLine cellLine) {

        if (starts != null) {
            intervalsStart = new long[starts.size()];
            for (int i = 0; i < starts.size(); i++)
                intervalsStart[i] = starts.get(i);
        } else intervalsStart = new long[0];

        if (ends != null) {
            intervalsEnd = new long[ends.size()];
            for (int i = 0; i < ends.size(); i++)
                intervalsEnd[i] = ends.get(i);
        } else intervalsEnd= new long[0];

        if (names != null) {
            intervalName = new String[names.size()];
            for (int i = 0; i < names.size(); i++)
                intervalName[i] = names.get(i);
        } else intervalName = new String[0];

        if (scores != null) {
            intervalScore = new double[scores.size()];
            for (int i = 0; i < scores.size(); i++)
                intervalScore[i] = scores.get(i);
        } else intervalScore = new double[0];

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

        return new ScoredTrack(
                this.getStarts(),
                this.getEnds(),
                this.getIntervalName(),
                this.getIntervalScore(),
                name,
                description,
                assembly,
                cellLine
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScoredTrack)) return false;

        ScoredTrack interval = (ScoredTrack) o;
        if (!Arrays.equals(intervalsStart, interval.intervalsStart)) return false;
        if (!Arrays.equals(intervalsEnd, interval.intervalsEnd)) return false;
        if (!Arrays.equals(intervalName, interval.intervalName)) return false;
        if (!Arrays.equals(intervalScore, interval.intervalScore)) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsEnd.length;
        return result;
    }

    @Override
    public long[] getStarts() {
        return this.intervalsStart;
    }

    @Override
    public long[] getEnds() {
        return this.intervalsEnd;
    }

    public String[] getIntervalName() {
        return intervalName;
    }

    public double[] getIntervalScore() {
        return intervalScore;
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
}
