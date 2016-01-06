package de.thm.positionData;

import de.thm.genomeData.Interval;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Michael Menzel on 6/1/16.
 */
public class BetterBackgroundModel extends BackgroundModel{

    private Random rand;

    public BetterBackgroundModel(int sitesIn, int sitesOut, Interval interval) {

        rand = new Random(System.currentTimeMillis());

        long maxValue = sumOfIntervals(interval);

    }

    private long sumOfIntervals(Interval interval) {

        long size = 0;


        ArrayList<Long> intervalStart = interval.getIntervalsStart();
        ArrayList<Long> intervalEnd = interval.getIntervalsEnd();

        for(int i = 0; i < intervalStart.size(); i++){
            size += intervalEnd.get(i) - intervalStart.get(i);
        }

        return size;
    }
}
