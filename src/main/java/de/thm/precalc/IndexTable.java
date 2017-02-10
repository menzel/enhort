package de.thm.precalc;

import de.thm.genomeData.Track;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Matrix-like table
 *
 * Created by menzel on 2/8/17.
 */
class IndexTable {

    private Map<Track, List<Integer>> props = new ConcurrentHashMap<>();
    private List<Long> positions = new ArrayList<>();
    private List<String> sequences = new ArrayList<>();

    Pair<List<Long>, List<Integer>> getByTrack(Track track){

        return new ImmutablePair<>(positions, props.get(track));
    }

    void setProperties(Track track, List<Integer> vals){
        props.put(track, vals);
    }

    List<String> getSequences() {
        return sequences;
    }

    void setSequences(List<String> sequences) {
        this.sequences = sequences;
    }

    public List<Long> getPositions() {
        return positions;
    }

    void setPositions(List<Long> positions){
        this.positions = positions;
    }
}
