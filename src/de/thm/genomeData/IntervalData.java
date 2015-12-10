package de.thm.genomeData;

import java.io.File;

/**
 * Created by Michael Menzel on 8/12/15.
 */
@Deprecated
public class IntervalData extends Interval{


    /**
     *
     * @param file
     */
    public IntervalData(File file) {

        initMap();
        loadIntervalData(file);
    }


    @Override
    protected void handleParts(String[] parts) {


        if(parts[1].matches("chr(\\d{1,2}|X|Y)")) { //TODO get other chromosoms
            intervals.get(parts[1]).add(Long.parseLong(parts[3]));
            intervals.get(parts[1]).add(Long.parseLong(parts[4]));
        }

    }
}
