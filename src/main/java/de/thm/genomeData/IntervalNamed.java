package de.thm.genomeData;

import de.thm.calc.PositionPreprocessor;
import de.thm.misc.ChromosomSizes;

import java.io.File;

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

        loadIntervalData(file);

        if(type == Type.inout)
            PositionPreprocessor.preprocessData(intervalsStart,intervalsEnd,intervalName, intervalScore);

    }


    @Override
    protected void handleParts(String[] parts) {

       ChromosomSizes chrSizes = ChromosomSizes.getInstance();

       if(parts[0].matches("chr(\\d{1,2}|X|Y)")) { //TODO get other chromosoms
           long offset = chrSizes.offset(parts[0]); //handle null pointer exc if chromosome name is not in list

           intervalsStart.add(Long.parseLong(parts[1]) + offset);
           intervalsEnd.add(Long.parseLong(parts[2])+ offset);

           intervalName.add(parts[3]);

           if(parts.length > 4)
                intervalScore.add(Long.parseLong(parts[4]));
           else
                intervalScore.add(0L);
       }
    }
}
