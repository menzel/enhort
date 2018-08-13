// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
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
