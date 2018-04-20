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
