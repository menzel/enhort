package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.Tracks;
import de.thm.misc.Genome;
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
class SingleTrackBackgroundModel {

    /**
     * Constructor for running sites against one interval
     *
     * @param track - interval to search against
     * @param sites    - sites to search
     */
    static BackgroundModel singleTrackBackgroundModel(InOutTrack track, Sites sites, int minSites) {

        List<Long> positions = new ArrayList<>();

        Intersect calc = new Intersect();
        TestTrackResult result = calc.searchSingleInterval(track, sites);

        // TODO: factor is wrong, seems to be always 1
        int factor = (sites.getPositionCount() < minSites)? minSites/ sites.getPositionCount(): 1;

        Track contigs = TrackFactory.getInstance().getTrackByName("Contigs", sites.getAssembly());

        positions.addAll(randPositions(result.getIn() * factor, Tracks.intersect(track, contigs)));
        positions.addAll(randPositions(result.getOut() * factor, Tracks.intersect(Tracks.invert(track), contigs)));

        Collections.sort(positions); //sort again here after merging outside and inside positions

        return new BackgroundModel(positions, sites.getAssembly());
    }

    /**
     * Generates random positions which are either all inside or outside of the given intervals.
     * The given track is always interesected with the given contigs track
     *
     *
     * @param siteCount - count of random positions to be made up
     * @param track     - interval by which the in/out check is made
     *
     * @return Collection of random positions
     */
    static Collection<Long> randPositions(int siteCount, Track track) {

        MersenneTwister rand;
        rand  = new MersenneTwister();

        long maxValue = Tracks.sumOfIntervals(track);

        List<Long> randomValues = new ArrayList<>();
        List<Long> sites = new ArrayList<>();
        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();

        //get some random numbers
        for (int i = 0; i < siteCount; i++)
            randomValues.add((long) Math.floor(rand.nextDouble() * maxValue));

        Collections.sort(randomValues); // very important before streching to the genome!

        //strech random values to whole genome:
        int j = 0;
        long sumOfPrevious = 0; // remember sum of previous intervals.


        for (int i = 0; i < siteCount; i++) {
            Long r = randomValues.get(i) - sumOfPrevious;

            Long intervalSize = intervalEnd[j] - intervalStart[j];

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

}
