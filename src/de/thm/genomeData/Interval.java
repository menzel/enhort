package de.thm.genomeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public abstract class Interval {

    protected Map<String, ArrayList<Long>> intervals = new HashMap<>();
    private int positionCount;


    /**
     *
     */
    protected void initMap(){

        for(int i = 1; i <= 22; i++){
            intervals.put("chr"+i, new ArrayList<>());
        }

        intervals.put("chrX", new ArrayList<>());
        intervals.put("chrY", new ArrayList<>());

    }


    public Map<String, ArrayList<Long>> getIntervals() {
        return intervals;
    }
}
