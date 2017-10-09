package de.thm.backgroundModel;

import de.thm.exception.CovariantsException;
import de.thm.exception.TrackTypeNotAllowedExcpetion;
import de.thm.genomeData.tracks.*;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
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
    private static final ExecutorService exe = Executors.newSingleThreadExecutor();
    private static final Logger logger = LoggerFactory.getLogger(BackgroundModelFactory.class);

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
     * Creates a background model based on one track as covariant, the given sites and a minimum of sites to create.
     *
     * @param track - covariant track
     * @param sites - sites to set the probabilities for the background positions
     * @param minSites - minimum expected sites count
     *
     * @return background model as sites object.
     */
    public static Sites createBackgroundModel(Track track, Sites sites, int minSites) throws TrackTypeNotAllowedExcpetion {
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
    private static Sites createBackgroundModel(final Track track, final Sites sites, final int minSites, final double influence) throws TrackTypeNotAllowedExcpetion {

        if (track instanceof InOutTrack)
            new SingleTrackBackgroundModel((InOutTrack) track, sites, minSites);
        else if (track instanceof ScoredTrack) // put single track in a list of size one
            new ScoreBackgroundModel((ScoredTrack) track, sites, minSites, influence);
        else if (track instanceof NamedTrack) //convert the single track to a scored track and put in a list of size one
           new ScoreBackgroundModel(Tracks.cast((NamedTrack) track), sites, minSites, influence);
        else if (track instanceof DistanceTrack)
           new DistanceBackgroundModel((DistanceTrack) track, sites, 200);
        else if (track instanceof StrandTrack)
            new RandomBackgroundModel(track.getAssembly(), minSites); // TODO add missing strandTrack BG model
        throw new TrackTypeNotAllowedExcpetion("Type of " + track  + " unkonwn");
    }

    /**
     * Creates a background model with a given list of covariants and sites and a minimum of sites to create.
     *
     * @param trackList - list of covariants. If the list is of size one createBackgroundModel(trackList.get(0), sites,minSites) is called
     * @param sites - sites to set the probabilities for the background positions
     * @param minSites - minimum expected sites count
     * @return background model as sites object.
     * @throws CovariantsException - if there are too many covariants
     */
    public static Sites createBackgroundModel(List<Track> trackList, Sites sites, int minSites, double smooth) throws CovariantsException, TrackTypeNotAllowedExcpetion {

        int finalMinSites = (int) (minSites * 1.05);

        Future<?> f;

        if (trackList.isEmpty())
            f = exe.submit(() -> { createBackgroundModel(sites.getAssembly(), sites.getPositionCount());});

        else if (trackList.size() == 1)
            f = exe.submit(() -> {createBackgroundModel(trackList.get(0), sites, finalMinSites, smooth);});

        else if (trackList.stream().allMatch(i -> i instanceof InOutTrack))
            if(trackList.size() < maxCovariantsInOutOnly) {
                f = exe.submit(() -> { new MultiTrackBackgroundModel(trackList, sites, finalMinSites);});
            } else throw new CovariantsException("Too many covariants: " + trackList.size() + ". Max " + maxCovariantsInOutOnly + " are allowed");

        else if (trackList.size() <= maxCovariants) {

            if (trackList.stream().allMatch(i -> i instanceof ScoredTrack)) {
                System.err.println("started scored track bg");
                List<ScoredTrack> newList = trackList.stream().map(i -> (ScoredTrack) i).collect(Collectors.toList());

                f = exe.submit(() -> {  new ScoreBackgroundModel(newList, sites, finalMinSites, smooth);});
                System.err.println("end scored track bg");

            } else {
                System.err.println("casting started");
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

                System.err.println("casting finished");

                f = exe.submit(() -> {new ScoreBackgroundModel(scoredIntervals, sites, minSites, smooth);});
            }

        } else {
            throw new CovariantsException("Too many covariants. " + trackList.size() + ". Only " + maxCovariantsInOutOnly + " are allowed");
        }

        try {
           Object bg = f.get(300, TimeUnit.SECONDS);

           return (Sites) bg;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Error while creating the background model",e);
        }

        throw new RuntimeException("Background model not created in BackgroundModelFactory");
    }
}
