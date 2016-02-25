package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.genomeData.Interval.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Results of one intersect run with the count of inside values. And depening on the interval type result names and result scores.
 *
 * Created by Michael Menzel on 8/12/15.
 */
public final class IntersectResult {


    private final Map<String, Integer> resultNames;
    private final List<Double> resultScores;
    private int in;
    private Interval usedInterval;


    /**
     * Constructor
     */
    IntersectResult() {
        this.resultNames = new HashMap<>();
        this.resultScores = new ArrayList<>();
    }

    public Type getType() {
        return usedInterval.getType();
    }

    public Map<String, Integer> getResultNames() {
        return resultNames;
    }

    /**
     * Adds a single score to the result
     *
     * @param score  - score value to add
     */
    void add(Double score){
        resultScores.add(score);
    }

    /**
     * Adds count to the given result name
     *
     * @param name - result name string
     * @param count - count to add
     */
    public void add(String name, int count){

        if(resultNames.containsKey(name)){
            resultNames.put(name, resultNames.get(name)+count);
        }else{
            resultNames.put(name,count);
        }

    }

    /**
     * Adds 1 to the given result name
     *
     * @param name - result name
     */
    void add(String name){

        if(resultNames.containsKey(name)){
            resultNames.put(name, resultNames.get(name)+1);
        }else{
            resultNames.put(name,1);
        }

    }

    public List<Double> getResultScores() {
        return resultScores;
    }

    @Override
    public String toString() {

        switch (getType()){
            case inout:
                return "in: " + in +
                        " out: " + resultNames.get("out") + "\n";
            case named:
                return resultNames.toString() + "\n";

            case score:
                return resultNames.toString() + "\n";
            default:
                return "";
        }
    }

    public int getIn() {
        return in;
    }

    void setIn(int in) {
        this.in = in;
    }

    public Integer getOut() {
        return resultNames.get("out");
    }

    void setUsedInterval(Interval usedInterval) {
        this.usedInterval = usedInterval;
    }

}

