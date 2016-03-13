package de.thm.stat;

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.Track;

/**
 * Created by Michael Menzel on 1/3/16.
 */
public interface Test<T extends Track>{

    TestResult test(TestTrackResult testTrackResultA, TestTrackResult testTrackResultB, Track track);
}
