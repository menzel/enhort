package de.thm.calc;

import de.thm.genomeData.DistanceTrack;
import de.thm.genomeData.Track;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculates a list of distances to intervals from positions
 *
 * Created by menzel on 9/7/16.
 */
public class Distances implements TestTrack<DistanceTrack>{


    /**
     * Calculates the intersect between an interval and some points. Handles in/out count, names and scores.
     *
     * @param track - interval to find positions
     * @param sites - positions to find
     * @return Result which contains the in/out count, names or scores
     */
    @Override
    public TestTrackResult searchTrack(DistanceTrack track, Sites sites) {
        List<Double> distances = distancesToNext(track, sites).stream().map(Integer::doubleValue).collect(Collectors.toList());

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
    private List<Integer> distancesToNext(Track track, Sites sites){

        //DistanceCounter distances = new DistanceCounter();
        List<Integer> distances = new ArrayList<>();

        List<Long> intervalStart = track.getIntervalsStart();

        int i = 0;
        int intervalCount = intervalStart.size() - 1;


        for (Long p : sites.getPositions()) {

            while (i < intervalCount && intervalStart.get(i) < p)
                i++;

            if(i == 0) { // if the position is before than the first start
                distances.add((int) (intervalStart.get(0) - p));
                continue;
            }

            // calc distance to last and next site from position

            int upstream = (int) (p - intervalStart.get(i-1));
            int downstream = (int) (p - intervalStart.get(i));

            // add smaller distance to map
            distances.add(Math.min(Math.abs(upstream),Math.abs(downstream)));
        }

        return distances;
    }

}
