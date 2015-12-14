package de.thm.stat;

import de.thm.calc.Result;
import org.apache.commons.math3.stat.inference.ChiSquareTest;


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
            case named:
                System.out.println(resultA.getInOut().toString() + "\n" + resultB.getInOut().toString());
                return tester.chiSquareTest(resultA.getInOut(), toLongArray(resultB.getInOut()));

            case score:
                return 1; //TODO use scores for independence test

            default:
                return 1;
        }
    }

    private long[] toLongArray(double[] values) {

        long[] longValues = new long[values.length];

        for(int i = 0 ; i < values.length; i++)
            longValues[i] = (long) values[i];

        return longValues;
    }
}



