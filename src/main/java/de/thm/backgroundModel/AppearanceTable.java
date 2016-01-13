package de.thm.backgroundModel;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class AppearanceTable {

    private Map<Long, Integer> appearance;


    /**
     *
     * @param intervals
     */
    public void fillTable(List<Interval> intervals, Sites positions) {
        appearance = new HashMap<>();
        Map<Interval, Integer> indices = new HashMap<>();

        //init indices map:
        for(Interval interval: intervals) {
            indices.put(interval, 0);
        }


        for(Long p : positions.getPositions()){
            Set<Integer> containing = new TreeSet<>();

            for(Interval interval: intervals){

                List<Long> intervalStart = interval.getIntervalsStart();
                List<Long> intervalEnd = interval.getIntervalsEnd();

                int i = indices.get(interval);
                int intervalCount = intervalStart.size()-1;

                while(i < intervalCount && intervalStart.get(i) <= p){
                    i++;
                }

                if(i == 0){
                     continue;

                } else if(i == intervalCount && p > intervalEnd.get(i-1)){ //last Interval and p not in previous
                    if(p < intervalEnd.get(i) && p >= intervalStart.get(i)){

                        containing.add(interval.getUid());

                    } else{
                        continue;
                    }
                }else{
                    if(p >= intervalEnd.get(i-1)){
                        continue;

                    }else{
                        containing.add(interval.getUid());
                    }
                }

                indices.put(interval, i);
            }

            if(appearance.containsKey(hash(containing))){
                appearance.put(hash(containing), appearance.get(hash(containing))+1);
            }else{
                appearance.put(hash(containing), 1);
            }
        }
    }

    protected Long hash(Set<Integer> containing) {
        long r = 0;
        int i = 0;

        for(Integer value: containing){
            r += value * Math.pow(10,i++);
        }

        return r;
    }

    protected Long hash(List<Interval> intervals) {
        List<Integer> containing = new ArrayList<>();

        for(Interval interval: intervals){
            containing.add(interval.getUid());
        }

        Collections.sort(containing);

        return hash(new TreeSet<>(containing));
    }

    /**
     *
     * @param intervals
     * @return
     */
    public int getAppearance(List<Interval> intervals){
        System.out.println("given interval ids: " +intervals.stream().map(Interval::getUid).collect(Collectors.toList()));
        System.out.println("appearance hash: " +Arrays.toString(appearance.keySet().toArray()));
        System.out.print("Hash function of given interval ids " + hash(intervals));
        System.out.println("\n_______");

        if(appearance.containsKey(hash(intervals))){
            return appearance.get(hash(intervals));

        } else{


            return 0;
        }
    }
}
