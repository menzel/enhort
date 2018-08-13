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

import de.thm.genomeData.tracks.InOutTrack;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionalIntersect implements TestTrack<InOutTrack> {

    /**
     * Calculates the intersect between an interval and some points. Handles in/out count, names and scores.
     *
     * @param track - interval to find positions
     * @param sites - positions to find
     * @return Result which contains the in/out count, names or scores
     */
    @Override
    public TestTrackResult searchTrack(InOutTrack track, Sites sites) {
        return positionalIntersect(track, sites, 200);
    }

    private TestTrackResult positionalIntersect(InOutTrack track, Sites sites, int windowcount) {
        int i = 0;
        long gs = ChromosomSizes.getInstance().getGenomeSize(sites.getAssembly());
        long windowsize = gs / windowcount;

        List<Double> density = new ArrayList<>(Collections.nCopies(windowcount, .0));

        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();

        int intervalCount = intervalStart.length - 1;


        for (long p : sites.getPositions()) {

            while (i < intervalCount && intervalEnd[i] <= p)
                i++;

            if (i == intervalCount && p >= intervalEnd[i]) { //not inside last interval
                break; //and end the loop
            }

            if (p >= intervalStart[i]) {
                int pos = (int) Math.floor(p / windowsize);
                density.set(pos, density.get(pos) + 1);
            }

            if (Thread.currentThread().isInterrupted()) return null;
        }


        return new TestTrackResult(track, 0, 0, density);
    }
}
