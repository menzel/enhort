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

    public double test(Result resultA, Result resultB) {


        switch (resultA.getType()){
            case inout:
                double[] m = {resultA.getIn(), resultB.getOut()};
                long[] e ={resultA.getIn(), resultB.getOut()};

                return tester.chiSquareTest(m, e);

            case named:

                Map<String, Integer> measured = resultA.getResultNames();
                Map<String, Integer> exspected = resultB.getResultNames();

                assimilateLists(measured,exspected);

                return tester.chiSquareTest(sortAndFlatDouble(measured),sortAndFlatLong(exspected));

            case score:
                return 0;

            default:
                return 0;
        }
    }

    private double[] sortAndFlatDouble(Map<String, Integer> values) {
        double[] val = new double[values.keySet().size()];

        List<String> names = new ArrayList<>(values.keySet());
        Collections.sort(names);

        int i = 0;
        for(String name: names){
            val[i++] = values.get(name);
        }

        return val;
    }

   private long[] sortAndFlatLong(Map<String, Integer> values) {
       return toLongArray(sortAndFlatDouble(values));
    }


    private void assimilateLists(Map<String, Integer> measured, Map<String, Integer> exspected) {

        for(String name: measured.keySet()){
            if(!exspected.containsKey(name)){
                exspected.put(name, 1);
            }
        }

        for(String name: exspected.keySet()){
            if(!measured.containsKey(name)){
                measured.put(name, 1);
            }
        }
    }

    private long[] toLongArray(double[] values) {

        long[] longValues = new long[values.length];

        for(int i = 0 ; i < values.length; i++)
            longValues[i] = (long) values[i];

        return longValues;
    }
}



