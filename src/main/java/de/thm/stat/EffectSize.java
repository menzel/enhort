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
     *
     * @return fold change of both result
     */
    public double test(IntersectResult intersectResultA, IntersectResult intersectResultB) {

        double fc1 = intersectResultA.getIn()/ (double) intersectResultA.getOut();
        double fc2 = intersectResultB.getIn()/ (double) intersectResultB.getOut();

        if(fc1 == 0 || fc2 == 0)
            return 0;

        return Math.abs(Math.log(fc1/fc2));

    }
}
