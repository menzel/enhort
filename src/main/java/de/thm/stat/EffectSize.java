package de.thm.stat;

import de.thm.calc.TestTrackResult;

import static java.lang.Math.max;
import static java.lang.Math.min;


/**
 * Can compute the the fold change
 * Created by Michael Menzel on 29/1/16.
 */
public final class EffectSize {


    /**
     * Computes the fold change of two intersect results.
     *
     * @param testTrackResultA - first result
     * @param testTrackResultB - second result
     * @return fold change of both result
     */
    public double test(TestTrackResult testTrackResultA, TestTrackResult testTrackResultB) {


        return foldChange(testTrackResultA.getIn(), testTrackResultA.getOut(), testTrackResultB.getIn(), testTrackResultB.getOut());

    }

    double foldChange(double in, double out, double in1, double out1) {

        if(in1 == 0 || out1 == 0 || out  == 0 || in == 0)
            return Double.POSITIVE_INFINITY;

        // the maximum of ( maximum inside / minimum inside and maximum outside / maximum outside) -- inverts
        return max(max(in,in1) / min(in,in1), max(out,out1) / min(out,out1));
    }
}
