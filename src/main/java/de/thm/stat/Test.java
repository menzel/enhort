package de.thm.stat;

import de.thm.calc.IntersectResult;
import de.thm.genomeData.Track;

/**
 * Created by Michael Menzel on 1/3/16.
 */
public interface Test<T extends Track>{

    TestResult test(IntersectResult intersectResultA, IntersectResult intersectResultB, Track track);
}
