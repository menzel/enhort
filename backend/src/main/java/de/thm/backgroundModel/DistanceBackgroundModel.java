package de.thm.backgroundModel;

import de.thm.calc.Distances;
import de.thm.genomeData.tracks.DistanceTrack;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.Tracks;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.apache.commons.math3.distribution.NormalDistribution;
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

    private final Genome.Assembly assembly;
    private final transient MersenneTwister rand;
    private List<Long> positions;
    private List<Character> strands = new ArrayList<>();


    DistanceBackgroundModel(DistanceTrack track, Sites sites, int standardDeviation){
        this.assembly = sites.getAssembly();

        rand  = new MersenneTwister();

        //generate positions inside
        List<Long> distHist  = generateDistanceHist(track, sites);
        int count = distHist.size(); //(sites.getPositionCount() > 10000) ? sites.getPositionCount() : 10000;
        positions = generatePositions(distHist, track, count, standardDeviation);

        //generate outside positions
        SingleTrackBackgroundModel outsideModel = new SingleTrackBackgroundModel(sites.getAssembly());
        InOutTrack ousideTrack = Tracks.invert(Tracks.convertByRange(track, 5000));
        positions.addAll(outsideModel.randPositions(sites.getPositionCount()- distHist.size(), ousideTrack));

        Collections.sort(positions);
    }

    private List<Long> generatePositions(List<Long> distances, Track track, int count, int standardDeviation) {

        List<Long> positions = new ArrayList<>();
        List<Long> upstream = distances.parallelStream().filter(i -> i < 0).sorted().collect(Collectors.toList());
        List<Long> downstream = distances.parallelStream().filter(i -> i >= 0).sorted().collect(Collectors.toList());
        Collections.reverse(downstream); // reverse downstream list to begin with the farest distances to the postion. upstream is already in order
        NormalDistribution nd = new NormalDistribution(0,standardDeviation);

        int i = 0;

        while(i <= count){

            //get random start:
            int id = 1 + (int) Math.floor(rand.nextDouble() * (track.getStarts().length - 2));
            long start = track.getStarts()[id];

            if(rand.nextBoolean()){ // set the new site up oder downstream of the random position by random
                long prev = track.getStarts()[id - 1];
                long dist_prev = (prev - start)/2;

                for(Long dist: upstream)
                    if(dist > dist_prev){
                        //set new pos:
                        long f = (long) nd.sample();
                        positions.add(start + dist + f); //dist is negative in this branch
                        upstream.remove(dist);
                        break;
                    }

            } else {

                long fol = track.getStarts()[id + 1];
                long dist_fol = (fol - start)/2;

                for(Long dist: downstream)
                    if(dist < dist_fol){
                        //set new pos:
                        long f = (long) nd.sample();
                        positions.add(start + dist + f); //dist is negative in this branch
                        downstream.remove(dist);
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

        return distances.parallelStream().filter(i -> i < 5000 && i > -5000).collect(Collectors.toList());
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
    public List<Character> getStrands() {
        return strands;
    }

    @Override
    public int getPositionCount() {
        return positions.size();
    }

    @Override
    public Genome.Assembly getAssembly() {
        return this.assembly;
    }
}