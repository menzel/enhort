package de.thm.backgroundModel;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.*;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.positionData.Sites;

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

    private static final int maxCovariants = 4;
    private static final int maxCovariantsInOutOnly = 10;

    /**
     * Creates a random backgroundmodel of given size.
     *
     * @param positionCount - count of random positions to create.
     * @return background model as sites object.
     */
    public static Sites createBackgroundModel(GenomeFactory.Assembly assembly, int positionCount) {
        if(positionCount < 10000)
            positionCount = 10000;
        return new RandomBackgroundModel(assembly, positionCount);
    }


    /**
     * Creates a background model based on a sequence logo through @see backgroundModel.LogoBackgroundModel
     *
     * @param assembly - assembly nr
     * @param logo  - sequencelogo @see logo.Logo
     * @param positionCount - count of positions to set
     *
     * @return sites with the given logo
     */
    public static Sites createBackgroundModel(GenomeFactory.Assembly assembly, Logo logo, int positionCount) {

        return new LogoBackgroundModel(assembly, logo, positionCount);
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
        return  createBackgroundModel(track,sites,minSites,1);
    }

    /**
     * Creates a background model based on one track as covariant, the given sites and a minimum of sites to create.
     *
     * @param track - covariant track
     * @param sites - sites to set the probabilities for the background positions
     * @param minSites - minimum expected sites count
     * @param influence - influence of the model on positions (between 0 and 1)
     *
     * @return background model as sites object.
     */
    public static Sites createBackgroundModel(Track track, Sites sites, int minSites, double influence) {
        if (track instanceof InOutTrack)
            return new SingleTrackBackgroundModel((InOutTrack) track, sites,minSites);
        else if (track instanceof ScoredTrack) // put single track in a list of size one
            return new ScoreBackgroundModel((ScoredTrack) track, sites, minSites, influence);
        else if (track instanceof NamedTrack) //convert the single track to a scored track and put in a list of size one
             return new ScoreBackgroundModel(Tracks.cast((NamedTrack) track), sites, minSites, influence);
        else if (track instanceof DistanceTrack)
             return new DistanceBackgroundModel((DistanceTrack) track, sites, 200);
        return null;
    }



    /**
     * Creates a background model with a list of tracks as covariants.
     *
     * @param trackList - list of covariant tracks
     * @param sites - sites to set the probabilities for the background positions
     * @return background model as sites object.
     * @throws CovariantsException
     */
    public static Sites createBackgroundModel(List<Track> trackList, Sites sites, double influence) throws CovariantsException {
        return createBackgroundModel(trackList, sites, sites.getPositionCount(), influence);

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
    public static Sites createBackgroundModel(List<Track> trackList, Sites sites, int minSites, double influence) throws CovariantsException {

        if (trackList.isEmpty())
            return createBackgroundModel(sites.getAssembly(), sites.getPositionCount());

        else if (trackList.size() == 1)
            return createBackgroundModel(trackList.get(0), sites, minSites, influence);

        else if (trackList.stream().allMatch(i -> i instanceof InOutTrack))
            if(trackList.size() < maxCovariantsInOutOnly) {
                return new MultiTrackBackgroundModel(trackList, sites, minSites);
            } else throw new CovariantsException("Too many covariants: " + trackList.size() + ". Max " + maxCovariantsInOutOnly + " are allowed");

        else if (trackList.size() <= maxCovariants) {

            if (trackList.stream().allMatch(i -> i instanceof ScoredTrack)) {
                List<ScoredTrack> newList = trackList.stream().map(i -> (ScoredTrack) i).collect(Collectors.toList());

                return new ScoreBackgroundModel(newList, sites, minSites, influence);

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

                scoredIntervals.addAll(trackList.stream()
                    .filter(i -> i instanceof NamedTrack)
                    .map(i -> (NamedTrack) i)
                    .map(Tracks::cast)
                    .collect(Collectors.toList()));


                return new ScoreBackgroundModel(scoredIntervals, sites, minSites, influence);
            }

        } else {
            throw new CovariantsException("Too many covariants. " + trackList.size() + ". Only " + maxCovariantsInOutOnly + " are allowed");
        }
    }
}
