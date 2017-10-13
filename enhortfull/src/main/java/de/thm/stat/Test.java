package de.thm.stat;

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.Track;

/**
 * Interface for statistical test evaluation
 *
 * Created by Michael Menzel on 1/3/16.
 */
public interface Test<T extends Track>{

    TestResult test(TestTrackResult testTrackResultA, TestTrackResult testTrackResultB, T track);
}