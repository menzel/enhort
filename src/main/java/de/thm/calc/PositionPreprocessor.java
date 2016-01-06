package de.thm.calc;

import java.util.ArrayList;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class PositionPreprocessor {

        public static void preprocessData(ArrayList<Long> intervalsStart, ArrayList<Long> intervalsEnd, ArrayList<String> intervalName, ArrayList<Long> intervalScore) {


        ArrayList<Long> newStart = new ArrayList<>();
        ArrayList<Long> newEnd = new ArrayList<>();
        ArrayList<String> newName = new ArrayList<>();

        if(intervalsStart.isEmpty()) return;

        long start = intervalsStart.get(0);
        long end = intervalsEnd.get(0);

        String name = intervalName.get(0);


        for (int i = 0; i < intervalsStart.size(); i++) {

            if(i < intervalsStart.size()-1 && end > intervalsStart.get(i+1)) { // overlap

                if(end < intervalsEnd.get(i+1)){
                    end = intervalsEnd.get(i+1);
                }

            }else{  //do not overlap
                newStart.add(start);
                newEnd.add(end);
                newName.add(name);

                if(i >= intervalsStart.size()-1) break; // do not get next points if this was the last

                start = intervalsStart.get(i+1);
                end = intervalsEnd.get(i+1);
                name = intervalName.get(i+1);

            }
        }

        intervalsStart.clear();
        intervalsStart.addAll(newStart);

        intervalsEnd.clear();
        intervalsEnd.addAll(newEnd);

        intervalName.clear();
        intervalName.addAll(newName);
    }

}
