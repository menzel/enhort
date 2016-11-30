package de.thm.backgroundModel;

import de.thm.calc.Distances;
import de.thm.genomeData.DistanceTrack;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.Tracks;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculates a distances background model for a single distance track
 *
 * Created by menzel on 10/10/16.
 */
class DistanceBackgroundModel implements Sites {

    private final GenomeFactory.Assembly assembly;
    private transient MersenneTwister rand;
    private List<Long> positions;


    DistanceBackgroundModel(GenomeFactory.Assembly assembly, DistanceTrack track, Sites sites){
        this.assembly = assembly;

        rand  = new MersenneTwister();

        //generate positions inside
        List<Long> distHist  = generateDistanceHist(track, sites);
        int count = distHist.size(); //(sites.getPositionCount() > 10000) ? sites.getPositionCount() : 10000;
        positions = generatePositions(distHist, track, count);

        //generate outside positions
        SingleTrackBackgroundModel outsideModel = new SingleTrackBackgroundModel(sites.getAssembly());
        InOutTrack ousideTrack = Tracks.invert(Tracks.convertByRange(track, 5000));
        positions.addAll(outsideModel.randPositions(sites.getPositionCount()- distHist.size(), ousideTrack));

        Collections.sort(positions);
    }

    private List<Long> generatePositions(List<Long> distances, Track track, int count) {

        List<Long> positions = new ArrayList<>();
        List<Long> upstream = distances.stream().filter(i -> i < 0).sorted().collect(Collectors.toList());
        List<Long> downstream = distances.stream().filter(i -> i >= 0).sorted().collect(Collectors.toList());
        Collections.reverse(downstream); // reverse downstream list to begin with the farest distances to the postion. upstream is already in order

        int i = 0;

        while(i <= count){

            //get random start:
            int id = 1 + (int) Math.floor(rand.nextDouble() * (track.getStarts().size()-2));
            long start = track.getStarts().get(id);

            if(rand.nextBoolean()){
                long prev = track.getStarts().get(id-1);
                long dist_prev = (prev - start)/2;

                for(Long dist: upstream)
                    if(dist > dist_prev){
                        //set new pos:
                        positions.add(start + dist); //dist is negative in this branch
                        distances.remove(dist); // ?
                        break;
                    }

            } else {

                long fol = track.getStarts().get(id+1);
                long dist_fol = (fol - start)/2;

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

        return  distances.stream().filter(i -> i < 5000 && i > -5000).collect(Collectors.toList());
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

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }
}
