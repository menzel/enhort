package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
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

/**
 * Implements the background model sites generation for a single track as covariance.
 * Scored or inout intervals are possible.
 * <p>
 * Created by Michael Menzel on 6/1/16.
 */
class SingleTrackBackgroundModel implements Sites {

    private final GenomeFactory.Assembly assembly;
    private transient MersenneTwister rand;
    private List<Long> positions = new ArrayList<>();
    private List<Character> strands = new ArrayList<>();

    /**
     * Contstructor
     */
    SingleTrackBackgroundModel(GenomeFactory.Assembly assembly) {
        this.assembly = assembly;
    }


    /**
     * Constructor for running sites against one interval
     *
     * @param track - interval to search against
     * @param sites    - sites to search
     */
    SingleTrackBackgroundModel(InOutTrack track, Sites sites, int minSites) {

        Intersect calc = new Intersect();
        TestTrackResult result = calc.searchSingleInterval(track, sites);
        assembly = sites.getAssembly();

        // TODO: factor is wrong, seems to be always 1
        int factor = (sites.getPositionCount() < minSites)? minSites/ sites.getPositionCount(): 1;

        positions.addAll(randPositions(result.getIn() * factor, track));
        positions.addAll(randPositions(result.getOut() * factor, Tracks.invert(track)));

        //positions.addAll(randPositions(result.getIn() * factor, interval, "in"));
        //positions.addAll(randPositions(result.getOut() * factor, interval, "out"));

        Collections.sort(positions); //sort again here after merging outside and inside positions

    }

    /**
     * Generates random positions which are either all inside or outside of the given intervals
     *
     * @param siteCount - count of random positions to be made up
     * @param track     - interval by which the in/out check is made
     * @return Collection of random positions
     */
    Collection<Long> randPositions(int siteCount, Track track) {

        rand  = new MersenneTwister();

        long maxValue = Tracks.sumOfIntervals(track);

        List<Long> randomValues = new ArrayList<>();
        List<Long> sites = new ArrayList<>();
        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();

        //get some random numbers
        for (int i = 0; i < siteCount; i++)
            randomValues.add(Math.round(Math.floor(rand.nextDouble() * maxValue)));

        Collections.sort(randomValues); // very important before streching to the genome!

        //strech random values to whole genome:
        int j = 0;
        long sumOfPrevious = 0; // remember sum of previous intervals.


        for (int i = 0; i < siteCount; i++) {
            Long r = randomValues.get(i) - sumOfPrevious;

            Long intervalSize = intervalEnd[j] - 1 - intervalStart[j];

            while(r >= intervalSize){
                r -= intervalSize;
                sumOfPrevious += intervalSize;
                j++;
                intervalSize = intervalEnd[j] - intervalStart[j];
            }

            sites.add(r + intervalStart[j]);
        }



        return sites;
    }


    @Override
    public void addPositions(Collection<Long> values) {
        this.positions.addAll(values);
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
        return this.positions.size();
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }
}
