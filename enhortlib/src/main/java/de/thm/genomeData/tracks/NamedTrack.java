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
 * Track object that defines interval with are identified by a name.
 *
 * Created by Michael Menzel on 26/2/16.
 */
public class NamedTrack extends AbstractTrack{

    private final transient String[] intervalName;

    NamedTrack(long[] starts, long[] ends, String[] names, String name, String description, Genome.Assembly assembly, String cellLine) {

        super(starts, ends, name, description, assembly, cellLine);

        this.intervalName = names;
    }

    NamedTrack(List<Long> starts, List<Long> ends, List<String> names, TrackEntry entry) {

        super(starts.stream().mapToLong(l->l).toArray(),
                ends.stream().mapToLong(l->l).toArray(),
                entry);

        this.intervalName = names.toArray(new String[0]);
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

    public String[] getIntervalName() {
        return intervalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedTrack)) return false;

        NamedTrack interval = (NamedTrack) o;
        if (!Arrays.equals(intervalsStart, interval.intervalsStart)) return false;
        if (!Arrays.equals(intervalsEnd, interval.intervalsEnd)) return false;
        return Arrays.equals(intervalName, interval.intervalName) && !(description != null ? !description.equals(interval.description) : interval.description != null);
    }
}
