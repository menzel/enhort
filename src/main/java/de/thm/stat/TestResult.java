package de.thm.stat;

import de.thm.calc.IntersectResult;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.NamedTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.util.Precision;

import java.text.DecimalFormat;

/**
 * The Results of one Test together with the input params (intersect results, measured in/ out counts, names files, pvalue, fold change )
 *
 * Created by Michael Menzel on 12/1/16.
 */
public final class TestResult{

    private final double pValue;
    private final double effectSize;
    private final String name;
    private final String description;
    private final int measuredIn;
    private final int measuredOut;
    private final int expectedIn;
    private final int expectedOut;
    private final IntersectResult resultMeasured;
    private final IntersectResult resultExpected;
    private final int id;
    private final Track usedInterval;

    public TestResult(double pValue, IntersectResult measured, IntersectResult expected, double effectSize, Track usedInterval) {
        this.usedInterval = usedInterval;

        DecimalFormat format = new DecimalFormat("0.00E00");
        String v = format.format(pValue);

        if(pValue != Double.NaN && NumberUtils.isNumber(v)) {
            this.pValue = Double.parseDouble(format.format(pValue));
        } else{
            System.err.println(measured);
            System.err.println(expected);
            this.pValue = 1; //TODO check
        }

        this.effectSize = Precision.round(effectSize,2);

        resultExpected = expected;
        resultMeasured = measured;

        this.measuredIn = measured.getIn();
        this.measuredOut = measured.getOut();

        this.expectedIn = expected.getIn();
        this.expectedOut = expected.getOut();

        this.name = usedInterval.getName();
        this.description = usedInterval.getDescription();

        this.id = usedInterval.getUid();
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
        String name = (this.name != null)? this.name: Integer.toString(this.id);
        return "measured "  + resultMeasured.toString() +
                "expected " +resultExpected.toString() +
                "Fold change Ratio: " + effectSize + "\n" +
                name + " p-value: " + pValue +
                "\n=====\n";
    }

    public String getName() {
        if(name == null || name.equals("")){
            return "track_" + id;
        }
        return name;
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

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Class getType() {
        if(this.usedInterval instanceof InOutTrack)
            return InOutTrack.class;
        if(this.usedInterval instanceof ScoredTrack)
            return ScoredTrack.class;
        if(this.usedInterval instanceof NamedTrack)
            return NamedTrack.class;
        else
            return Interval.class;
    }
}
