package de.thm.calc;

import de.thm.genomeData.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Results of one intersect run with the count of inside values. And depening on the interval type result names and result scores.
 *
 * Created by Michael Menzel on 8/12/15.
 */
public final class IntersectResult<T extends Interval>{


    private final Map<String, Integer> resultNames;
    private final List<Double> resultScores;
    private int in;
    private int out;
    private final T usedInterval;


    /**
     * Constructor
     */
    IntersectResult(T usedInterval) {
        this.usedInterval = usedInterval;
        this.resultNames = new HashMap<>();
        this.resultScores = new ArrayList<>();
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
        return usedInterval.getName() + "\t" + in + "\t" + out;
    }

    public int getIn() {
        return in;
    }

    void setIn(int in) {
        this.in = in;
    }

    void setOut(int out) { this.out = out; }

    public int getOut(){ return this.out; }

    public Class getType(){
        return getClass();
    }
}

