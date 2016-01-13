package de.thm.calc;

import de.thm.genomeData.Interval;

import java.util.*;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class Result {


    private final Map<String, Integer> resultNames;
    //private final Map<Long, Integer> resultScores;
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
    public Result() {
        this.resultNames = new HashMap<>();
        //this.resultScores= new HashMap<>();
    }

    /**
     *
     * @param score
     */
    public void add(long score){

        add(Long.toString(score));

        /*if(resultScores.containsKey(score)){
            resultScores.put(score, resultScores.get(score)+1);
        }else{
            resultScores.put(score, 1);
        }*/
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
    /*
    public String getResultScores() {
        String val = "";
        List<Long> keys = new ArrayList<>(resultScores.keySet());
        Collections.sort(keys);

        val = "200\t" + resultNames.get("out") + "\n";
        for(Long key: keys){
            System.out.println(key + "\t" + resultScores.get(key));

            val = val + key + "\t" + resultScores.get(key) + "\n";
        }

        return val;
    }
    */

    @Override
    public String toString() {

        switch (getType()){
            case inout:
                return "in: " + in + "\n" +
                        "out: " + resultNames.get("out") + "\n";
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

