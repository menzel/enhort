package de.thm.stat;

import de.thm.calc.TestResult;

/**
 * Can compute the the fold change
 * Created by Michael Menzel on 29/1/16.
 */
public final class EffectSize {


    /**
     * Computes the fold change of two intersect results.
     *
     * @param testResultA - first result
     * @param testResultB - second result
     * @return fold change of both result
     */
    public double test(de.thm.calc.TestResult testResultA, TestResult testResultB) {


        return foldChange(testResultA.getIn(), testResultA.getOut(), testResultB.getIn(), testResultB.getOut());

    }

    double foldChange(int in, int out, int in1, int out1) {

        double fc1 = in / (double) out;
        double fc2 = in1 / (double) out1;

        if(Double.isNaN(fc1)
                || Double.isNaN(fc2)
                || (in < 3 && in1 < 3))
            return 0.0;

        if (in == 0 ^ in1 == 0)
            return Double.POSITIVE_INFINITY;


        return Math.abs(Math.log(fc1 / fc2));
    }
}
