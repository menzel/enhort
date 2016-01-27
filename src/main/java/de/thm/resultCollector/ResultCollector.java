package de.thm.resultCollector;

import de.thm.stat.TestResult;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Michael Menzel on 26/1/16.
 */
public class ResultCollector {

    private static ResultCollector resultCollector;
    private final List<TestResult> results;

    public static ResultCollector getInstance(){
        if(resultCollector == null){
            resultCollector = new ResultCollector();
        }

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
        JSONObject obj = new JSONObject();

        for(TestResult result: results) {
            obj.put(result.getTrackname(), result.toJson());
        }

        return obj.toString();
    }
}
