package de.thm.backgroundModel;

import de.thm.exception.CovariantsException;
import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 23/2/16.
 */
public final class BackgroundModelFactory {

    private static final int maxCovariants = 7;

    public static Sites createBackgroundModel(int positionCount){
        return new RandomBackgroundModel(positionCount);

    }

    public static Sites createBackgroundModel(Interval interval, Sites sites) throws Exception {
        if(interval.getType() == Interval.Type.inout)
            try {
                return new SingleTrackBackgroundModel(interval,sites);
            } catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
                intervalTypeNotAllowedExcpetion.printStackTrace();
            }
        else if(interval.getType() == Interval.Type.score)
            throw new Exception("Not impl yet");

        return null;
    }

    public static Sites createBackgroundModel(List<Interval> intervalList, Sites sites) throws CovariantsException {

        if(intervalList.size() < maxCovariants) {
            if (intervalList.stream().allMatch(i -> i.getType() == Interval.Type.score))
                return new ScoreMultiTrackBackgroundModel(intervalList, sites);

            else {
                List<Interval> scoredIntervals = intervalList.stream()
                        .filter(i -> i.getType() == Interval.Type.score)
                        .collect(Collectors.toList());

                //convert all non score intervals to score interval
                scoredIntervals.addAll(intervalList.stream()
                        .filter(i -> i.getType() == Interval.Type.inout)
                        .map(Intervals::convertToScore)
                        .collect(Collectors.toList()));


                return new ScoreMultiTrackBackgroundModel(scoredIntervals, sites);
            }

        } else {
            throw new CovariantsException("Too many covariants");
        }

    }
}
