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


    /**
     * Calculates the fold change of given in out values for two given sets by the following formula:
     * The maximum of ( maximum inside / minimum inside and maximum outside / maximum outside)
     * This inverts values smaller than 1 and returns the max fold change for in or outside counts
     *
     * @param in - count inside of first set
     * @param out -  count outside of first set
     * @param in1 - count insideof second set
     * @param out1 - count outside of second set
     *
     * Params should be whole numbers
     * They are converted implicitly to ensure double precision for the return value
     *
     * @return fold change
     */
    double foldChange(double in, double out, double in1, double out1) {

        if(in1 == 0 || out1 == 0 || out  == 0 || in == 0)
            return Double.POSITIVE_INFINITY;

        // the maximum of ( maximum inside / minimum inside and maximum outside / maximum outside) -- inverts values smaller than 1
        return max(max(in,in1) / min(in,in1), max(out,out1) / min(out,out1));
    }
}
