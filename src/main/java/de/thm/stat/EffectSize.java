package de.thm.stat;

import de.thm.calc.IntersectResult;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 * Created by Michael Menzel on 29/1/16.
 */
public class EffectSize {

    private PearsonsCorrelation correlation = new PearsonsCorrelation();

    public double test(IntersectResult intersectResultA, IntersectResult intersectResultB, String trackName) {

        double[][] values= new double[2][2];
        values[0] = new double[] {intersectResultA.getIn(), intersectResultA.getOut()};
        values[1] = new double[] {intersectResultB.getIn(), intersectResultB.getOut()};

        return correlation.correlation(values[0], values[1]);

    }
}
