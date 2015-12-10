package de.thm.genomeData;

import de.thm.calc.PositionPreprocessor;

import java.io.File;

/**
 * Created by Michael Menzel on 9/12/15.
 */
public class IntervalDual extends Interval{


    /**
     *
     * @param file
     */
    public IntervalDual(File file) {

        initMap(intervalsStart);
        initMap(intervalsEnd);
        loadIntervalData(file);
        PositionPreprocessor.preprocessData(intervalsStart,intervalsEnd);
    }



    @Override
    protected void handleParts(String[] parts) {

        if(parts[1].matches("chr(\\d{1,2}|X|Y)")) { //TODO get other chromosoms
            intervalsStart.get(parts[1]).add(Long.parseLong(parts[3]));
            intervalsEnd.get(parts[1]).add(Long.parseLong(parts[4]));

        }else if (parts[4].matches("chr(\\d{1,2}|X|Y)")){
            intervalsStart.get(parts[4]).add(Long.parseLong(parts[5]));
            intervalsEnd.get(parts[4]).add(Long.parseLong(parts[6]));

        }

    }
}
