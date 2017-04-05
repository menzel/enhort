package de.thm.stat;

import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.positionData.Sites;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.util.Precision;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Collects results from a single intersect run.
 * <p>
 * Is serialized and sent to interface for result display
 * <p>
 * Created by Michael Menzel on 26/1/16.
 */
public final class ResultCollector implements Serializable{

    private final List<TestResult> results;
    private final Sites backgroundSites;
    private List<String> knownPackages; //keeps a list of all known packages for the gui to display
    private Logo logo;
    private Logo other_logo;
    private ScoredTrack hotspots;

    public ResultCollector(Sites bgModel, GenomeFactory.Assembly assembly) {
        results = Collections.synchronizedList(new ArrayList<>());
        backgroundSites = bgModel;
        knownPackages = TrackFactory.getInstance().getTrackPackageNames(assembly);
    }

    public List<TestResult> getScoredResults() {

        try {
            List<TestResult> r = results.stream()
                    .filter(testResult -> testResult.getType() == TestResult.Type.score)
                    .filter(testResult -> testResult.getpValue() < 0.05 / (testResult.getExpectedIn() + testResult.getExpectedOut()))
                    .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                    .collect(Collectors.toList());

            if (r == null)
                return new ArrayList<>();
            return r;

        } catch (NullPointerException e){
            System.err.println("Null Pointer Exp in getScoredResults");
            return new ArrayList<>();
        }
    }

    public List<TestResult> getInOutResults() {
        try {
            List<TestResult> r = results.stream()
                    .filter(testResult -> testResult.getType() == TestResult.Type.inout)
                    .filter(testResult -> testResult.getpValue() < 0.05 / (testResult.getExpectedIn() + testResult.getExpectedOut()))
                    .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                    .collect(Collectors.toList());

            if (r == null)
                return new ArrayList<>();
            return r;

        } catch (NullPointerException e){
            System.err.println("Null Pointer Exp in getInOutResults");
            return new ArrayList<>();
        }

    }

    public List<TestResult> getNamedResults() {

        try {
            List<TestResult> r = results.stream()
                    .filter(testResult -> testResult.getType() == TestResult.Type.name)
                    .filter(testResult -> testResult.getpValue() < 0.05 / (testResult.getExpectedIn() + testResult.getExpectedOut()))
                    .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                    .collect(Collectors.toList());

            if (r == null)
                return new ArrayList<>();
            return r;

        } catch (NullPointerException e){
            System.err.println("Null Pointer Exp in getNamedResults");
            return new ArrayList<>();
        }

    }


    public List<TestResult> getInsignificantResults() {
        try {
            List<TestResult> r = results.stream()
                    .filter(testResult -> testResult.getpValue() >= 0.05)
                    .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                    .collect(Collectors.toList());

            if (r == null)
                return new ArrayList<>();
            return r;

        } catch (NullPointerException e){
            System.err.println("Null Pointer Exp in getInsignificantResults");
            return new ArrayList<>();
        }


    }

    public List<TestResult> getCovariants(List<String> covariants) {
        return results.stream().filter(tr -> covariants.contains(Integer.toString(tr.getId()))).collect(Collectors.toList());
    }

    public Pair<List<String>, List<Double>> getBarplotdata() {


        List<String> names = new ArrayList<>();
        List<Double> effecsizes = new ArrayList<>();

        results.stream()
        .filter(testResult -> testResult.getpValue() < 0.05)
        .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
        .forEach(result ->{
            names.add(result.getName());

            if (result.getPercentInE() > result.getPercentInM()) // if less in data than expected
                effecsizes.add(1 / result.getEffectSize()); // invert value
             else effecsizes.add(result.getEffectSize());

        });

        return new Pair<>(names, effecsizes);
    }


    public String getBarplotdataExport() {

        String output = "";

        List<TestResult> filtered_results = results.stream()
                //.filter(testResult -> testResult.getpValue() < 0.05)
        .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
        .collect(Collectors.toList());

        filtered_results.sort(Comparator.comparing(TestResult::getName));

        // Name
        for (TestResult result : filtered_results) {
            output += result.getName();
            output += ",";
        }
        output = output.substring(0,output.length()-1);
        output += "\n";

        // Effect Size
        output += "pvalue,";
        for (TestResult result : filtered_results) {
            output += result.getpValue();
            output += ",";
        }

        output = output.substring(0,output.length()-1);
        output += "\n";

        // Effect Size
        output += "effectsize,";
        for (TestResult result : filtered_results) {
            if (result.getPercentInE() > result.getPercentInM()) { // weniger als erwartet drinn
                output += 1 / result.getEffectSize();
            } else {
                output += result.getEffectSize();
            }
            output += ",";
        }
        output = output.substring(0,output.length()-1);
        output += "\n";

        return output;

    }



    /**
     * Returns all significant results sorted by effect size in csv format.
     *
     * @return results in csv format.
     */
    public String getCsv() {
        String output = "";

        //filter by p value and sort by effect size:
        List<TestResult> filtered_results = results.stream()
                //.filter(testResult -> testResult.getpValue() < 0.05)
                .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                .collect(Collectors.toList());

        filtered_results.sort(Comparator.comparing(TestResult::getName));

        for (TestResult result : filtered_results) {
            output += result.getName();
            output += ",";
        }
        output += "\\\\";


        for (TestResult result : filtered_results) {
            if (result.getExpectedIn() >= result.getMeasuredIn()) { // weniger als erwartet drinn
                output += 1 / result.getEffectSize();
            } else {
                output += result.getEffectSize();
            }
            output += ",";
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

        return output;
    }

    public Sites getBackgroundSites() {
        return backgroundSites;
    }


    public String toString() {
        String r = "";

        for (TestResult result : results) {
            r += result.toString() + "\n";
        }

        return r;
    }

    public List<TestResult> getResults() {
        return this.results;
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
     * Returns how many tracks are registered as signicant on a 5% basis.
     *
     * @return count of significant tracks
     */
    public long getSignificantTrackCount() {
        return results.stream()
                .filter(testResult -> testResult.getpValue() < 0.05)
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

    public List<String> getKnownPackages() {
        return knownPackages;
    }

    public void setKnownPackages(List<String> packages){
        this.knownPackages = packages;
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

    public void addLogo(Logo logo) {
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

    public void addHotspot(ScoredTrack hotspots) {
        this.hotspots = hotspots;
    }

    public ScoredTrack getHotspots() {
        return hotspots;
    }
}
