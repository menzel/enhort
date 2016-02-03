package de.thm.stat;

import de.thm.calc.IntersectResult;
import de.thm.genomeData.Interval;
import org.apache.commons.math3.util.Precision;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by Michael Menzel on 12/1/16.
 */
public class TestResult implements Serializable{

    public final double pValue;
    public final double effectSize;
    public final String trackName;
    public final int measuredIn;
    public final int measuredOut;
    public final int expectedIn;
    public final int expectedOut;
    public final IntersectResult resultMeasured;
    public final IntersectResult resultExpected;

    public TestResult(double pValue, IntersectResult measured, IntersectResult expected, double effectSize, String trackName) {

        DecimalFormat format = new DecimalFormat("0.00E00");
        this.pValue = Double.parseDouble(format.format(pValue));
        this.effectSize = Precision.round(effectSize,2);

        resultExpected = expected;
        resultMeasured = measured;

        this.measuredIn = measured.getIn();
        this.measuredOut = measured.getOut();

        this.expectedIn = expected.getIn();
        this.expectedOut = expected.getOut();

        this.trackName = trackName;
    }

    public double getpValue() {
        return pValue;
    }

    public int getMeasuredIn() {
        return measuredIn;
    }

    public int getExpectedIn() {
        return expectedIn;
    }

    public int getMeasuredOut() {
        return measuredOut;
    }

    public int getExpectedOut() {
        return expectedOut;
    }

    public String toString(){
        return "measured "  + resultMeasured.toString() +
                "expected " +resultExpected.toString() +
                "Fold change Ratio: " + effectSize + "\n" +
                trackName + " p-value: " + pValue +
                "\n=====\n";
    }

    public String toCsv(){

        String line = trackName.substring(0,trackName.indexOf("."));
        //double all = (measuredIn+expectedIn+measuredOut+expectedOut);
        double all = (measuredIn+expectedIn);
        int max = 100;
        if(pValue > 0.05)
            line += "," + 0 + "," + (measuredIn/all)*max + ",0," + (expectedIn/all)*max +  "," + 0 + "," + max + "\n";
        else
            line += "," + (measuredIn/all)*max + ",0,0,0," + (expectedIn/all)*max +  "," + max + "\n";

        return  line;

    }

    public String getTrackname() {
        return trackName;
    }

    public Interval.Type getType(){
        return this.resultExpected.getType();
    }

    public IntersectResult getResultMeasured() {
        return resultMeasured;
    }

    public IntersectResult getResultExpected() {
        return resultExpected;
    }

    public double getEffectSize() {
        return effectSize;
    }
}
