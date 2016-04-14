package de.thm.stat;

import de.thm.calc.TestTrackResult;

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

    double foldChange(int in, int out, int in1, int out1) {

        double fc1 = in / (double) out;
        double fc2 = in1 / (double) out1;

        if(Double.isNaN(fc1) || Double.isNaN(fc2) || (in < ((in+out)/200) && in1 < ((in1+out1)/200)))
            return 0.0;

        if (in == 0 || in1 == 0)
            return Double.POSITIVE_INFINITY;


        return Math.abs(Math.log(fc1 / fc2));
    }
}
