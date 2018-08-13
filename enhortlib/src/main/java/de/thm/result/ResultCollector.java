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
package de.thm.result;

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.Track;
import de.thm.logo.Logo;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import de.thm.stat.TestResult;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Collects results from a single intersect run.
 * <p>
 * Is serialized and sent to interface for result display
 * <p>
 * Created by Michael Menzel on 26/1/16.
 */
public final class ResultCollector implements Result {

    private final List<TestResult> results;
    private final List<TestTrackResult> positionalResults;
    private final Sites backgroundSites;
    private final Genome.Assembly assembly;
    private List<String> tracks; //keeps a list of all known packages for the gui to display
    private Logo logo;
    private Logo other_logo;
    private List<Integer> hotspots;

    private static final Logger logger = LoggerFactory.getLogger(ResultCollector.class);
    private SortedMap<String, double[]> pca;

    public ResultCollector(Sites bgModel, Genome.Assembly assembly, List<Track> tracks) {
        results = Collections.synchronizedList(new ArrayList<>());
        positionalResults = Collections.synchronizedList(new ArrayList<>());
        backgroundSites = bgModel;
        this.tracks = tracks.stream().map(t -> String.valueOf(t.getUid())).collect(Collectors.toList());
        this.assembly = assembly;
    }

    public List<TestResult> getScoredResults(boolean showall) {

        try {
            List<TestResult> r = results.stream()
                    .filter(testResult -> testResult.getType() == TestResult.Type.score)
                    .filter(testResult -> testResult.getpValue() < 0.05 / results.size() || showall)
                    .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                    .collect(Collectors.toList());

            if (r == null)
                return new ArrayList<>();
            return r;

        } catch (NullPointerException e){
            logger.warn("Null Pointer Exp in getScoredResults");
            return new ArrayList<>();
        }
    }

    public List<TestResult> getInOutResults(boolean showall) {
        try {
            List<TestResult> r = results.stream()
                    .filter(testResult -> testResult.getType() == TestResult.Type.inout)
                    .filter(testResult -> testResult.getpValue() < 0.05 / results.size() || showall)
                    .filter(testResult -> (testResult.getMeasuredIn() >= (testResult.getMeasuredIn() + testResult.getMeasuredOut()) / 200
                            && testResult.getExpectedIn() >= (testResult.getExpectedIn() + testResult.getExpectedOut()) / 200) || showall)
                    .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                    .collect(Collectors.toList());

            if (r == null)
                return new ArrayList<>();
            return r;

        } catch (NullPointerException e){
            logger.warn("Null Pointer Exp in getInOutResults");
            return new ArrayList<>();
        }

    }

    public List<TestResult> getNamedResults(boolean showall) {

        try {
            List<TestResult> r = results.stream()
                    .filter(testResult -> testResult.getType() == TestResult.Type.name)
                    .filter(testResult -> testResult.getpValue() < 0.05 / results.size() || showall)
                    .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                    .collect(Collectors.toList());

            if (r == null)
                return new ArrayList<>();
            return r;

        } catch (NullPointerException e){
            logger.warn("Null Pointer Exp in getNamedResults");
            return new ArrayList<>();
        }

    }


    public List<TestResult> getInsignificantResults() {
        try {
            List<TestResult> r = results.stream()
                    .filter(testResult -> testResult.getpValue() >= 0.05 / results.size())
                    .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                    .collect(Collectors.toList());

            if (r == null)
                return new ArrayList<>();
            return r;

        } catch (NullPointerException e){
            logger.warn("Null Pointer Exp in getInsignificantResults");
            return new ArrayList<>();
        }


    }

    public List<TestResult> getCovariants(List<String> covariants) {
        return results.stream().filter(tr -> covariants.contains(Integer.toString(tr.getId()))).collect(Collectors.toList());
    }

