package de.thm.resultCollector;

import de.thm.genomeData.Interval;
import de.thm.stat.TestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 26/1/16.
 */
public class ResultCollector {

    private static final ResultCollector resultCollector = new ResultCollector();
    private final List<TestResult> results;

    public static ResultCollector getInstance(){
        return resultCollector;
    }

    private ResultCollector() {
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

    public String toCsv() {
        String data = "";

        results.sort((result, t1) -> (result.getpValue() >= t1.getpValue())? 1 : -1);

        for(TestResult tr: results) {
            data += tr.toCsv();
        }

        return data;
    }

    public List<TestResult> getResultsByType(Interval.Type score) {
        return  results.stream().filter(testResult -> testResult.getType().equals(score)).collect(Collectors.toList());
    }
}
