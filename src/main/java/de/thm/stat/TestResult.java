package de.thm.stat;

import de.thm.calc.IntersectResult;
import de.thm.genomeData.Track;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Precision;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Results of one Test together with the input params (intersect results, measured in/ out counts, names files, pvalue, fold change )
 * <p>
 * Created by Michael Menzel on 12/1/16.
 */
public final class TestResult {

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
    private final Set<String> namesMea;
    private final Set<String> namesExp;
    private final int id;
    private Type type;

    public TestResult(double pValue, IntersectResult measured, IntersectResult expected, double effectSize, Track usedInterval, Type type) {

        DecimalFormat format = new DecimalFormat("0.00E00");
        String v = format.format(pValue);

        if (pValue != Double.NaN && NumberUtils.isNumber(v)) {
            this.pValue = Double.parseDouble(format.format(pValue));
        } else {
            System.err.println(measured);
            System.err.println(expected);
            this.pValue = 1; //TODO check
        }

        this.effectSize = Precision.round(effectSize, 2);

        this.scoresExp = expected.getResultScores();
        this.scoresMea = measured.getResultScores();
        this.namesExp = expected.getResultNames().entrySet().stream().map(i -> i.getKey() + ":" + i.getValue()).collect(Collectors.toSet());
        this.namesMea = measured.getResultNames().entrySet().stream().map(i -> i.getKey() + ":" + i.getValue()).collect(Collectors.toSet());

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

    public Set<String> getNamesMea() {
        return namesMea;
    }

    public Set<String> getNamesExp() {
        return namesExp;
    }

    public enum Type {inout, score, name}
}
