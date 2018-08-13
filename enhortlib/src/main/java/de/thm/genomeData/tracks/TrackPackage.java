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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A track package has a list of tracks.
 *
 * Created by Michael Menzel on 12/2/16.
 */
public final class TrackPackage implements Serializable{
    private final List<Track> trackList;
    private final String name;
    private final Genome.Assembly assembly;
    private final String cellLine;


    TrackPackage(String name, Genome.Assembly assembly, String cellLine) {
        this.trackList = new ArrayList<>();
        this.name = name;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }


    public void add(Track track){
        this.trackList.add(track);
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public String getName() {
        return name;
    }

    public Genome.Assembly getAssembly() {
        return assembly;
    }

    public String getCellLine() {
        return cellLine;
    }
}
