package de.thm.genomeData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 9/12/15.
 */
public class IntervalDual extends Interval{

    protected Map<String, ArrayList<Long>> intervalsStart = new HashMap<>();
    protected Map<String, ArrayList<Long>> intervalsEnd = new HashMap<>();

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

            for (int i = 0; i < intervalsStart.size()-1; i++) {


                if(end > currentStart.get(i+1)) { // overlap
                    if(end < currentStart.get(i+1)){
                        end = currentStart.get(i+1);
                    }

                }else{  //do not overlap
                    newStart.add(start);
                    newEnd.add(end);

                    start = currentStart.get(i);
                    end = currentEnd.get(i);

                }
            }

            currentStart.clear();
            currentStart.addAll(newStart);

            currentEnd.clear();
            currentEnd.addAll(newEnd);


            for(int i = 0; i < currentStart.size(); i++){
                System.out.println(currentStart.get(i) + " " + currentEnd.get(i));

            }
        }
    }


    /**
     *
     */
    protected void initMap(Map<String, ArrayList<Long>> map){

        for(int i = 1; i <= 22; i++){
            map.put("chr"+i, new ArrayList<>());
        }

        map.put("chrX", new ArrayList<>());
        map.put("chrY", new ArrayList<>());

    }

    public Map<String, ArrayList<Long>> getIntervalStarts() {
        return intervalsStart;
    }

    public Map<String, ArrayList<Long>> getIntervalsEnd() {
        return intervalsEnd;
    }

    @Override
    protected void handleParts(String[] parts) {

        intervalsStart.get(parts[1]).add(Long.parseLong(parts[3]));
        intervalsEnd.get(parts[1]).add(Long.parseLong(parts[4]));

    }
}
