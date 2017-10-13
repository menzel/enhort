package de.thm.calc;

import de.thm.genomeData.tracks.Track;
import de.thm.positionData.Sites;

/**
 * Calculates the intersect between an interval and some points. Handles in/out count, names and scores
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
public interface TestTrack<T extends Track> {

    /**
     * Calculates the intersect between an interval and some points. Handles in/out count, names and scores.
     *
     * @param track - interval to find positions
     * @param sites - positions to find
     *
     * @return Result which contains the in/out count, names or scores
     */
    TestTrackResult searchTrack(T track, Sites sites);

}
