package de.thm.stat;

import de.thm.calc.IntersectResult;

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

}
