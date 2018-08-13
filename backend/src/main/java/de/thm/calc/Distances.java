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

import de.thm.genomeData.tracks.DistanceTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculates a list of distances to intervals from positions
 *
 * Created by menzel on 9/7/16.
 */
public final class Distances implements TestTrack<DistanceTrack>{

    private static final int distanceRadius = 5000; //radius to be observed around a position


    /**
     * Calculates the intersect between an interval and some points. Handles in/out count, names and scores.
     *
     * @param track - interval to find positions
     * @param sites - positions to find
     * @return Result which contains the in/out count, names or scores
     */
    @Override
    public TestTrackResult searchTrack(DistanceTrack track, Sites sites) {
        List<Double> distances = distancesToNext(track, sites).stream()
                                                              .map(Long::doubleValue)
                                                              .collect(Collectors.toList());

        return new TestTrackResult(track, sites.getPositionCount(), 0, distances);
    }



    /**
     * Computes a map of distances for a set of positions from a track of interval
     *
     * @param track - track start sites
     * @param sites - sites to measure
     *
     * @return map of distances observed
     */
    public List<Long> distancesToNext(Track track, Sites sites){

        //DistanceCounter distances = new DistanceCounter();
        List<Long> distances = new ArrayList<>();

        long[] intervalStart = track.getStarts();

        int i = 0;
        int intervalCount = intervalStart.length - 1;

        for (long p : sites.getPositions()) {

            while (i < intervalCount && intervalStart[i] < p)
                i++;

            if(i == 0) { // if the position is before than the first start
                distances.add(intervalStart[0] - p);
                continue;
            }

            if (i == intervalCount && intervalStart[i] < p) {
                distances.add(p - intervalStart[intervalCount]);
                continue;
            }

            // calc distance to last and next site from position

            long upstream = p - intervalStart[i - 1];
            long downstream = p - intervalStart[i];
            //TODO use min or absolute min?

            // add smaller distance to map

             distances.add(min(upstream,downstream));
        }

        return distances.stream().filter(l -> l < distanceRadius && l > -distanceRadius).collect(Collectors.toList());
    }

    /**
     * Returns the absolute minimum of two numbers. However, the sign is kept.
     * @param a - first number
     * @param b - second number
     *
     * @return a if absolute(a) < absolute(b). else: b
     */
    private Long min(long a, long b) {
        if(Math.abs(a) < Math.abs(b))
            return a;
        else
            return b;
    }

}
