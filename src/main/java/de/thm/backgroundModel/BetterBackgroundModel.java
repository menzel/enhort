package de.thm.backgroundModel;

import de.thm.genomeData.Interval;

import java.util.*;

/**
 * Created by Michael Menzel on 6/1/16.
 */
public class BetterBackgroundModel extends BackgroundModel{

    private Random rand;
    private final int factor = 10;

    public BetterBackgroundModel(int sitesIn, int sitesOut, Interval interval) {


        positions.addAll(randPositions(sitesIn * factor, interval, "in"));
        positions.addAll(randPositions(sitesOut * factor, interval, "out"));

    }

    /**
     * Generates random positions which are either all inside or outside of the given intervals
     *
     * @param siteCount - count of random positions to be made up
     * @param interval - interval by which the in/out check is made
     * @param mode - either the string "in" or "out". Controls the behavior of setting the rand positions in or outside of the intervals
     *
     * @return Collection of random positions
     */
    private Collection<? extends Long> randPositions(int siteCount, Interval interval, String mode) {

        int io = (mode.equals("in"))? 0: 1; //remember if rand positions should be in or outside of an interval

        rand = new Random(System.currentTimeMillis());
        long maxValue = sumOfIntervals(interval, mode);

        List<Long> randomValues = new ArrayList<>();
        List<Long> sites = new ArrayList<>();
        List<Long> intervalStart = interval.getIntervalsStart();
        List<Long> intervalEnd = interval.getIntervalsEnd();

        //get some random numbers
        for(int i = 0; i < siteCount; i++){
            Long r = Math.round(Math.floor(rand.nextDouble() * (maxValue+1)));
            randomValues.add(r);
        }

        Collections.sort(randomValues); // very important!

        //strech random values to whole genome:
        int j = 0;
        long sumOfPrevious = 0; // remember sum of previous intervals.

        for(int i = 0; i < siteCount; i++){
            long iStart = intervalStart.get(j + io); // io is 0 when the rand position should be inside an interval, 1 otherwise
            long iEnd = intervalEnd.get(j);
            long randV = randomValues.get(i) - sumOfPrevious; // substract sum of previous intervals. Since random values are in order this works.

            while(iEnd < iStart + randV && j < intervalStart.size()-(1+io)){ // if it does not fit in go to next interval and substract interval length from rand value
                j++;
                randV = randV  - (iEnd - iStart);
                sumOfPrevious += (iEnd - iStart);
                iStart = intervalStart.get(j + io);
                iEnd = intervalEnd.get(j);
            }

            if(io == 0) //if position should be inside the interval
                sites.add(iStart + randV);
            else //otherwise
                sites.add(iEnd + randV);
        }

        Collections.sort(sites); //don't forget this

        return sites;

    }

    /**
     * Sums up the size of all intervals. Either all intervals or the space between them
     *
     * @param interval - intervals to sum up
     * @param mode - either "in" or "out".
     *
     * @return sum of interval length inside or outside the intervals
     */
    private long sumOfIntervals(Interval interval, String mode) {

        long size = 0;
        int io = (mode.equals("in"))? 0: 1;

        List<Long> intervalStart = interval.getIntervalsStart();
        List<Long> intervalEnd = interval.getIntervalsEnd();

        for(int i = 0; i < intervalStart.size()-io; i++){
            if(mode.equals("in"))
                size += intervalEnd.get(i) - intervalStart.get(i);
            else
                size +=  intervalStart.get(i+1) - intervalEnd.get(i);
        }

        return size;
    }
}
