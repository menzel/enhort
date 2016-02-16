package de.thm.stat;

import de.thm.genomeData.Interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 26/1/16.
 */
public class ResultCollector {

    private final List<TestResult> results;
    private int bgModelHash;

    public ResultCollector(int bgModelHash) {
        this.bgModelHash = bgModelHash;
        results = Collections.synchronizedList(new ArrayList<>());
    }

    public List<TestResult> getResults(){ return  this.results;}

    public void addResult(TestResult result){
        this.results.add(result);
    }

    public String toString(){
        String r = "";

        for(TestResult result: results){
            r += result.toString();
        }

        return r;

    }

    /**
     * Return list of all TestResults which have the given type in order sorted by effect size.
     *
     * @param type of the interval
     *
     * @return list of TestResults of type type
     */
    public List<TestResult> getResultsByType(Interval.Type type) {
        List<TestResult> r = results.stream()
                .filter(testResult -> testResult.getType()
                .equals(type))
                .filter(testResult -> testResult.getpValue() < 0.05)
                .sorted((t1, t2) -> Double.compare(t2.getEffectSize(), t1.getEffectSize()))
                .collect(Collectors.toList());

        if(r == null)
            return new ArrayList<>();
        return r;

    }

    public int getBgModelHash() {
        return bgModelHash;
    }

    public List<TestResult> getCovariants() {
        return results.stream().filter(tr -> tr.getpValue() > 0.99).collect(Collectors.toList());
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
            output += "\n";
        }

        return output;
    }

}
