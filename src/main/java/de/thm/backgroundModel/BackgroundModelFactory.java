package de.thm.backgroundModel;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Interval.Type;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for Background models. Start here to generate a background model based on a count of sites or a list or a single
 * covariant.
 *
 *
 * Created by Michael Menzel on 23/2/16.
 */
public final class BackgroundModelFactory {

    private static final int maxCovariants = 2;

    public static Sites createBackgroundModel(int positionCount){
        return new RandomBackgroundModel(positionCount);

    }

    public static Sites createBackgroundModel(Interval interval, Sites sites) {
        if(interval.getType() == Type.inout)
            return new SingleTrackBackgroundModel(interval,sites);

        else if(interval.getType() == Type.score)
            return new ScoreMultiTrackBackgroundModel(Collections.singletonList(interval), sites);


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
