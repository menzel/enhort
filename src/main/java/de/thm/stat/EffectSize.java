package de.thm.stat;

import de.thm.calc.IntersectResult;

/**
 * Created by Michael Menzel on 29/1/16.
 */
public class EffectSize {


    public double test(IntersectResult intersectResultA, IntersectResult intersectResultB, String trackName) {

        double fc1 = intersectResultA.getIn()/new Double(intersectResultA.getOut());
        double fc2 = intersectResultB.getIn()/new Double(intersectResultB.getOut());

        return Math.abs(Math.log(fc1/fc2));

    }
}
