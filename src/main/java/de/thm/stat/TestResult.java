package de.thm.stat;

import de.thm.calc.IntersectResult;
import de.thm.genomeData.Interval;

/**
 * Created by Michael Menzel on 12/1/16.
 */
public class TestResult {

    private final double pValue;
    private final String trackName;
    private final int measuredIn;
    private final int measuredOut;
    private final int expectedIn;
    private final int expectedOut;
    private final IntersectResult resultMeasured;
    private final IntersectResult resultExpected;

    public TestResult(double pValue, IntersectResult measured, IntersectResult expected, String trackName) {
        this.pValue = pValue;

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
}
