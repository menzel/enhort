package de.thm.backgroundModel;

import de.thm.calc.Distances;
import de.thm.genomeData.Track;
import de.thm.positionData.Sites;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by menzel on 10/10/16.
 */
public class DistanceBackgroundModel implements Sites {

    private transient MersenneTwister rand;
    private List<Long> positions;


    DistanceBackgroundModel(List<Track> tracks, Sites sites){

        rand  = new MersenneTwister();

        HashMap<Track, List<Long>> distances = new HashMap<>();

        for(Track track: tracks)
            distances.put(track, generateDistanceHist(track, sites));

        int count = sites.getPositionCount(); //(sites.getPositionCount() > 10000) ? sites.getPositionCount() : 10000;

        positions = generatePositions(distances, count);

    }

    private List<Long> generatePositions(HashMap<Track, List<Long>> distances, int count) {
        List<Long> positions = new ArrayList<>();

        for(Track track: distances.keySet()){
            for(int i = 0 ; i < count/distances.size(); i++) {

                List<Long> dist = distances.get(track);

                //get random start:
                long start = track.getStarts().get((int) Math.floor(rand.nextDouble() * track.getStarts().size()));

                //get random distance
                long offset = dist.get((int) Math.floor(rand.nextDouble() * dist.size()));

                positions.add(start + offset);
            }
        }

        return positions;
    }

    private List<Long> generateDistanceHist(Track track, Sites sites) {
        Distances dist = new Distances();
        List<Long> distances = new ArrayList<>();

        distances.addAll(dist.distancesToNext(track, sites));

        return  distances.stream().filter(i -> i < 15000).collect(Collectors.toList());
    }


    @Override
    public void addPositions(Collection<Long> values) {
        positions.addAll(values);
    }

    @Override
    public List<Long> getPositions() {
        return this.positions;
    }

    @Override
    public void setPositions(List<Long> positions) {
        this.positions = positions;
    }

    @Override
    public int getPositionCount() {
        return positions.size();
    }
}
