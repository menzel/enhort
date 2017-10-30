package de.thm.genomeData.tracks;


import de.thm.misc.Genome;

import java.util.Arrays;
import java.util.List;

/**
 * Track for intervals that define inside and outside of intervals.
 *
 * Created by Michael Menzel on 25/2/16.
 */
public class InOutTrack extends AbstractTrack {


    InOutTrack(long[] starts, long[] ends, String name, String description, Genome.Assembly assembly, String cellLine, String pack) {

        super(starts, ends, name, description, assembly, cellLine, pack);
    }

    InOutTrack(List<Long> starts, List<Long> ends, String name, String description, Genome.Assembly assembly, String cellLine, String pack) {

        super(starts.stream().mapToLong(l->l).toArray(),
                ends.stream().mapToLong(l->l).toArray(),
                name,
                description,
                assembly,
                cellLine,
                pack);
    }

    @Override
    public Track clone() {
        return new InOutTrack(intervalsStart, intervalsEnd, this.getName(), this.getDescription(), this.assembly, this.cellLine, this.pack);
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
