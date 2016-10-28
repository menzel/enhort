package de.thm.backgroundModel;

import de.thm.calc.Distances;
import de.thm.genomeData.Track;
import de.thm.positionData.Sites;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by menzel on 10/10/16.
 */
public class DistanceBackgroundModel implements Sites {

    private transient MersenneTwister rand;
    private List<Long> positions;


    DistanceBackgroundModel(Track track, Sites sites){

        rand  = new MersenneTwister();
        int count = sites.getPositionCount(); //(sites.getPositionCount() > 10000) ? sites.getPositionCount() : 10000;

        positions = generatePositions(generateDistanceHist(track, sites), track, count);
        Collections.sort(positions);

    }

    private List<Long> generatePositions(List<Long> distances, Track track, int count) {

        List<Long> positions = new ArrayList<>();
        //Collections.sort(distances);
        //Collections.reverse(distances);

        List<Long> upsteam = distances.stream().filter(i -> i < 0).sorted().collect(Collectors.toList());
        Collections.reverse(upsteam);
        List<Long> downstream = distances.stream().filter(i -> i >= 0).sorted().collect(Collectors.toList());
        Collections.reverse(downstream);

        int i = 0;

        while(i <= count){
            //get random start:
            int id = 1 + (int) Math.floor(rand.nextDouble() * (track.getStarts().size()-2));

            long start = track.getStarts().get(id);
            long prev = track.getStarts().get(id-1);
            long fol = track.getStarts().get(id+1);

            long dist_prev = (start - prev)/2;
            long dist_fol = (fol - start)/2;

            if(dist_fol < dist_prev){ // prev is bigger
                for(Long dist: upsteam)
                    if(dist < dist_prev){
                        //set new pos:
                        positions.add(start + dist);
                        distances.remove(dist); // ?
                        break;
                    }

            } else {
                for(Long dist: downstream)
                    if(dist < dist_fol){
                        //set new pos:
                        positions.add(start + dist);
                        distances.remove(dist); // ?
                        break;
                    }
            }

            i++;
        }

        return positions;
    }

    private List<Long> generateDistanceHist(Track track, Sites sites) {
        Distances dist = new Distances();
        List<Long> distances = new ArrayList<>();

        distances.addAll(dist.distancesToNext(track, sites));

        return  distances.stream().filter(i -> i < 5000).collect(Collectors.toList());
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
