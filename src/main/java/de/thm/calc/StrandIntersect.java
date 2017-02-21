package de.thm.calc;

import de.thm.genomeData.StrandTrack;
import de.thm.positionData.Sites;
import de.thm.positionData.StrandSites;

import java.util.List;

/**
 * Created by menzel on 2/21/17.
 */
public class StrandIntersect implements TestTrack<StrandTrack> {

    /**
     * Calculates the intersect between an interval and some points. Handles in/out count, names and scores.
     *
     * @param track - interval to find positions
     * @param sites_t - positions to find should be instanceof StrandSites
     * @return Result which contains the in/out count, names or scores
     */
    @Override
    public TestTrackResult searchTrack(StrandTrack track, Sites sites_t) {

        int out = 0;
        int in = 0;
        int i = 0;

        StrandSites sites = (StrandSites) sites_t;

        List<Long> intervalStart = track.getStarts();
        List<Long> intervalEnd = track.getEnds();
        List<Character> strands = track.getStrands();

        int intervalCount = intervalStart.size() - 1;

        int p_counter = 0; //counter over positions from sites object
        for (long p : sites.getPositions()) {

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if(i == intervalCount && p >= intervalEnd.get(i)) { //not inside last interval
                out += sites.getPositions().size() - sites.getPositions().indexOf(p); //add remaining positions to out
                break; //and end the loop
            }

            if (p >= intervalStart.get(i) && sites.getStrand().get(p_counter) == strands.get(i)) in++;
            else out++;

            p_counter++;
        }


        return new TestTrackResult(track, in, out);
    }
}
