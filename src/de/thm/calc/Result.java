package de.thm.calc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class Result {


    private final Map<String, Integer> resultNames;
    private final Map<Long, Integer> resultScores;
    public enum Type {inout, score, named}
    private Type type;

    /**
     *
     * @return
     */
    public double[] getInOut() {
        if(type == Type.inout)
            return new double[] {getInCount(),resultNames.get("out")};

        if(type == Type.named){

            return getValues();
        }

        else //can only be Type.score, then getInOut should not be called.
            return null;
    }

    /**
     *
     * @return
     */
    private double[] getValues() {

        List<String> names = resultNames.entrySet().stream().sorted().map(Map.Entry::getKey).collect(Collectors.toList());
        double[] returnValues = new double[names.size()];

        int i = 0;
        for(String name: names){
            returnValues[i++] = resultNames.get(name);
        }

        return returnValues;
    }

    /**
     *
     * @return
     */
    private double getInCount() {
        double count = 0;

        for(String name: resultNames.keySet()){
            if(!name.matches("out"))
                count += resultNames.get(name);
        }

        return count;
    }


    /**
     *
     * @return
     */
    public Type getType() {
        if(type == Type.named && resultNames.keySet().size() == 2){
            type = Type.inout;
        }

        return type;
    }


    /**
     *
     */
    public Result() {
        this.resultNames = new HashMap<>();
        this.resultScores= new HashMap<>();
    }

    /**
     *
     * @param score
     */
    public void add(long score){
        if(type == null) type = Type.score;

        if(resultScores.containsKey(score)){
            resultScores.put(score, resultScores.get(score)+1);
        }else{
            resultScores.put(score, 1);
        }
    }

    /**
     *
     * @param name
     * @param count
     */
    public void add(String name, int count){

        if(type == null) type = Type.named;

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

        if(type == null) type = Type.named;

        if(resultNames.containsKey(name)){
            resultNames.put(name, resultNames.get(name)+1);
        }else{
            resultNames.put(name,1);
        }

    }

    /**
     *
     * @return
     */
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
}

