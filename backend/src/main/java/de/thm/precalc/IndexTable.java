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
package de.thm.precalc;

import de.thm.genomeData.tracks.Track;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Matrix-like table to store precalculated site information
 *
 * Created by menzel on 2/8/17.
 */
class IndexTable {

    private final Map<Track, List<Integer>> props = new ConcurrentHashMap<>();
    private List<Long> positions = new ArrayList<>();
    private List<String> sequences = new ArrayList<>();

    /**
     * Returns the list of position-specific inside and outside
     *
     * @param track - track from which the data is to be retrieved
     *
     * @return List of positions and their properties for the given track
     */
    Pair<List<Long>, List<Integer>> getByTrack(Track track){

        return new ImmutablePair<>(positions, props.get(track));
    }

    /**
     * Sets a new property for the known positions for a track
     *
     * @param track - track from whicht the inout properties were calculatd
     * @param vals - list of in (1) out (0) values
     */
    void setProperties(Track track, List<Integer> vals){
        props.put(track, vals);
    }

    /**
     * Get sequences with a given length
     *
     * @param l - length of the sequences to return
     * @return list of sequences with the length of l as list
     */
    List<String> getSequences(int l) throws IllegalArgumentException{
        if(l < 2)
            throw new IllegalArgumentException("IndexTable: the sequence length should be larger than 2 it is " + l);

        int start = sequences.get(0).length()/2 - l/2;
        int end = sequences.get(0).length()/2 + l/2;

        if(start < 0 || end < 0)
            throw new IllegalArgumentException("start and end should be positive");

        return sequences.stream().map(i -> i.substring(start,end)).collect(Collectors.toList());
    }

    List<String> getSequences() {
        return sequences;
    }

    void setSequences(List<String> sequences) {
        this.sequences = sequences.stream().map(String::toUpperCase).collect(Collectors.toList());
    }

    public List<Long> getPositions() {
        return positions;
    }

    void setPositions(List<Long> positions){
        this.positions = positions;
    }
}
