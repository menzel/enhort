package de.thm.stat;

import de.thm.genomeData.Interval;

import java.util.ArrayList;
import java.util.Arrays;
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

    public String toJson() {
        String output = "[";

        //sort by p value
        results.sort((result, t1) -> (result.getpValue() >= t1.getpValue())? 1 : -1);

        // only keep results from inout checks
        List<TestResult> inoutResults = results.stream().filter(p -> p.getType().equals(Interval.Type.inout)).collect(Collectors.toList());

        String names[] = new String[inoutResults.size()];
        int data[] = new int[inoutResults.size()];

        int i = 0;
        for(TestResult tr: inoutResults) {
            names[i] = '"' + tr.getTrackname().substring(0,tr.getTrackname().indexOf(".")) + '"';

            if(tr.getExpectedIn() <= tr.getMeasuredIn())
                data[i++] = tr.getExpectedIn();
            else
                data[i++] = -tr.getExpectedIn();
        }

        output += Arrays.toString(names);
        output += ",";
        output += Arrays.toString(data);
        output += ",";

        i = 0;
        for(TestResult tr: inoutResults) {
            names[i] = '"' + tr.getTrackname().substring(0,tr.getTrackname().indexOf(".")) + '"';

            if(tr.getExpectedIn() <= tr.getMeasuredIn())
                data[i++] = tr.getMeasuredIn();
            else
                data[i++] = -tr.getMeasuredIn();
        }


        output += Arrays.toString(names);
        output += ",";
        output += Arrays.toString(data);


        return output + "]";
    }

    public List<TestResult> getResultsByType(Interval.Type score) {
        return  results.stream().filter(testResult -> testResult.getType().equals(score)).collect(Collectors.toList());
    }

    public String toBubblesJson() {
        String output = "[";

        //sort by p value
        results.sort((result, t1) -> (result.getpValue() >= t1.getpValue())? 1 : -1);

        // only keep results from inout checks
        List<TestResult> inoutResults = results.stream().filter(p -> p.getType().equals(Interval.Type.inout)).collect(Collectors.toList());


        List<Integer> me = new ArrayList<>();
        me.addAll(inoutResults.stream().map(TestResult::getMeasuredIn).collect(Collectors.toList()));
        output += Arrays.toString(me.toArray());
        output += ",";

        List<Integer> ex = new ArrayList<>();
        ex.addAll(inoutResults.stream().map(TestResult::getExpectedIn).collect(Collectors.toList()));
        output += Arrays.toString(ex.toArray());
        output += ",";

        List<String> names = new ArrayList<>();
        names.addAll(inoutResults.stream().map(TestResult::getTrackname).map(p -> '"' + p + '"').collect(Collectors.toList()));
        output += Arrays.toString(names.toArray());
        output += ",";


        List<Double> pv = new ArrayList<>();
        pv.addAll(inoutResults.stream().map(TestResult::getpValue).map(p -> (1-Math.log10(p)*5)).collect(Collectors.toList()));
        output += Arrays.toString(pv.toArray());

        return output + "]";

    }
}