package de.thm.backgroundModel;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.Tracks;
import de.thm.positionData.Sites;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for Background models. Start here to generate a background model based on a count of sites or a list or a single
 * covariant.
 * <p>
 * <p>
 * Created by Michael Menzel on 23/2/16.
 */
public final class BackgroundModelFactory {

    private static final int maxCovariants = 2;
    private static final int maxCovariantsInOutOnly = 7;

    public static Sites createBackgroundModel(int positionCount) {
        return new RandomBackgroundModel(positionCount);

    }

    public static Sites createBackgroundModel(Track track, Sites sites) {
        if (track instanceof InOutTrack)
            return new SingleTrackBackgroundModel((InOutTrack) track, sites);

        else if (track instanceof ScoredTrack)
            return new ScoreMultiTrackBackgroundModel(Collections.singletonList((ScoredTrack) track), sites);


        return null;
    }

    public static Sites createBackgroundModel(List<Track> trackList, Sites sites) throws CovariantsException {
        if (trackList.isEmpty())
            return createBackgroundModel(sites.getPositionCount());

        else if (trackList.size() == 1)
            return createBackgroundModel(trackList.get(0), sites);

        else if (trackList.stream().allMatch(i -> i instanceof InOutTrack)) //check for maxCovariantsInOut
            return new MultiTrackBackgroundModel(trackList, sites);

        else if (trackList.size() <= maxCovariants) {
            if (trackList.stream().allMatch(i -> i instanceof ScoredTrack)) {
                List<ScoredTrack> newList = trackList.stream().map(i -> (ScoredTrack) i).collect(Collectors.toList());

                return new ScoreMultiTrackBackgroundModel(newList, sites);

            } else {
                List<ScoredTrack> scoredIntervals = trackList.stream()
                        .filter(i -> i instanceof ScoredTrack)
                        .map(i -> (ScoredTrack) i)
                        .collect(Collectors.toList());

                //convert all non score intervals to score interval
                scoredIntervals.addAll(trackList.stream()
                        .filter(i -> i instanceof InOutTrack)
                        .map(i -> (InOutTrack) i)
                        .map(Tracks::cast)
                        .collect(Collectors.toList()));


                return new ScoreMultiTrackBackgroundModel(scoredIntervals, sites);
            }

        } else {
            throw new CovariantsException("Too many covariants");
        }

    }
}
