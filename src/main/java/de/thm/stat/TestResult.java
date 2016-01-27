package de.thm.stat;

import de.thm.calc.IntersectResult;
import org.json.JSONObject;

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

    public String toJson(){

        JSONObject obj = new JSONObject();

        obj.put("meIn", this.measuredIn);
        obj.put("meOut", this.measuredOut);
        obj.put("exIn", this.expectedIn);
        obj.put("exOut", this.expectedOut);
        obj.put("pValue", this.pValue);

        return  obj.toString();
    }

    public String getTrackname() {
        return trackName;
    }
}