    public Pair<List<String>, List<Double>> getBarplotdata() {


        List<String> names = new ArrayList<>();
        List<Double> effectsizes = new ArrayList<>();

        Double pval = 0.05 / results.size();

        results.stream()
                .filter(testResult -> testResult.getpValue() < pval)
                .filter(testResult -> testResult.getMeasuredIn() >= (testResult.getMeasuredIn() + testResult.getMeasuredOut()) / 200
                        && testResult.getExpectedIn() >= (testResult.getExpectedIn() + testResult.getExpectedOut()) / 200)
                .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                .forEach(result -> {
                    names.add(result.getName());
                    effectsizes.add(result.getEffectSize());
                });

        return new Pair<>(names, effectsizes);
    }


    public String getBarplotdataExport() {

        StringBuilder output = new StringBuilder();

        List<TestResult> filtered_results = results.stream()
                .filter(testResult -> testResult.getType() == TestResult.Type.inout)
                //.filter(testResult -> testResult.getpValue() < 0.05)
                //.filter(testResult -> testResult.getMeasuredIn() >= (testResult.getMeasuredIn()+testResult.getMeasuredOut())/100
                //       && testResult.getExpectedIn() >= (testResult.getExpectedIn()+testResult.getExpectedOut())/100)
        .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
        .collect(Collectors.toList());

        filtered_results.sort(Comparator.comparing(TestResult::getName));

        String lb = "<br>";

        // Name
        for (TestResult result : filtered_results) {
            output.append(result.getName());
            output.append(",");
        }
        output = new StringBuilder(output.substring(0, output.length() - 1));
        output.append(lb);

        // Effect Size
        output.append("pvalue,");
        for (TestResult result : filtered_results) {
            output.append(result.getpValue());
            output.append(",");
        }

        output = new StringBuilder(output.substring(0, output.length() - 1));
        output.append(lb);

        // Effect Size
        output.append("effectsize,");
        for (TestResult result : filtered_results) {
            if (result.getPercentInE() > result.getPercentInM()) { // weniger als erwartet drinn
                output.append(1 / result.getEffectSize());
            } else {
                output.append(result.getEffectSize());
            }
            output.append(",");
        }
        output = new StringBuilder(output.substring(0, output.length() - 1));
        output.append(lb);

        output.append("MeasuredIn,");
        for (TestResult result : filtered_results) {
            output.append(result.getMeasuredIn());
            output.append(",");
        }

        output = new StringBuilder(output.substring(0, output.length() - 1));
        output.append(lb);

        output.append("ExpectedIn,");
        for (TestResult result : filtered_results) {
            output.append(result.getExpectedIn());
            output.append(",");
        }

        output = new StringBuilder(output.substring(0, output.length() - 1));
        output.append(lb);

        output.append("MeasuredPercentIn,");
        for (TestResult result : filtered_results) {
            output.append(result.getPercentInM());
            output.append(",");
        }

        output = new StringBuilder(output.substring(0, output.length() - 1));
        output.append(lb);

        output.append("ExpectedPercentIn,");
        for (TestResult result : filtered_results) {
            output.append(result.getPercentInE());
            output.append(",");
        }

        output = new StringBuilder(output.substring(0, output.length() - 1));
        output.append(lb);



        return output.toString();

    }



    /**
     * Returns all significant results sorted by effect size in csv format.
     *
     * @return results in csv format.
     */
    public String getCsv() {
        StringBuilder output = new StringBuilder();

        //filter by p value and sort by effect size:
        List<TestResult> filtered_results = results.stream()
                //.filter(testResult -> testResult.getpValue() < 0.05)
                .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                .collect(Collectors.toList());

        filtered_results.sort(Comparator.comparing(TestResult::getName));

        for (TestResult result : filtered_results) {
            output.append(result.getName());
            output.append(",");
        }
        output.append("\\\\");


        for (TestResult result : filtered_results) {
            if (result.getExpectedIn() >= result.getMeasuredIn()) { // weniger als erwartet drinn
                output.append(1 / result.getEffectSize());
            } else {
                output.append(result.getEffectSize());
            }
            output.append(",");
        }


        /*
        for (TestResult result : filtered_results) {
            if(result.getpValue() > 0.05)
                output += "0,";
            else {
                if (result.getExpectedIn() < result.getMeasuredIn()) { // mehr als erwartet drinn
                    output += ((double) result.getExpectedIn()) / result.getMeasuredIn();
                }
                if (result.getExpectedIn() >= result.getMeasuredIn()) { // weniger als erwartet drinn
                    output += -1 * ((double) result.getExpectedIn()) / result.getMeasuredIn();

                }
                output += ",";
            }
        }
         **/

        return output.toString();
    }

