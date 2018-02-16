package de.thm.stat;

import de.thm.calc.TestTrackResult;
import org.apache.commons.math3.util.FastMath;

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

        if (in == 0 && in1 == 0 || out == 0 && out1 == 0)
            return 0;

        //pseudocount
        in += 1;
        out += 1;
        in1 += 1;
        out1 += 1;

        double sum = in+out;
        in = in/sum;
        out = out/sum;

        sum = in1+out1;
        in1 = in1/sum;
        out1 = out1/sum;

        // the maximum of ( maximum inside / minimum inside and maximum outside / maximum outside) -- inverts values smaller than 1
        return FastMath.log10(max(max(in, in1) / min(in, in1), max(out, out1) / min(out, out1)));
    }
}
