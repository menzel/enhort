package de.thm.stat;

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.*;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Tests the indepence of two intersect results and a given interval with either ChiSquare of KolmogorovSmirnov test.
 * <p>
 * Created by Michael Menzel on 10/12/15.
 */
public final class IndependenceTest implements Test{

    private final ChiSquareTest tester;
    private final KolmogorovSmirnovTest kolmoTester;
    private final EffectSize effectSizeTester;

    public IndependenceTest() {

        tester = new ChiSquareTest();
        kolmoTester = new KolmogorovSmirnovTest();
        effectSizeTester = new EffectSize();
    }


    /**
     *
     * Compares two tes results for a single track
     *
     * @param testTrackResultA - test result for the first dataset
     * @param testTrackResultB - test result for the second dataset
     * @param track - tested track
     *
     * @return combined result with effect size and p value
     */
    public TestResult testScoredTrack(TestTrackResult testTrackResultA, TestTrackResult testTrackResultB, Track track){

        double[] measuredScore = testTrackResultA.getResultScores().stream().mapToDouble(i -> i).toArray();
        double[] expectedScore = testTrackResultB.getResultScores().stream().mapToDouble(i -> i).toArray();

        if (measuredScore.length < 2 || expectedScore.length < 2) {
            System.err.println("Not enough data to compute independence test");
            double effectSize = effectSizeTester.test(testTrackResultA, testTrackResultB);
            return new TestResult(Double.NaN, testTrackResultA, testTrackResultB, effectSize, track, TestResult.Type.score);
        }

        double effectSize = effectSizeTester.test(testTrackResultA, testTrackResultB);

        return new TestResult(kolmoTester.kolmogorovSmirnovTest(measuredScore, expectedScore), testTrackResultA, testTrackResultB, effectSize, track, TestResult.Type.score);
    }


    /**
     *
     * Compares two tes results for a single track
     *
     * @param testTrackResultA - test result for the first dataset
     * @param testTrackResultB - test result for the second dataset
     * @param track - tested track
     *
     * @return combined result with effect size and p value
     */
    public TestResult testNamedTrack(TestTrackResult testTrackResultA, TestTrackResult testTrackResultB, Track track) {

        Map<String, Integer> measured = testTrackResultA.getResultNames();
        Map<String, Integer> expected = testTrackResultB.getResultNames();

        double effectSize = effectSizeTester.test(testTrackResultA, testTrackResultB);

        return new TestResult(tester.chiSquareTest(prepareLists(measured, expected)), testTrackResultA, testTrackResultB, effectSize, track, TestResult.Type.name);
    }

    /**
     * Tests two Result objects upon independence
     *
     * @param testTrackResultA measured results
     * @param testTrackResultB expected (random) results
     * @param track            - used interval for reference
     * @return p value of independence test with effect size as TestResult object
     */
    public TestResult test(TestTrackResult testTrackResultA, TestTrackResult testTrackResultB, Track track) {

        long[][] counts = new long[2][2];
        counts[0] = new long[]{testTrackResultA.getIn(), testTrackResultA.getOut()};
        counts[1] = new long[]{testTrackResultB.getIn(), testTrackResultB.getOut()};


        if (track instanceof ScoredTrack)
            return testScoredTrack(testTrackResultA,testTrackResultB, track);

        else if (track instanceof NamedTrack)
            return testNamedTrack(testTrackResultA, testTrackResultB, track);

        else if (track instanceof InOutTrack || track instanceof StrandTrack){

            double effectSize = effectSizeTester.test(testTrackResultA, testTrackResultB);
            return new TestResult(tester.chiSquareTest(counts), testTrackResultA, testTrackResultB, effectSize, track, TestResult.Type.inout);

        } else {
            System.err.println("Unknown track type for test in IndependenceTest");
            return null;
        }
    }

    /**
     * Sorts the values by name and writes them to a long[]. The lists must be sorted before. After this method the name information is gone
     *
     * @param values - Map of names to values
     * @return Integer values from values as long[]
     */
    long[] sortAndFlat(Map<String, Integer> values) {
        long[] val = new long[values.keySet().size()];

        List<String> names = new ArrayList<>(values.keySet());
        Collections.sort(names);

        int i = 0;
        for (String name : names) {
            val[i++] = values.get(name);
        }

        return val;
    }


    /**
     * Ensures that the lists are the same and calls sortAndFlat for each.
     *
     * @param measured - measured results
     * @param expected - expected results
     * @return rectangular long[][] for chi square test
     */
    long[][] prepareLists(Map<String, Integer> measured, Map<String, Integer> expected) {

        //make sure the keys in both lists are the same:
        for (String name : measured.keySet()) {
            if (!expected.containsKey(name)) {
                expected.put(name, 1);
            }
        }

        for (String name : expected.keySet()) {
            if (!measured.containsKey(name)) {
                measured.put(name, 1);
            }
        }

        // put both lists in a 2dim long array
        long[][] r = new long[2][measured.keySet().size()];

        r[0] = sortAndFlat(measured);
        r[1] = sortAndFlat(expected);

        return r;
    }
}



