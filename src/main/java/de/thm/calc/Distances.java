package de.thm.calc;

import de.thm.genomeData.InOutTrack;
import de.thm.positionData.Sites;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by menzel on 9/7/16.
 */
public class Distances {
    /**
     * Counts distances between positions
     *
     * @param intv
     * @param pos
     * @return
     * @deprecated
     */
    @Deprecated
    public Set<Map.Entry<Integer, Integer>> getAverageDistance(InOutTrack intv, Sites pos) {
        int i = 0;
        int last_i = i;

        DistanceCounter distances = new DistanceCounter();


        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();

        int intervalCount = intervalStart.size() - 1;


        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalStart.get(i) <= p) {
                i++;
            }

            if (i == 0) {
                distances.add(0);

            } else if (i == intervalCount && p > intervalEnd.get(i - 1)) { //last Interval and p not in previous
                if (p < intervalEnd.get(i) && p >= intervalStart.get(i)) {
                    distances.add(i - last_i);
                    last_i = i;
                }
            } else {
                if (p >= intervalEnd.get(i - 1)) {
                    distances.add(i - last_i);
                    last_i = i;
                }
            }
        }

        for(Integer key: (distances.distances.keySet())){
            System.out.println(key + "\t" + distances.distances.get(key));
        }

        return distances.distances.entrySet();
    }


    /**
     * Class to count the occurrences of numbers in a Map
     */
    private class DistanceCounter {
        Map<Integer, Integer> distances = new HashMap<>();

        public void add(int d){

            if(distances.containsKey(d)){
                distances.put(d, distances.get(d) + 1 );
            } else{
                distances.put(d, 1);
            }
        }

    }


}
