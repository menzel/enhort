package de.thm.stat;

import de.thm.calc.Result;
import org.apache.commons.math3.stat.inference.ChiSquareTest;


/**
 * Created by Michael Menzel on 10/12/15.
 */
public class QuiSquareTest {


    public static double chiSquareTest(Result resultA, Result resultB) {

        return test(resultA.getA(), resultA.getB(), resultB.getA(), resultB.getB());
    }

    public static double test(int a, int b, int c, int d) {
        ChiSquareTest tester = new ChiSquareTest();

        double[] observed = {a,b};
        long[] expected = {c,d};

        return tester.chiSquareTest(observed,expected);

    }

}



