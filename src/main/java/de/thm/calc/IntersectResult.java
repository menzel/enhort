package de.thm.calc;

import de.thm.genomeData.Interval;

import java.util.*;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class IntersectResult {


    public final Map<String, Integer> resultNames;
    public final List<Double> resultScores;
    private int in;
    private Interval usedInterval;


    public Interval.Type getType() {
        return usedInterval.getType();
    }

    public Map<String, Integer> getResultNames() {
        return resultNames;
    }

    /**
     *
     */
    public IntersectResult() {
        this.resultNames = new HashMap<>();
        this.resultScores = new ArrayList<>();
    }


    /**
     *
     * @param score
     */
    public void add(Double score){
        resultScores.add(score);
    }

    /**
     *
     * @param name
     * @param count
     */
    public void add(String name, int count){

        if(resultNames.containsKey(name)){
            resultNames.put(name, resultNames.get(name)+count);
        }else{
            resultNames.put(name,count);
        }

    }

    /**
     *
     * @param name
     */
    public void add(String name){

        if(resultNames.containsKey(name)){
            resultNames.put(name, resultNames.get(name)+1);
        }else{
            resultNames.put(name,1);
        }

    }

    /**
     *
     *
     * @return
     */
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


    public void setIn(int in) {
        this.in = in;
    }

    public int getIn() {
        return in;
    }

    public Integer getOut() {
        return resultNames.get("out");
    }

    public void setUsedInterval(Interval usedInterval) {
        this.usedInterval = usedInterval;
    }

}

