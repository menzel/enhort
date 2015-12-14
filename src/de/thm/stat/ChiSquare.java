package de.thm.stat;

import de.thm.calc.Result;


/**
 * Created by Michael Menzel on 10/12/15.
 */
public class ChiSquare {


    public static double chiSquareTest(Result resultA, Result resultB) {

        return test(resultA.getA(), resultA.getB(), resultB.getA(), resultB.getB());
    }

    public static double test(int a, int b, int c, int d) {
        org.apache.commons.math3.stat.inference.ChiSquareTest tester = new org.apache.commons.math3.stat.inference.ChiSquareTest();

        double[] observed = {a,b};
        long[] expected = {c,d};

        return tester.chiSquareTest(observed,expected);

    }

}



