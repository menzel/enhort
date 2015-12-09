package de.thm.genomeData;

import java.io.File;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class IntervalData extends Interval{


    /**
     *
     * @param file
     */
    public IntervalData(File file) {
        super();

        initMap();
    }


    @Override
    protected void handleParts(String[] parts) {

        intervals.get(parts[1]).add(Long.parseLong(parts[3]));
        intervals.get(parts[1]).add(Long.parseLong(parts[4]));

    }
}
