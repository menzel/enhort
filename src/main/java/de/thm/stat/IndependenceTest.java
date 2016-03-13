package de.thm.stat;

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.NamedTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import java.util.*;


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
     * Tests two Result objects upon independence
     *
     * @param testTrackResultA measured results
     * @param testTrackResultB expected (random) results
     * @param track            - used interval for reference
     * @return p value of independence test
     */
    public TestResult test(TestTrackResult testTrackResultA, TestTrackResult testTrackResultB, Track track) {

        long[][] counts = new long[2][2];
        counts[0] = new long[]{testTrackResultA.getIn(), testTrackResultA.getOut()};
        counts[1] = new long[]{testTrackResultB.getIn(), testTrackResultB.getOut()};

        double effectSize = effectSizeTester.test(testTrackResultA, testTrackResultB);


        if (track instanceof ScoredTrack) {

            double[] measuredScore = testTrackResultA.getResultScores().stream().mapToDouble(i -> i).toArray();
            double[] expectedScore = testTrackResultB.getResultScores().stream().mapToDouble(i -> i).toArray();

            if (measuredScore.length < 2 || expectedScore.length < 2) {
                System.err.println("In Independence test");
                System.err.println(Arrays.toString(measuredScore));
                System.err.println(Arrays.toString(expectedScore));
                return null;

            } else {
                return new TestResult(kolmoTester.kolmogorovSmirnovTest(measuredScore, expectedScore), testTrackResultA, testTrackResultB, effectSize, track, TestResult.Type.score);
            }

        } else if (track instanceof NamedTrack) {


            Map<String, Integer> measured = testTrackResultA.getResultNames();
            Map<String, Integer> expected = testTrackResultB.getResultNames();

            return new TestResult(tester.chiSquareTest(prepareLists(measured, expected)), testTrackResultA, testTrackResultB, effectSize, track, TestResult.Type.name);


        } else if (track instanceof InOutTrack) {

            return new TestResult(tester.chiSquareTest(counts), testTrackResultA, testTrackResultB, effectSize, track, TestResult.Type.inout);

        } else {
            return null;
        }
    }

    /**
     * Sorts the values by name and writes them to a long[]. The lists must be sorted before. After this method the name information is gone
     *
     * @param values - Map of names to values
     * @return Integer values from values as long[]
     */
    private long[] sortAndFlat(Map<String, Integer> values) {
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
    private long[][] prepareLists(Map<String, Integer> measured, Map<String, Integer> expected) {

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



