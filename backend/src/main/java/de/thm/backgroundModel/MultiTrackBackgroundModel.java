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

import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.Tracks;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a background model which can use many covariants.
 * <p>
 * Created by Michael Menzel on 13/1/16.
 */
class MultiTrackBackgroundModel {


    /**
     * Constructor. Creates a Bg Model with covariants according the given intervals and positions.
     *
     * @param tracks         - covariants
     * @param inputPositions - positions to match against
     */
    static BackgroundModel create(List<Track> tracks, Sites inputPositions, int minSites) {

        List<Long> positions = new ArrayList<>();

        AppearanceTable appearanceTable = new AppearanceTable(minSites);
        appearanceTable.fillTable(tracks, inputPositions);

        positions.addAll(randPositions(appearanceTable, tracks));

        return new BackgroundModel(positions, tracks.get(0).getAssembly());
    }

    /**
     * Generates random positions for a given appearance table and the intervals.
     * The appearance table has to be made of the given intervals.
     *
     * @param appearanceTable - table of appearance counts
     * @param tracks          - intervals to match against
     * @return list of positions which are spread by the same appearance values
     */
    static Collection<Long> randPositions(AppearanceTable appearanceTable, List<Track> tracks) {

        List<Long> sites = new ArrayList<>();
        Genome.Assembly assembly = tracks.get(0).getAssembly();

        Track contigs = TrackFactory.getInstance().getTrackByName("Contigs", assembly);

        // set the positions for each combination of tracks
        for (String app : appearanceTable.getKeySet()) {

            if (app.compareTo("[]") == 0) //skip outside positions
                continue;

            int count = appearanceTable.getAppearance(app);
            List<Track> currentTracks = appearanceTable.translate(app, tracks);
            List<Track> negativeTracks = appearanceTable.translateNegative(tracks, app);

            currentTracks.addAll(negativeTracks.stream().map(Tracks::invert).collect(Collectors.toList()));

            Track track = Tracks.intersect(currentTracks);

            //TODO check if sum of intervals is too small and add some pseudocount
            sites.addAll(SingleTrackBackgroundModel.randPositions(count, Tracks.intersect(track, contigs)));
        }

        // set outside positions
        int count = appearanceTable.getAppearance("[]");
        Track outs = Tracks.intersect(tracks.stream().map(Tracks::invert).collect(Collectors.toList()));
        sites.addAll(SingleTrackBackgroundModel.randPositions(count, Tracks.intersect(outs, contigs)));

        Collections.sort(sites);

        return sites;
    }
}
