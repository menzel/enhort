package de.thm.stat;

import de.thm.calc.Result;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by Michael Menzel on 10/12/15.
 */
public class IndependenceTest {

    private ChiSquareTest tester;

    public IndependenceTest() {

        tester = new ChiSquareTest();
    }

    /**
     * Tests two Result objects upon independence
     *
     * @param resultA  measured results
     * @param resultB expected (random) results
     * @param trackName - name of the track for output
     * @return p value of independence test
     */
    public TestResult test(Result resultA, Result resultB, String trackName) {


        switch (resultA.getType()){

            case score:
            case named:

                Map<String, Integer> measured = resultA.getResultNames();
                Map<String, Integer> expected = resultB.getResultNames();

                return new TestResult(tester.chiSquareTest(prepareLists(measured,expected)),resultA, resultB, trackName);

            case inout:

                long[][] counts = new long[2][2];
                counts[0] = new long[] {resultA.getIn(), resultA.getOut()};
                counts[1] = new long[] {resultB.getIn(), resultB.getOut()};

                return new TestResult(tester.chiSquareTest(counts),resultA, resultB, trackName);

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



