package de.thm.stat;

import de.thm.genomeData.Track;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Precision;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * The Results of one Test together with the input params (intersect results, measured in/ out counts, names files, pvalue, fold change )
 * <p>
 * Created by Michael Menzel on 12/1/16.
 */
public final class TestResult implements Serializable{

    private final double pValue;
    private final double effectSize;
    private final String name;
    private final String description;
    private final int measuredIn;
    private final int measuredOut;
    private final int expectedIn;
    private final int expectedOut;
    private final List<Double> scoresMea;
    private final List<Double> scoresExp;
    private final Map<String, Integer> namesMea;
    private final Map<String, Integer> namesExp;
    private final int id;
    private final Type type;

    public TestResult(double pValue, de.thm.calc.TestResult measured, de.thm.calc.TestResult expected, double effectSize, Track usedInterval, Type type) {

        DecimalFormat format = new DecimalFormat("0.00E00");
        String v = format.format(pValue);

        if (pValue != Double.NaN && NumberUtils.isNumber(v)) {
            this.pValue = Double.parseDouble(format.format(pValue));

        } else {  // pValue is NaN because no point was inside an interval
            this.pValue = 1;
        }

        this.effectSize = Precision.round(effectSize, 2);

        this.scoresExp = expected.getResultScores();
        this.scoresMea = measured.getResultScores();
        //this.namesExp = expected.getResultNames().entrySet().stream().map(i -> i.getKey() + ":" + i.getValue()).collect(Collectors.toSet());
        //this.namesMea = measured.getResultNames().entrySet().stream().map(i -> i.getKey() + ":" + i.getValue()).collect(Collectors.toSet());


        //TODO divide names by count of points
        this.namesMea = measured.getResultNames();
        this.namesExp = expected.getResultNames();

        this.measuredIn = measured.getIn();
        this.measuredOut = measured.getOut();

        this.expectedIn = expected.getIn();
        this.expectedOut = expected.getOut();

        this.name = usedInterval.getName();
        this.description = usedInterval.getDescription();

        this.id = usedInterval.getUid();
        this.type = type;

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

    public double getPercentInM() {
        return Precision.round(new Double(measuredIn) / (new Double(measuredOut + measuredIn)) * 100, 2);
    }

    public double getPercentOutM() {
        return Precision.round(new Double(measuredOut) / (new Double(measuredOut + measuredIn)) * 100, 2);
    }

    public double getPercentInE() {
        return Precision.round(new Double(expectedIn) / (new Double(expectedOut + expectedIn)) * 100, 2);
    }

    public double getPercentOutE() {
        return Precision.round(new Double(expectedOut) / (new Double(expectedOut + expectedIn)) * 100, 2);
    }

    public String toString() {
        String name = (this.name != null) ? this.name : Integer.toString(this.id);
        return "Fold change Ratio: " + effectSize + "\n" +
                "mea in " + measuredIn + " out " + measuredOut + "\n" +
                "exp in " + expectedIn + " out " + expectedOut + "\n" +
                name + " p-value: " + pValue +
                "\n=====\n";
    }

    public String getName() {
        if (name == null || name.equals("")) {
            return "track_" + id;
        }
        return name;
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

    public Type getType() {
        return this.type;
    }

    public List<Double> getScoresMea() {
        return scoresMea;
    }

    public List<Double> getScoresExp() {
        return scoresExp;
    }

    public Map<String, Integer> getNamesMea() {
        return namesMea;
    }

    public Map<String, Integer> getNamesExp() {
        return namesExp;
    }

    public enum Type {inout, score, name}
}
