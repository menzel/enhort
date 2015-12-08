package de.thm.genomeData;

import de.thm.positionData.Sites;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class IntervalData implements  Interval{

    ArrayList<Long> intervals = new ArrayList<>();


    private void loadIntervalData(File file){

    }

    @Override
    public boolean isIn(Sites pos) {
        return false;
    }
}
