// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.stat;

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.ScoredTrack;
import de.thm.genomeData.tracks.TrackFactory;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the indepence test class
 *
 * Created by menzel on 9/26/16.
 */
public class IndependenceTestTest {

    @org.junit.Test
    public void testScoredTrack() throws Exception {

        //create base values
        Double[] val1 = {1.,55.,8.,23.,27.,1.,9.};
        Double[] val2 = {1.5,5.,1.099,2.1,2.,1.15,9.};

        List<Double> scores_a = new ArrayList<>(Arrays.asList(val1));
        List<Double> scores_b = new ArrayList<>(Arrays.asList(val2));

        TestTrackResult a = new TestTrackResult(null, 3000, 1200, scores_a);
        TestTrackResult b = new TestTrackResult(null, 5000, 200, scores_b);

        IndependenceTest tester = new IndependenceTest();

        ScoredTrack track = mockTrack(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        // get expected value
        KolmogorovSmirnovTest kolmo = new KolmogorovSmirnovTest();

        double[] sc1 = {1.,55.,8.,23.,27.,1.,9.};
        double[] sc2 = {1.5,5.,1.099,2.1,2.,1.15,9.};

        double expected = kolmo.kolmogorovSmirnovTest(sc1,sc2);

        //test the specific method for scored tracks
        TestResult result = tester.testScoredTrack(a,b,track);
        assertEquals(expected,result.getpValue(), 0.001);
        assertEquals(7.4, result.getEffectSize(), 0.1);

        //test the combined method which decied upon the track which method to call
        TestResult otherResult = tester.test(a,b,track);
        assertEquals(expected,otherResult.getpValue(), 0.001);
    }

    @Test
    public void testNamedTrack() throws Exception {
        //TODO

    }

    @Test
    public void sortAndFlat() throws Exception {
        //TODO
    }

    @Test
    public void prepareLists() throws Exception {
        //TODO
    }



    private ScoredTrack mockTrack(List<Long> start, List<Long> end, List<String> names, List<Double> scores) {

        return  TrackFactory.getInstance().createScoredTrack(start, end, names, scores,"name", "desc");
    }

}