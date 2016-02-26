package de.thm.stat;

import de.thm.genomeData.InOutInterval;
import de.thm.genomeData.NamedTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collects results from a single intersect run.
 *
 * Created by Michael Menzel on 26/1/16.
 */
public final class ResultCollector {

    private final List<TestResult> results;
    private final Sites backgroundSites;

    private enum Type {inout, named, scored};

    public ResultCollector(Sites bgModel) {
        results = Collections.synchronizedList(new ArrayList<>());
        backgroundSites = bgModel;
    }

    public List<TestResult> getScoredResults() {
        List<TestResult> r = results.stream()
                .filter(testResult -> testResult.getType() == ScoredTrack.class)
                .filter(testResult -> testResult.getpValue() < 0.05)
                .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                .collect(Collectors.toList());

        if(r == null)
            return new ArrayList<>();
        return r;
    }

    public List<TestResult> getInOutResults() {
        List<TestResult> r = results.stream()
                .filter(testResult -> testResult.getType() == InOutInterval.class)
                .filter(testResult -> testResult.getpValue() < 0.05)
                .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                .collect(Collectors.toList());

        if(r == null)
            return new ArrayList<>();
        return r;

    }

    public List<TestResult> getNamedResults() {
        List<TestResult> r = results.stream()
                .filter(testResult -> testResult.getType() == NamedTrack.class)
                .filter(testResult -> testResult.getpValue() < 0.05)
                .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                .collect(Collectors.toList());

        if(r == null)
            return new ArrayList<>();
        return r;

    }




    public List<TestResult> getCovariants(List<String> covariants) {
        return results.stream().filter(tr -> covariants.contains(tr.getId())).collect(Collectors.toList());
    }

    /**
     * Returns all significant results sorted by effect size in csv format.
     *
     * @return results in csv format.
     */
    public String getCsv(){
        String output = "Track name, p value, effect size";

        //filter by p value and sort by effect size:
        List<TestResult> filtered_results = results.stream()
                .filter(testResult -> testResult.getpValue() < 0.05)
                .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                .collect(Collectors.toList());

        for(TestResult result: filtered_results){
            output += result.getName();
            output += ",";
            output += result.getpValue();
            output += ",";
            output += result.getEffectSize();
            output += "<br>";
        }

        return output;
    }

    public Sites getBackgroundSites() {
        return backgroundSites;
    }


    public String toString(){
        String r = "";

        for(TestResult result: results){
            r += result.toString();
        }

        return r;
    }

    public List<TestResult> getResults(){ return  this.results;}

    /**
     * Add test result to current collection of tests
     *
     * @param result - test result to add
     */
    public void addResult(TestResult result){
        this.results.add(result);
    }


    /**
     * Returns how many tracks are registered as signicant on a 5% basis.
     *
     * @return count of significant tracks
     */
    public long getSignificantTrackCount(){
        return results.stream()
               .filter(testResult -> testResult.getpValue() < 0.05)
                .count();
    }

    /**
     * Get overall track count
     *
     * @return count of all tracks
     */
    public long getTrackCount(){
        return results.size();
    }

}
