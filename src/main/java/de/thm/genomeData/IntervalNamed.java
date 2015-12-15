package de.thm.genomeData;

import de.thm.calc.PositionPreprocessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Michael Menzel on 10/12/15.
 */
public class IntervalNamed extends Interval {

    public IntervalNamed(){
        super();
    }

    /**
     *
     * @param file
     */
    public IntervalNamed(File file, Type type) {

        this.type = type;

        initMap(intervalsStart);
        initMap(intervalsEnd);
        initNameMap(intervalName);
        initMap(intervalScore);

        loadIntervalData(file);

        if(type == Type.inout)
            PositionPreprocessor.preprocessData(intervalsStart,intervalsEnd,intervalName, intervalScore);
    }


    @Override
    protected void handleParts(String[] parts) {
       if(parts[0].matches("chr(\\d{1,2}|X|Y)")) { //TODO get other chromosoms
           intervalsStart.get(parts[0]).add(Long.parseLong(parts[1]));
           intervalsEnd.get(parts[0]).add(Long.parseLong(parts[2]));

           intervalName.get(parts[0]).add(parts[3]);
           intervalScore.get(parts[0]).add(Long.parseLong(parts[4]));
       }
    }


    public Map<String, ArrayList<String>> getIntervalName() {
        return intervalName;
    }
}
