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
    public final String name;
    public final String filename;
    public final String description;
    public final int measuredIn;
    public final int measuredOut;
    public final int expectedIn;
    public final int expectedOut;
    public final IntersectResult resultMeasured;
    public final IntersectResult resultExpected;

    public TestResult(double pValue, IntersectResult measured, IntersectResult expected, double effectSize, Interval usedInterval) {

        if(pValue == Double.NaN) {
            DecimalFormat format = new DecimalFormat("0.00E00");
            this.pValue = Double.parseDouble(format.format(pValue));
        } else this.pValue = 1;

        this.effectSize = Precision.round(effectSize,2);

        resultExpected = expected;
        resultMeasured = measured;

        this.measuredIn = measured.getIn();
        this.measuredOut = measured.getOut();

        this.expectedIn = expected.getIn();
        this.expectedOut = expected.getOut();

        this.name = usedInterval.getName();
        this.filename = usedInterval.getFilename();
        this.description = usedInterval.getDescription();
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

    public double getPercentInM(){
        return Precision.round(new Double(measuredIn) / (new Double(measuredOut + measuredIn)) * 100,2);
    }

    public double getPercentOutM(){
        return Precision.round(new Double(measuredOut) / (new Double(measuredOut + measuredIn)) * 100,2);
    }

    public double getPercentInE(){
        return Precision.round(new Double(expectedIn) / (new Double(expectedOut+ expectedIn)) * 100,2);
    }

    public double getPercentOutE(){
        return Precision.round(new Double(expectedOut) / (new Double(expectedOut+ expectedIn)) * 100,2);
    }

    public String toString(){
        return "measured "  + resultMeasured.toString() +
                "expected " +resultExpected.toString() +
                "Fold change Ratio: " + effectSize + "\n" +
                name + " p-value: " + pValue +
                "\n=====\n";
    }

    public String getFilename(){
        return this.filename;
    }

    public String getName() {
        if(name == null || name.equals("")){
            return filename;
        }
        return name;
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
