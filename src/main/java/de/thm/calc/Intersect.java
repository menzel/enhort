package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public interface Intersect {

    Result searchSingleInterval(Interval intv, Sites pos);

}
