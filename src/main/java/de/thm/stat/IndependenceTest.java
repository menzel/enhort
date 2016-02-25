package de.thm.stat;

import de.thm.calc.IntersectResult;
import de.thm.genomeData.Interval;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import java.util.*;


/**
 * Tests the indepence of two intersect results and a given interval with either ChiSquare of KolmogorovSmirnov test.
 *
 * Created by Michael Menzel on 10/12/15.
 */
public final class IndependenceTest {

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
     * @param intersectResultA  measured results
     * @param intersectResultB expected (random) results
     * @param interval - used interval for reference
     *
     * @return p value of independence test
     */
    public TestResult test(IntersectResult intersectResultA, IntersectResult intersectResultB, Interval interval){

        long[][] counts = new long[2][2];
        counts[0] = new long[] {intersectResultA.getIn(), intersectResultA.getOut()};
        counts[1] = new long[] {intersectResultB.getIn(), intersectResultB.getOut()};

        double effectSize = effectSizeTester.test(intersectResultA,intersectResultB);


        switch (intersectResultA.getType()){

            case score:

                double[] measuredScore = intersectResultA.getResultScores().stream().mapToDouble(i ->i).toArray();
                double[] expectedScore = intersectResultB.getResultScores().stream().mapToDouble(i ->i).toArray();

                intersectResultA.add("in", measuredScore.length);
                intersectResultB.add("in", expectedScore.length);

                if(measuredScore.length < 2 || expectedScore.length < 2){
                    System.err.println(interval.getFilename() + " Failed");
                    System.err.println(Arrays.toString(measuredScore));
                    System.err.println(Arrays.toString(expectedScore));

                } else {
                    return new TestResult(kolmoTester.kolmogorovSmirnovTest(measuredScore, expectedScore), intersectResultA, intersectResultB, effectSize, interval);
                }

            case named:

                Map<String, Integer> measured = intersectResultA.getResultNames();
                Map<String, Integer> expected = intersectResultB.getResultNames();

                return new TestResult(tester.chiSquareTest(prepareLists(measured,expected)), intersectResultA, intersectResultB, effectSize, interval);

            case inout:

                return new TestResult(tester.chiSquareTest(counts), intersectResultA, intersectResultB, effectSize, interval);

            default:
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
        for(String name: names){
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
        for(String name: measured.keySet()){
            if(!expected.containsKey(name)){
                expected.put(name, 1);
            }
        }

        for(String name: expected.keySet()){
            if(!measured.containsKey(name)){
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