    public Sites getBackgroundSites() {
        return backgroundSites;
    }


    public String toString() {
        StringBuilder r = new StringBuilder();

        for (TestResult result : results) {
            r.append(result.toString()).append("\n");
        }

        return r.toString();
    }

    public List<TestResult> getResults() {
        return getResults(true);
    }

    public List<TestTrackResult> getPositionalResults() {
        return positionalResults;
    }


    public List<TestResult> getResults(boolean isShowall) {
        if (isShowall)
            return this.results;
        else
            return this.results.stream()
                    .filter(testResult -> testResult.getpValue() < 0.05 / results.size())
                    .filter(testResult -> testResult.getMeasuredIn() >= (testResult.getMeasuredIn() + testResult.getMeasuredOut()) / 200
                            && testResult.getExpectedIn() >= (testResult.getExpectedIn() + testResult.getExpectedOut()) / 200)
                    .collect(Collectors.toList());
    }

    /**
     * Add test result to current collection of tests
     *
     * @param result - test result to add
     */
    public void addResult(TestResult result) {
        this.results.add(result);
    }


    /**
     * Add positional results for circ plot
     *
     * @param result - result to add
     */
    public void addResult(TestTrackResult result) {
        this.positionalResults.add(result);
    }


    /**
     * Returns how many tracks are registered as signicant on a 5% basis.
     *
     * @return count of significant tracks
     */
    public long getSignificantTrackCount() {
        return results.stream()
                .filter(testResult -> testResult.getpValue() < 0.05 / (results.size()))
                .filter(testResult -> testResult.getMeasuredIn() >= (testResult.getMeasuredIn() + testResult.getMeasuredOut()) / 200
                        && testResult.getExpectedIn() >= (testResult.getExpectedIn() + testResult.getExpectedOut()) / 200)
                .count();
    }

    /**
     * Get overall track count
     *
     * @return count of all tracks
     */
    public long getTrackCount() {
        return results.size();
    }

    public int getBgCount() {
        return backgroundSites.getPositionCount();
    }

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks){
        this.tracks = tracks;
    }

    public Logo getLogo() {
        if(logo != null)
            return logo;
        return null;
    }

    public Logo getSecondLogo() {
        if(other_logo!= null)
            return other_logo;
        return null;
    }

    public synchronized void addLogo(Logo logo) {
        if(this.logo != null)
            this.other_logo = logo;
        else
            this.logo = logo;
    }

    public double logoEffectSize() {
        if(logo == null)
            return 0;

        double sum = 0;
        List<List<Map<String, String>>> values = logo.getValues();

        for(List<Map<String, String>> position: values){
            for(Map<String, String> base: position){
                sum += Double.parseDouble(base.get("bits"));
            }
        }

        return Precision.round(sum/values.size()*10,2);
    }

    public void addHotspots(List<Integer> hotspots) {
        this.hotspots = hotspots;
    }

    public List<Integer> getHotspots() {
        return hotspots;
    }

    public Genome.Assembly getAssembly() {
        return assembly;
    }

    public void setPCA(SortedMap<String, double[]> pca) {
        this.pca = pca;
    }

    public SortedMap<String, double[]> getPca() {
        return pca;
    }
}
