package de.thm.genomeData;

import de.thm.positionData.Sites;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public interface Interval {

    boolean isIn(Sites pos);
}
