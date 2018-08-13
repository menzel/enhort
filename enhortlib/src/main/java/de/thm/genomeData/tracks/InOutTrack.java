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
 * Track for intervals that define inside and outside of intervals.
 *
 * Created by Michael Menzel on 25/2/16.
 */
public class InOutTrack extends AbstractTrack {


    InOutTrack(long[] starts, long[] ends, String name, String description, Genome.Assembly assembly, String cellLine) {

        super(starts, ends, name, description, assembly, cellLine);
    }

    InOutTrack(List<Long> starts, List<Long> ends, TrackEntry entry) {

        super(starts.stream().mapToLong(l -> l).toArray(),
                ends.stream().mapToLong(l -> l).toArray(),
                entry);
    }

    @Override
    public Track clone() {
        return new InOutTrack(intervalsStart, intervalsEnd, this.getName(), this.getDescription(), this.assembly, this.cellLine);
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
