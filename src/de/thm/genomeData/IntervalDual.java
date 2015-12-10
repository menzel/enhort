package de.thm.genomeData;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

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
        preprocessData(intervalsStart,intervalsEnd);
    }

    private void preprocessData(Map<String, ArrayList<Long>> intervalsStart, Map<String, ArrayList<Long>> intervalsEnd) {


        for (String chromosom : intervalsStart.keySet()) {

            long start = 0;
            long end = 0;

            ArrayList<Long> currentStart = intervalsStart.get(chromosom);
            ArrayList<Long> currentEnd = intervalsEnd.get(chromosom);

            ArrayList<Long> newStart = new ArrayList<>();
            ArrayList<Long> newEnd = new ArrayList<>();

            if(currentStart.isEmpty()) continue;

            start = currentStart.get(0);
            end = currentEnd.get(0);

            for (int i = 0; i < currentStart.size(); i++) {

                if(i < currentStart.size()-1 && end > currentStart.get(i+1)) { // overlap

                    if(end < currentEnd.get(i+1)){
                        end = currentEnd.get(i+1);
                    }

                }else{  //do not overlap
                    newStart.add(start);
                    newEnd.add(end);

                    if(i >= currentStart.size()-1) break; // do not get next points if this was the last

                    start = currentStart.get(i+1);
                    end = currentEnd.get(i+1);

                }
            }

            currentStart.clear();
            currentStart.addAll(newStart);

            currentEnd.clear();
            currentEnd.addAll(newEnd);

        }
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
