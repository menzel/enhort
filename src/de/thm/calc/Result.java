package de.thm.calc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class Result {


    private final Map<String, Integer> result;

    public Result() {
        this.result = new HashMap<>();
    }

    public void add(String name){

        if(result.containsKey(name)){
            result.put(name,result.get(name)+1);
        }else{
            result.put(name,1);
        }

    }

    @Override
    public String toString() {
        return result.toString();
    }

    public int getA(){
        int r = 0;

        for(Integer i: result.values()){
            r += i;
        }

        return r - result.get("out");
    }

    public int getB(){
        return result.get("out");
    }
}

