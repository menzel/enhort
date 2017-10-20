package de.thm.genomeData.tracks;


import de.thm.misc.Genome;

import java.util.Arrays;
import java.util.List;

/**
 * Track with scored values for each interval.
 *
 * Created by Michael Menzel on 25/2/16.
 */
public class ScoredTrack extends AbstractTrack {

    private transient final String[] intervalName;
    private transient final double[] intervalScore;

    ScoredTrack(long[] starts, long[] ends, String[] names, double[] scores, String name, String description, Genome.Assembly assembly, String cellLine) {

        super(starts,
                ends,
                name,
                description,
                assembly,
                cellLine);

        if (names != null) {
            intervalName = names;
        } else intervalName = new String[0];

        if (scores != null) {
            intervalScore = scores;
        } else intervalScore = new double[0];

    }

    ScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, Genome.Assembly assembly, String cellLine) {

        super(starts.stream().mapToLong(l->l).toArray(),
                ends.stream().mapToLong(l->l).toArray(),
                name,
                description,
                assembly,
                cellLine);

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
                this.intervalName,
                this.intervalScore,
                this.getName(),
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

    public int hashCode() {
        return 31 * uid + intervalsEnd.length;
    }

    public String[] getIntervalName() {
        return intervalName;
    }

    public double[] getIntervalScore() {
        return intervalScore;
    }

}
