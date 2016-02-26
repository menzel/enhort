package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

/**
 * Calculates the intersect between an interval and some points. Handles in/out count, names and scores
 *
 * Created by Michael Menzel on 8/12/15.
 */
public interface Intersect<T extends Interval>{

    /**
     * Calculates the intersect between an interval and some points. Handles in/out count, names and scores.
     *
     * @param intv - interval to find positions
     * @param pos - positions to find
     *
     * @return Result which contains the in/out count, names or scores
     */
    IntersectResult<T> searchSingleInterval(T intv, Sites pos);

}
