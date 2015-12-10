package de.thm.genomeData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 10/12/15.
 */
public class IntervalNamed extends Interval {

    protected Map<String, ArrayList<String>> intervalName = new HashMap<>();


    /**
     *
     * @param file
     */
    public IntervalNamed(File file) {

        initMap(intervalsStart);
        initMap(intervalsEnd);
        initNameMap(intervalName);

        loadIntervalData(file);
    }


    @Override
    protected void handleParts(String[] parts) {
       if(parts[1].matches("chr(\\d{1,2}|X|Y)")) { //TODO get other chromosoms
           intervalsStart.get(parts[1]).add(Long.parseLong(parts[2]));
           intervalsEnd.get(parts[1]).add(Long.parseLong(parts[3]));

           intervalName.get(parts[1]).add(parts[4]);
       }
    }

    /**
     *
     */
    protected void initNameMap(Map<String, ArrayList<String>> map){

        for(int i = 1; i <= 22; i++){
            map.put("chr"+i, new ArrayList<>());
        }

        map.put("chrX", new ArrayList<>());
        map.put("chrY", new ArrayList<>());

    }

    public Map<String, ArrayList<String>> getIntervalName() {
        return intervalName;
    }
}
