package de.thm.mTrackBg;

import de.thm.backgroundModel.BackgroundModel;
import de.thm.backgroundModel.SingleTrackBackgroundModel;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael Menzel on 17/2/16.
 */
public class ScoreMultiTrackBackgroundModel extends BackgroundModel {

    public ScoreMultiTrackBackgroundModel(Sites sites, List<Interval> intervals) {
        Interval interval = generateProbabilityInterval(sites, intervals);
        SingleTrackBackgroundModel bgModel = new SingleTrackBackgroundModel();

        Collection<Long> pos = bgModel.generatePositonsByProbability(interval, sites.getPositionCount());

        positions.addAll(pos);

    }

    private Interval generateProbabilityInterval(Sites sites, List<Interval> intervals) {
        Map<String, Double> map = new HashMap<>();
        Map<Interval, Integer> indices = new HashMap<>();

        //init indices map:
        for(Interval interval: intervals) {
            indices.put(interval, 0);
        }

        for(Long p : sites.getPositions()){
            String key = "";

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
        double sum = sites.getPositionCount();
        for(String k: map.keySet())
            map.put(k, map.get(k)/sum);


        //if( 1 == Double.compare(map.values().stream().filter(Double::isFinite).mapToDouble(i -> i).sum(), 1)) System.err.println("Not 1");

        // get p for interval without sites information

        Interval interval = Intervals.combine(intervals, map);

        //divide each value in the interval by the count of occurences of intervals with the same key
        //" split the probability to the intervals "


        Map<String, Integer> occurenceTable = new HashMap<>();

        //count occurences:
        for(String key: interval.getIntervalName()){
            if(occurenceTable.containsKey(key)){
                occurenceTable.put(key, occurenceTable.get(key)+1);
            } else {
                occurenceTable.put(key, 1);
            }
        }
        List<String> keys = interval.getIntervalName();

        List<Double> intervalScore = interval.getIntervalScore();

        for (int i = 0; i < intervalScore.size(); i++) {
            Double p = intervalScore.get(i);
            p = p / occurenceTable.get(keys.get(i));
            intervalScore.add(i, p);
        }

        interval.setIntervalScore(intervalScore); //TODO neccessary?


        return interval;
    }

    private Double divideByProb(String key, Double value, Interval probInterval) {
        //count how often the key (|value|value) is inside the names of the interval

        double prob = probInterval.getIntervalName().stream().filter(i -> i.equals(key)).count();


        //divide value by that count and return
        return value/ prob;
    }


}
