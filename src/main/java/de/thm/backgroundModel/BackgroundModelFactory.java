package de.thm.backgroundModel;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.InOutInterval;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.genomeData.ScoredTrack;
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
    private static final int maxCovariantsInOutOnly = 7;

    public static Sites createBackgroundModel(int positionCount){
        return new RandomBackgroundModel(positionCount);

    }

    public static Sites createBackgroundModel(Interval interval, Sites sites) {
        if(interval instanceof InOutInterval)
            return new SingleTrackBackgroundModel((InOutInterval) interval,sites);

        else if(interval instanceof ScoredTrack)
            return new ScoreMultiTrackBackgroundModel(Collections.singletonList((ScoredTrack) interval), sites);


        return null;
    }

    public static Sites createBackgroundModel(List<Interval> intervalList, Sites sites) throws CovariantsException {
        if(intervalList.isEmpty())
            return createBackgroundModel(sites.getPositionCount());

        else if(intervalList.size() == 1)
            return createBackgroundModel(intervalList.get(0), sites);

        else if (intervalList.stream().allMatch(i -> i instanceof InOutInterval)) //check for maxCovariantsInOut
                return new MultiTrackBackgroundModel(intervalList, sites);

        else if(intervalList.size() <= maxCovariants) {
            if (intervalList.stream().allMatch(i -> i instanceof ScoredTrack)) {
                List<ScoredTrack> newList = intervalList.stream().map(i -> (ScoredTrack) i).collect(Collectors.toList());

                return new ScoreMultiTrackBackgroundModel(newList, sites);

            } else {
                List<ScoredTrack> scoredIntervals = intervalList.stream()
                        .filter(i -> i instanceof ScoredTrack)
                        .map(i -> (ScoredTrack) i )
                        .collect(Collectors.toList());

                //convert all non score intervals to score interval
                scoredIntervals.addAll(intervalList.stream()
                        .filter(i -> i instanceof InOutInterval)
                        .map(i -> (InOutInterval) i)
                        .map(Intervals::cast)
                        .collect(Collectors.toList()));


                return new ScoreMultiTrackBackgroundModel(scoredIntervals, sites);
            }

        } else {
            throw new CovariantsException("Too many covariants");
        }

    }
}
