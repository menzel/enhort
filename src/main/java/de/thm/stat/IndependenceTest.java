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
     * @param resultA first result to test
     * @param resultB second result to test
     * @return p value of independence test
     */
    public double test(Result resultA, Result resultB) {


        switch (resultA.getType()){

            case named:

                Map<String, Integer> measured = resultA.getResultNames();
                Map<String, Integer> expected = resultB.getResultNames();

                System.out.println("======");
                System.out.println("measured " + resultA.toString());
                System.out.println("expected " + resultB.toString());

                return tester.chiSquareTest(prepareLists(measured,expected));

            case inout:

                long[][] counts = new long[2][2];
                counts[0] = new long[] {resultA.getIn(), resultA.getOut()};
                counts[1] = new long[] {resultB.getIn(), resultB.getOut()};


                System.out.println("======");
                System.out.print("measured " + resultA.toString());
                System.out.print("expected " + resultB.toString());

                return tester.chiSquareTest(counts);

            case score:
                return -1;

            default:
                return -1;
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



