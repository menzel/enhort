package de.thm.backgroundModel;

import de.thm.exception.CovariantsException;
import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.genomeData.Intervals.Type;
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

    public static Sites createBackgroundModel(Interval interval, Sites sites) {
        if(interval.getType() == Type.inout)
            try {
                return new SingleTrackBackgroundModel(interval,sites);
            } catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
                intervalTypeNotAllowedExcpetion.printStackTrace();
            }
        else if(interval.getType() == Type.score)
            try {
                throw new Exception("Not impl yet");
            } catch (Exception e) {
                e.printStackTrace();
            }

        return null;
    }

    public static Sites createBackgroundModel(List<Interval> intervalList, Sites sites) throws CovariantsException {
        if(intervalList.isEmpty())
            return createBackgroundModel(sites.getPositionCount());
        else if(intervalList.size() == 1)
            return createBackgroundModel(intervalList.get(0), sites);

        if(intervalList.size() < maxCovariants) {
            if (intervalList.stream().allMatch(i -> i.getType() == Type.score))
                return new ScoreMultiTrackBackgroundModel(intervalList, sites);

            else if (intervalList.stream().allMatch(i -> i.getType() == Type.inout)) {

                return new MultiTrackBackgroundModel(intervalList, sites);

            } else {
                List<Interval> scoredIntervals = intervalList.stream()
                        .filter(i -> i.getType() == Type.score)
                        .collect(Collectors.toList());

                //convert all non score intervals to score interval
                scoredIntervals.addAll(intervalList.stream()
                        .filter(i -> i.getType() == Type.inout)
                        .map(Intervals::convertToScore)
                        .collect(Collectors.toList()));


                return new ScoreMultiTrackBackgroundModel(scoredIntervals, sites);
            }

        } else {
            throw new CovariantsException("Too many covariants");
        }

    }
}
