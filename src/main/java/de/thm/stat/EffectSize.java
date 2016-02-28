package de.thm.stat;

import de.thm.calc.IntersectResult;

/**
 * Can compute the the fold change
 * Created by Michael Menzel on 29/1/16.
 */
public final class EffectSize {


    /**
     * Computes the fold change of two intersect results.
     *
     * @param intersectResultA - first result
     * @param intersectResultB - second result
     * @return fold change of both result
     */
    public double test(IntersectResult intersectResultA, IntersectResult intersectResultB) {


        return foldChange(intersectResultA.getIn(), intersectResultA.getOut(), intersectResultB.getIn(), intersectResultB.getOut());

    }

    double foldChange(int in, int out, int in1, int out1) {

        double fc1 = in / (double) out;
        double fc2 = in1 / (double) out1;

        if(Double.isNaN(fc1) || Double.isNaN(fc2))
            return 0.0;

        if (fc1 == 0 || fc2 == 0)
            return Double.POSITIVE_INFINITY;

        return Math.abs(Math.log(fc1 / fc2));
    }
}
