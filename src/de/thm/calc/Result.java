package de.thm.calc;

import java.util.*;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class Result {


    private final Map<String, Integer> resultNames;
    private final Map<Long, Integer> resultScores;
    private final int classes = 1;

    public Result() {
        this.resultNames = new HashMap<>();
        this.resultScores= new HashMap<>();
    }

    public void add(long score){
        score = (int) Math.round(score / classes);

        if(resultScores.containsKey(score)){
            resultScores.put(score, resultScores.get(score)+1);
        }else{
            resultScores.put(score, 1);
        }
    }

    public void add(String name, int count){

        if(resultNames.containsKey(name)){
            resultNames.put(name, resultNames.get(name)+count);
        }else{
            resultNames.put(name,count);
        }

    }

    public void add(String name){

        if(resultNames.containsKey(name)){
            resultNames.put(name, resultNames.get(name)+1);
        }else{
            resultNames.put(name,1);
        }

    }

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

    @Override
    public String toString() {
        return resultNames.toString();
    }

    public int getA(){

        return resultNames.get("in");

    }

    public int getB(){

        return resultNames.get("out");
    }
}

