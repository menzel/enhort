package de.thm.precalc;

import de.thm.genomeData.Track;
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

    private Map<Track, List<Integer>> props = new ConcurrentHashMap<>();
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

    List<String> getSequences(int l) {
        int start = sequences.get(0).length()/2 - l/2;
        int end = sequences.get(0).length()/2 + l/2;

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
