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

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.*;
import de.thm.positionData.Sites;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.thm.backgroundModel.ScoreBackgroundModel.generatePositionsByProbability;

class NamedBackgroundModel {

    static BackgroundModel create(Sites sites, int count, NamedTrack track) {

        Intersect calc = new Intersect();
        TestTrackResult result = calc.searchSingleInterval(track, sites);

        // create map of names to probability for each name from names from the track
        Map<String, Double> probabilities = result.getResultNames().keySet().stream()
                .collect(Collectors.toMap(key -> key, key -> result.getResultNames().get(key).doubleValue() / sites.getPositionCount(), (a, b) -> b));

        // create list of probabilities for the probability track from the probabilities, it is missing the probs for values outside of any named interval
        // outside positions are created separately

        // get overall lengths for each possible name
        List<String> names = Stream.of(track.getIntervalName()).distinct().collect(Collectors.toList());
        List<Long> lengths = new ArrayList<>(Collections.nCopies(names.size(), 0L));

        for (int i = 0; i < track.getStarts().length; i++) {
            int pos = names.indexOf(track.getIntervalName()[i]);
            lengths.set(pos, lengths.get(pos) + track.getEnds()[i] - track.getStarts()[i]);
        }

        // generate list of probabilities for each interval
        // the prob is calculated by the fraction of the length of the single interval over the length of all intervals with this name,
        // times the probability for this name
        List<Double> probs = new ArrayList<>();
        for (int i = 0; i < track.getStarts().length; i++) {
            double currLength = track.getEnds()[i] - track.getStarts()[i];
            probs.add(probabilities.getOrDefault(track.getIntervalName()[i], 0.0) * (currLength / lengths.get(names.indexOf(track.getIntervalName()[i]))));
        }

        ScoredTrack probTrack = TrackFactory.getInstance().createScoredTrack(
                track.getStarts(),
                track.getEnds(),
                track.getIntervalName(),
                probs.stream().mapToDouble(d -> d).toArray(),
                track.getName() + " probabilities for background model",
                track.getDescription(),
                sites.getAssembly());


        count = (sites.getPositionCount() > count) ? sites.getPositionCount() : count;
        count *= 1.15; // increase count to adjust for contigs filter

        double percentIn = ((double) result.getIn()) / sites.getPositionCount();
        double percentOut = ((double) result.getOut()) / sites.getPositionCount();

        Track contigs = TrackFactory.getInstance().getTrackByName("Contigs", sites.getAssembly());
        Collection<Long> pos = generatePositionsByProbability(probTrack, (int) (count * percentIn)); // creates inside positions

        pos.addAll(SingleTrackBackgroundModel.randPositions((int) (count * percentOut), Tracks.invert(track))); // creates outside positions

        // intersect with contigs, filter all positions outside of contigs
        ArrayList<Long> positions = new ArrayList<>(pos);
        Track filteredSites = Tracks.intersect(contigs, Tracks.getTrack(new BackgroundModel(positions, sites.getAssembly())));

        positions.clear();
        positions.addAll(Arrays.asList(ArrayUtils.toObject(filteredSites.getStarts())));

        return new BackgroundModel(positions, sites.getAssembly());

    }
}
