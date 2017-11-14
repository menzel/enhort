package de.thm.genomeData.tracks;


import de.thm.misc.Genome;

import java.util.Arrays;
import java.util.List;

/**
 * Track for genomic positions to which a distance will be calculated.
 *
 * Created by menzel on 9/23/16.
 */
public class DistanceTrack extends AbstractTrack {

    DistanceTrack(long[] starts, String name, String description, Genome.Assembly assembly, String cellLine) {

        super(starts, new long[0], name, "Distance from " + name, assembly, cellLine);
    }


    DistanceTrack(List<Long> starts, TrackEntry trackEntry) {

        super(starts.stream().mapToLong(l -> l).toArray(), new long[0], trackEntry);
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
    public Track clone() {
        return new DistanceTrack(intervalsStart, name, description, assembly, cellLine);
    }
}
