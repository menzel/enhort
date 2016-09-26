package de.thm.stat;

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.TrackFactory;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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

        ScoredTrack track = mockTrack(null,null, null,null);

        // get expected value
        KolmogorovSmirnovTest kolmo = new KolmogorovSmirnovTest();

        double[] sc1 = {1.,55.,8.,23.,27.,1.,9.};
        double[] sc2 = {1.5,5.,1.099,2.1,2.,1.15,9.};

        double expected = kolmo.kolmogorovSmirnovTest(sc1,sc2);

        //test the specific method for scored tracks
        TestResult result = tester.testScoredTrack(a,b,track);
        assertEquals(expected,result.getpValue(), 0.001);
        assertEquals(2.3,result.getEffectSize(), 0.001);

        //test the combined method which decied upon the track which method to call
        TestResult otherResult = tester.test(a,b,track);
        assertEquals(expected,otherResult.getpValue(), 0.001);
    }

    @Test
    public void testNamedTrack() throws Exception {
        //TODO

    }



    private ScoredTrack mockTrack(List<Long> start, List<Long> end, List<String> names, List<Double> scores) {

        return  TrackFactory.getInstance().createScoredTrack(start, end, names, scores,"name", "desc");
    }

}