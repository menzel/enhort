// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.backgroundModel;

import de.thm.exception.CovariatesException;
import de.thm.exception.TrackTypeNotAllowedExcpetion;
import de.thm.genomeData.tracks.*;
import de.thm.misc.Genome;
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
    public static Sites createBackgroundModel(Genome.Assembly assembly, int positionCount) {
        if(positionCount < 10000)
            positionCount = 10000;

        return RandomBackgroundModel.create(assembly, positionCount);
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
            return SingleTrackBackgroundModel.create((InOutTrack) track, sites, minSites);
        else if (track instanceof ScoredTrack) // put single track in a list of size one
            return ScoreBackgroundModel.scoreBackgroundModel((ScoredTrack) track, sites, minSites, influence);
        else if (track instanceof NamedTrack) //convert the single track to a scored track and put in a list of size one
            return NamedBackgroundModel.create(sites, minSites, (NamedTrack) track);
        else if (track instanceof DistanceTrack)
           return DistanceBackgroundModel.create((DistanceTrack) track, sites, 200);
        else if (track instanceof StrandTrack)
            return RandomBackgroundModel.create(track.getAssembly(), minSites); // TODO add missing strandTrack BG model
        throw new TrackTypeNotAllowedExcpetion("Type of " + track  + " unkonwn");
    }

    /**
     * Creates a background model with a given list of covariants and sites and a minimum of sites to create.
     *
     * @param trackList - list of covariants. If the list is of size one createBackgroundModel(trackList.get(0), sites,minSites) is called
     * @param sites - sites to set the probabilities for the background positions
     * @param minSites - minimum expected sites count
     * @return background model as sites object.
     * @throws CovariatesException - if there are too many covariants
     */
    public static Sites createBackgroundModel(List<Track> trackList, Sites sites, int minSites, double smooth) throws CovariatesException, TrackTypeNotAllowedExcpetion {


        if (trackList.isEmpty())
            return createBackgroundModel(sites.getAssembly(), sites.getPositionCount());

        else if (trackList.size() == 1)
            return createBackgroundModel(trackList.get(0), sites, minSites, smooth);

        else if (trackList.stream().allMatch(i -> i instanceof InOutTrack))
            if(trackList.size() < maxCovariantsInOutOnly) {
                return MultiTrackBackgroundModel.create(trackList, sites, minSites);
            } else
                throw new CovariatesException("Too many covariants: " + trackList.size() + ". Max " + maxCovariantsInOutOnly + " are allowed");

        else if (trackList.size() <= maxCovariants) {

            if (trackList.stream().allMatch(i -> i instanceof ScoredTrack)) {
                List<ScoredTrack> newList = trackList.stream().map(i -> (ScoredTrack) i).collect(Collectors.toList());

                return ScoreBackgroundModel.create(newList, sites, minSites, smooth);

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


                return ScoreBackgroundModel.create(scoredIntervals, sites, minSites, smooth);
            }

        } else {
            throw new CovariatesException("Too many covariants. " + trackList.size() + ". Only " + maxCovariantsInOutOnly + " are allowed");
        }
    }
}
