package de.thm.mTrackBg;

import de.thm.backgroundModel.BackgroundModel;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael Menzel on 17/2/16.
 */
public class ScoreMultiTrackBackgroundModel extends BackgroundModel {

    public ScoreMultiTrackBackgroundModel(Sites sites, List<Interval> intervals) {
        Interval interval = generateProbabilityInterval(sites, intervals);

    }

    private Interval generateProbabilityInterval(Sites sites, List<Interval> intervals) {
        Map<String, Double> map = new HashMap<>();
        Map<Interval, Integer> indices = new HashMap<>();
        String key = "";

        //init indices map:
        for(Interval interval: intervals) {
            indices.put(interval, 0);
        }

        for(Long p : sites.getPositions()){
            key = "";

            for(Interval interval:intervals){

                List<Long> intervalStart = interval.getIntervalsStart();
                List<Long> intervalEnd = interval.getIntervalsEnd();

                int i = indices.get(interval);
                int intervalCount = intervalStart.size()-1;

                while(i < intervalCount && intervalStart.get(i) <= p){
                    i++;
                }

                if(i == 0){
                    if(p >= intervalStart.get(i)){
                        key += "|" + interval.getIntervalScore().get(i);
                    }

                } else if(i == intervalCount && p > intervalEnd.get(i-1)){ //last Interval and p not in previous
                    if(p < intervalEnd.get(i) && p >= intervalStart.get(i)){

                        key += "|" + interval.getIntervalScore().get(i);

                    } else{
                        key += "|";
                        continue;
                    }
                }else{
                    if(p >= intervalEnd.get(i-1)){
                        key += "|";
                        continue; // not inside the last interval

                    }else{
                        key += "|" + interval.getIntervalScore().get(i);
                    }
                }

                indices.put(interval, i);

            }

            if(map.containsKey(key)){
                map.put(key, map.get(key)+1);
            } else {
                map.put(key, 1.);
            }
        }

        // normalize values
        double sum = map.values().stream().mapToDouble(Double::doubleValue).sum();
        //map.keySet().stream().map(k -> map.put(k,map.get(k)/sum));

        for(String k: map.keySet())
            map.put(k, map.get(k)/sum);

        // get p for interval without sites information

        // use conversion list (score -> probability to create prob interval

        Interval interval = Intervals.combine(intervals, map);

        return interval;
    }

}
