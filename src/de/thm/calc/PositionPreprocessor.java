package de.thm.calc;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class PositionPreprocessor {

        public static void preprocessData(Map<String, ArrayList<Long>> intervalsStart, Map<String, ArrayList<Long>> intervalsEnd, Map<String, ArrayList<String>> intervalName, Map<String, ArrayList<Long>> intervalScore) {


        for (String chromosom : intervalsStart.keySet()) {


            ArrayList<Long> currentStart = intervalsStart.get(chromosom);
            ArrayList<Long> currentEnd = intervalsEnd.get(chromosom);
            ArrayList<String> currentName = intervalName.get(chromosom);
            ArrayList<Long> currentScore = intervalScore.get(chromosom);

            ArrayList<Long> newStart = new ArrayList<>();
            ArrayList<Long> newEnd = new ArrayList<>();

            ArrayList<String> newName = new ArrayList<>();
            ArrayList<Long> newScore = new ArrayList<>();

            if(currentStart.isEmpty()) continue;

            long start = currentStart.get(0);
            long end = currentEnd.get(0);

            String name = currentName.get(0);
            long score = currentScore.get(0);


            for (int i = 0; i < currentStart.size(); i++) {

                if(i < currentStart.size()-1 && end > currentStart.get(i+1)) { // overlap

                    if(end < currentEnd.get(i+1)){
                        end = currentEnd.get(i+1);
                    }

                }else{  //do not overlap
                    newStart.add(start);
                    newEnd.add(end);
                    newName.add(name);
                    newScore.add(score);

                    if(i >= currentStart.size()-1) break; // do not get next points if this was the last

                    start = currentStart.get(i+1);
                    end = currentEnd.get(i+1);
                    name = currentName.get(i+1);
                    score = currentScore.get(i + 1);

                }
            }

            currentStart.clear();
            currentStart.addAll(newStart);

            currentEnd.clear();
            currentEnd.addAll(newEnd);

        }
    }

}
