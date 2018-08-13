// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.stat;

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.NamedTrack;
import de.thm.genomeData.tracks.ScoredTrack;
import de.thm.genomeData.tracks.Track;
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
public class TestResult implements Serializable{

    private final double pValue;
    private final double effectSize;
    private final int measuredIn;
    private final int measuredOut;
    private final int expectedIn;
    private final int expectedOut;
    private final List<Double> scoresMea;
    private final List<Double> scoresExp;
    private final Map<String, Integer> namesMea;
    private final Map<String, Integer> namesExp;
    private final Track track;

    public TestResult(double pValue, TestTrackResult measured, TestTrackResult expected, double effectSize, Track track) {

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

        this.track = track;
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
        return Precision.round((double) measuredIn / ((double) (measuredOut + measuredIn)) * 100, 2);
    }

    public double getPercentOutM() {
        return Precision.round((double) measuredOut / ((double) (measuredOut + measuredIn)) * 100, 2);
    }

    public double getPercentInE() {
        return Precision.round((double) expectedIn / ((double) (expectedOut + expectedIn)) * 100, 2);
    }

    public double getPercentOutE() {
        return Precision.round((double) expectedOut / ((double) (expectedOut + expectedIn)) * 100, 2);
    }

    public String toString() {
        return track.getName() + " Fold change Ratio: " + effectSize + "\n" +
                "mea in " + measuredIn + " out " + measuredOut + "\n" +
                "exp in " + expectedIn + " out " + expectedOut + "\n" +
                "\n=====\n";
    }

    public String getName() {
        return track.getName();
    }

    public double getEffectSize() {
        return effectSize;
    }

    public String getDescription() {
        return track.getDescription();
    }

    public int getId() {
        return this.track.getUid();
    }

    public Type getType() {
        if (track instanceof InOutTrack)
            return Type.inout;
        if (track instanceof ScoredTrack)
            return Type.score;
        if (track instanceof NamedTrack)
            return Type.name;
        return null;
    }

    public Track getTrack() {
        return track;
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
