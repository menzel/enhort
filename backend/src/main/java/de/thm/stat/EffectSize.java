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
     * @param a_in - count inside of first set
     * @param a_out -  count outside of first set
     * @param b_in - count insideof second set
     * @param b_out - count outside of second set
     *
     * Params should be whole numbers
     * They are converted implicitly to ensure double precision for the return value
     *
     * @return fold change
     */
    double foldChange(int a_in, int a_out, int b_in, int b_out) {

        if (a_in == 0 && b_in == 0 || a_out == 0 && b_out == 0)
            return 0.0;

        // first add relative pseudocounts
        double s0 = a_in + a_out;
        double s1 = b_in + b_out;

        int delta = (int) Math.round((max(s0, s1) / min(s0, s1)));

        int pc_a = (s0 > s1) ? delta : 1;
        int pc_b = (s0 < s1) ? delta : 1;

        a_in += pc_a;
        a_out += pc_a;
        b_in += pc_b;
        b_out += pc_b;

        // recalc sums because the counts changed
        s0 = a_in + a_out;
        s1 = b_in + b_out;

        double in_a = a_in / s0;
        double out_a = a_out / s0;

        double in_b = b_in / s1;
        double out_b = b_out / s1;

        /*
        //pseudocount
        in_a = Double.isNaN(in_a) || in_a == 0? Double.MIN_VALUE: in_a;
        in_b = Double.isNaN(in_b) || in_b == 0? Double.MIN_VALUE: in_b;

        out_a = Double.isNaN(out_a) || out_a == 0? Double.MIN_VALUE: out_a;
        out_b = Double.isNaN(out_b) || out_b == 0? Double.MIN_VALUE: out_b;
        */


        // the maximum of ( maximum inside / minimum inside and maximum outside / minimum outside) -- inverts values smaller than 1
        return FastMath.log(2, max(max(in_a, in_b) / min(in_a, in_b), max(out_a, out_b) / min(out_a, out_b)));
    }
}
