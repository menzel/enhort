package de.thm.backgroundModel;

import de.thm.calc.IntersectCalculate;
import de.thm.calc.IntersectResult;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.Tracks;
import de.thm.positionData.Sites;

import java.util.*;

/**
 * Implements the background model sites generation for a single track as covariance.
 * Scored or inout intervals are possible.
 * <p>
 * Created by Michael Menzel on 6/1/16.
 */
class SingleTrackBackgroundModel implements Sites {

    private Random rand;
    private List<Long> positions = new ArrayList<>();

    /**
     * Contstructor
     */
    SingleTrackBackgroundModel() {
    }


    /**
     * Constructor for running sites against one interval
     *
     * @param interval - interval to search against
     * @param sites    - sites to search
     */
    SingleTrackBackgroundModel(InOutTrack interval, Sites sites) {

        IntersectCalculate calc = new IntersectCalculate();
        IntersectResult result = calc.searchSingleInterval(interval, sites);

        int factor = (sites.getPositionCount() < 10000)? 10000 / sites.getPositionCount(): 1;

        positions.addAll(randPositions(result.getIn() * factor, interval, "in"));
        positions.addAll(randPositions(result.getOut() * factor, interval, "out"));

    }


    /**
     * Generates random positions which are either all inside or outside of the given intervals
     *
     * @param siteCount - count of random positions to be made up
     * @param track     - interval by which the in/out check is made
     * @param mode      - either the string "in" or "out". Controls the behavior of setting the rand positions in or outside of the intervals
     * @return Collection of random positions
     */
    Collection<Long> randPositions(int siteCount, Track track, String mode) {

        int io = (mode.equals("in")) ? 0 : 1; //remember if rand positions should be in or outside of an interval

        rand = new Random(System.currentTimeMillis());
        long maxValue = Tracks.sumOfIntervals(track, mode);

        List<Long> randomValues = new ArrayList<>();
        List<Long> sites = new ArrayList<>();
        List<Long> intervalStart = track.getIntervalsStart();
        List<Long> intervalEnd = track.getIntervalsEnd();

        //get some random numbers
        for (int i = 0; i < siteCount; i++) {
            Long r = Math.round(Math.floor(rand.nextDouble() * (maxValue)));
            randomValues.add(r);
        }

        Collections.sort(randomValues); // very important!

        //strech random values to whole genome:
        int j = 0;
        long sumOfPrevious = 0; // remember sum of previous intervals.

        for (int i = 0; i < siteCount; i++) {
            long iStart = intervalStart.get(j + io); // io is 0 when the rand position should be inside an interval, 1 otherwise
            long iEnd = intervalEnd.get(j);
            long randV = randomValues.get(i) - sumOfPrevious; // substract sum of previous intervals. Since random values are in order this works.

            while (iEnd - 1 < iStart + randV && j < intervalStart.size() - (1 + io)) { // if it does not fit in go to next interval and substract interval length from rand value
                j++;
                randV = randV - (iEnd - iStart);
                sumOfPrevious += (iEnd - iStart);
                iStart = intervalStart.get(j + io);
                iEnd = intervalEnd.get(j);
            }

            if (io == 0) //if position should be inside the interval
                sites.add(iStart + randV);
            else //otherwise
                sites.add(iEnd + randV);
        }

        Collections.sort(sites); //don't forget this

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
    public int getPositionCount() {
        return this.positions.size();
    }
}
