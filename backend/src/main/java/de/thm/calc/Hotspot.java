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
package de.thm.calc;

import de.thm.genomeData.tracks.ScoredTrack;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;

/**
 * Hotspot calculator across the genome. Returns a track with integration scores for sliding windows
 *
 * Created by menzel on 2/21/17.
 */
class Hotspot {

    /**
     * Find hotspots with sliding window. Returns a track with found integration scores as count for the window
     *
     * @param sites - sites to measure
     * @param windowSize - window size for sliding windows, should be large enough to reduce computation time
     *
     * @return track with integration count as scores
     */
    ScoredTrack findHotspots(Sites sites, final int windowSize){
        long genomeSize = ChromosomSizes.getInstance().getGenomeSize(sites.getAssembly());

        // if windows size is below the threshold the lists can't handle the amount of intervals
        // if(windowSize < 10 * genomeSize/Integer.MAX_VALUE) // which is about 14, so windowSize should be at leat 20 (20/10 =2)
        if(windowSize < 20)
            throw new IllegalArgumentException("Window size is too small for the hotspots list. List can't be biggger than Int.Max_Value");

        int size = (int) (genomeSize/(windowSize/10)); //init size for interval lists

        List<Long> starts = new ArrayList<>(size);
        List<Long> ends = new ArrayList<>(size);
        List<Double> score = new ArrayList<>(size);
        List<Long> positions = sites.getPositions();

        for (long i = 0; i < (genomeSize - windowSize) && i < (positions.get(positions.size() - 1) + windowSize); i += windowSize) {
            // find sites between i and i + windowSize

            long finalI = i;
            long count = positions.parallelStream()
                            .filter(p -> p >= finalI && p < (finalI + windowSize))
                            .count();

            starts.add(i);
            ends.add(i+windowSize);
            score.add((double) count);
        }

        return TrackFactory.getInstance().createScoredTrack(starts,ends,null,score, "CIS", "CIS");
    }

}
