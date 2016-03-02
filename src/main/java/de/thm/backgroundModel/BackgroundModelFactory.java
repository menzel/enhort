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
 * <p>
 * Factory for Background models. Start here to generate a background model based on a count of sites or a list or a single
 * covariant.
 * </p>
 * Created by Michael Menzel on 23/2/16.
 */
public final class BackgroundModelFactory {

    private static final int maxCovariants = 2;
    private static final int maxCovariantsInOutOnly = 10;

    /**
     * Creates a random backgroundmodel of given size.
     *
     * @param positionCount - count of random positions to create.
     * @return background model as sites object.
     */
    public static Sites createBackgroundModel(int positionCount) {
        return new RandomBackgroundModel(positionCount);

    }

    /**
     * Creates a background model based on one track as covariant, the given sites and a minimum of sites to create.
     *
     * @param track - covariant track
     * @param sites - sites to set the probabilities for the background positions
     * @param minSites - minimum expected sites count
     *
     * @return background model as sites object.
     */
    public static Sites createBackgroundModel(Track track, Sites sites, int minSites) {
         if (track instanceof InOutTrack)
            return new SingleTrackBackgroundModel((InOutTrack) track, sites,minSites);

        else if (track instanceof ScoredTrack)
            return new ScoreMultiTrackBackgroundModel(Collections.singletonList((ScoredTrack) track), sites, minSites);

        return null;
    }

    /**
     * Creates a background model based on one track as covariant and the given sites.
     *
     * @param track covariant track
     * @param sites - sites to set the probabilities for the background positions
     * @return background model as sites object.
     */
    public static Sites createBackgroundModel(Track track, Sites sites) {
        return  createBackgroundModel(track, sites, sites.getPositionCount());
    }


    /**
     * Creates a background model with a list of tracks as covariants.
     *
     * @param trackList - list of covariant tracks
     * @param sites - sites to set the probabilities for the background positions
     * @return background model as sites object.
     * @throws CovariantsException
     */
    public static Sites createBackgroundModel(List<Track> trackList, Sites sites) throws CovariantsException {
        return createBackgroundModel(trackList, sites, sites.getPositionCount());

    }

    /**
     * Creates a background model with a given list of covariants and sites and a minimum of sites to create.
     *
     * @param trackList - list of covariants. If the list is of size one createBackgroundModel(trackList.get(0), sites,minSites) is called
     * @param sites - sites to set the probabilities for the background positions
     * @param minSites - minimum expected sites count
     * @return background model as sites object.
     * @throws CovariantsException
     */
    public static Sites createBackgroundModel(List<Track> trackList, Sites sites, int minSites) throws CovariantsException {
        if (trackList.isEmpty())
            return createBackgroundModel(sites.getPositionCount());

        else if (trackList.size() == 1)
            return createBackgroundModel(trackList.get(0), sites,minSites);

        else if (trackList.stream().allMatch(i -> i instanceof InOutTrack))
            if(trackList.size() < maxCovariantsInOutOnly) {
                return new MultiTrackBackgroundModel(trackList, sites, minSites);
            } else throw new CovariantsException("Too many covariants. Only " + maxCovariantsInOutOnly + " are allowed");

        else if (trackList.size() <= maxCovariants) {
            if (trackList.stream().allMatch(i -> i instanceof ScoredTrack)) {
                List<ScoredTrack> newList = trackList.stream().map(i -> (ScoredTrack) i).collect(Collectors.toList());

                return new ScoreMultiTrackBackgroundModel(newList, sites, minSites);

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


                return new ScoreMultiTrackBackgroundModel(scoredIntervals, sites, minSites);
            }

        } else {
            throw new CovariantsException("Too many covariants");
        }
    }
}
