package de.thm.mTrackBg;

import de.thm.backgroundModel.BackgroundModel;
import de.thm.backgroundModel.SingleTrackBackgroundModel;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.*;

/**
 * Created by Michael Menzel on 17/2/16.
 */
public class ScoreMultiTrackBackgroundModel extends BackgroundModel {

    public ScoreMultiTrackBackgroundModel(Sites sites, List<Interval> covariants) {
        Interval interval = generateProbabilityInterval(sites, covariants);
        SingleTrackBackgroundModel bgModel = new SingleTrackBackgroundModel();

        //inside positions
        Collection<Long> pos = bgModel.generatePositonsByProbability(interval, sites.getPositionCount());

        positions.addAll(pos);

    }

    public ScoreMultiTrackBackgroundModel() { }

    Interval generateProbabilityInterval(Sites sites, List<Interval> intervals) {

        Map<String, Double> sitesOccurence = fillOccurenceMap(intervals,sites);

        // normalize values
        double sum = sites.getPositionCount();

        //substract all points that are outside
        //sum -= sitesOccurence.get("||");

        for(String k: sitesOccurence.keySet())
            sitesOccurence.put(k, sitesOccurence.get(k)/sum);


        //if( 1 == Double.compare(sitesOccurence.values().stream().filter(Double::isFinite).mapToDouble(i -> i).sum(), 1)) System.err.println("Not 1");

        // get p for interval without sites information

        Interval interval = Intervals.combine(intervals, sitesOccurence);

        //divide each value in the interval by the count of occurences of intervals with the same key
        //" split the probability to the intervals "


        Map<String, Integer> genomeOccurence = new HashMap<>();

        //count occurences:
        for(String key: interval.getIntervalName()){
            if(genomeOccurence.containsKey(key)){
                genomeOccurence.put(key, genomeOccurence.get(key)+1);
            } else {
                genomeOccurence.put(key, 1);
            }
        }

        //TODO create hash for lengths per key

        List<String> keys = interval.getIntervalName();
        List<Double> intervalScore = interval.getIntervalScore();
        List<Double> newScores = new ArrayList<>();

        for (int i = 0; i < intervalScore.size(); i++) {
            Double p = intervalScore.get(i);
            int o = genomeOccurence.get(keys.get(i));

            if(p == null){
                newScores.add(0d);

            } else {
                p = p / o;
                newScores.add(p);
            }
        }

        interval.setIntervalScore(newScores);


        return interval;
    }

    Map<String, Double> fillOccurenceMap(List<Interval> intervals, Sites sites) {
        Map<String, Double> map = new HashMap<>(); //holds the conversion between score and probability
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
                        key += "|";

                } else if(i == intervalCount && p > intervalEnd.get(i-1)){ //last Interval and p not in previous
                    if(p < intervalEnd.get(i) && p >= intervalStart.get(i)){
                        //inside last interval
                        key += "|" + interval.getIntervalScore().get(i);

                    } else{
                        key += "|";
                    }
                }else{
                    if(p >= intervalEnd.get(i-1)){
                        key += "|"; // not inside the previous interval

                    }else{
                        key += "|" + interval.getIntervalScore().get(i-1);
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
        return map;
    }

}
