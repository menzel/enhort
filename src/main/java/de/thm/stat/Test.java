package de.thm.stat;

import de.thm.calc.TestResult;
import de.thm.genomeData.Track;

/**
 * Created by Michael Menzel on 1/3/16.
 */
public interface Test<T extends Track>{

    de.thm.stat.TestResult test(TestResult testResultA, TestResult testResultB, Track track);
}
