package de.thm.mTrackBg;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 17/2/16.
 */
public class AppearanceTable {

    private Map<String, Map<Double, Integer>> appearance;

    public void fillTable(List<Interval> intervals, Sites positions) {
        appearance = new HashMap<>();
        Map<Interval, Integer> indices = new HashMap<>();

        //init indices map:
        for(Interval interval: intervals) {
            indices.put(interval, 0);
        }


        for(Long p : positions.getPositions()){
            Set<Integer> containing = new TreeSet<>();
            double score = -1;

            for(Interval interval: intervals){

                List<Long> intervalStart = interval.getIntervalsStart();
                List<Long> intervalEnd = interval.getIntervalsEnd();
                List<Double> intervalScore = interval.getIntervalScore();

                int i = indices.get(interval);
                int intervalCount = intervalStart.size()-1;

                while(i < intervalCount && intervalStart.get(i) <= p){
                    i++;
                }

                if(i == 0){
                    if(p >= intervalStart.get(i)){
                        containing.add(interval.getUid());
                        score = intervalScore.get(i);
                    }

                } else if(i == intervalCount && p > intervalEnd.get(i-1)){ //last Interval and p not in previous
                    if(p < intervalEnd.get(i) && p >= intervalStart.get(i)){

                        containing.add(interval.getUid());
                        score = intervalScore.get(i);

                    } else{
                        continue;
                    }
                }else{
                    if(p >= intervalEnd.get(i-1)){
                        continue; // not inside the last interval

                    }else{
                        containing.add(interval.getUid());
                        score = intervalScore.get(i-1);
                    }
                }

                indices.put(interval, i);

            }

            if(appearance.containsKey(hash(containing))){

                Map<Double,Integer> scoreToCount = appearance.get(hash(containing));

                if(scoreToCount.containsKey(score))
                    scoreToCount.put(score, scoreToCount.get(score)+1);
                else{
                    scoreToCount.put(score,1);
                }

                appearance.put(hash(containing), scoreToCount);
            }else{
                Map<Double,Integer> scoreToCount = new HashMap<>();
                scoreToCount.put(score,1);
                appearance.put(hash(containing), scoreToCount);
            }
        }
    }

    /**
     * Gets the hash key for a list of Interval Ids.
     *
     * @param containing - list of interval id's
     * @return HashMap key as String
     */
    protected String hash(Set<Integer> containing) {

        List<Integer> list = new ArrayList<>(containing);
        Collections.sort(list);

        return Arrays.toString(list.toArray());
    }

    public Set<String> getKeySet() {
        //sort by length
        return appearance.keySet().stream().sorted((s, t1) -> s.length() - t1.length()).collect(Collectors.toSet());
    }

    public Map<Double, Integer> get(String app) {
        return appearance.get(app);
    }

    public double getProb(String app, double score) {
        //count of appearance of result scores / count of result possible result scores
        double a = appearance.get(app).get(score);
        double b = appearance.values().stream().map(Map::keySet).collect(Collectors.toSet()).size();

        return a/b;
    }


      protected List<Interval> translate(String app, List<Interval> knownIntervals) {

        if(app.compareTo("[]") == 0){ //empty array
            return null;
        }

        List<Interval> intervals = new ArrayList<>();

        app = app.substring(1, app.length()-1);

        int[] digits =  Arrays.stream(app.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();

        for (int id : digits) {
            for(Interval interval: knownIntervals){
                if(id == interval.getUid())
                    intervals.add(interval);
            }
        }


        return intervals;
      }

      public List<Interval> translateNegative(List<Interval> outer, String app) {

        List<Interval> intervals = new CopyOnWriteArrayList<>(outer);

        app = app.substring(1, app.length()-1);

        int[] digits =  Arrays.stream(app.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();


        //remove those which are in the given list
        for (int id : digits) {
            for(Interval interval: intervals){
                if(interval.getUid() == id)
                    intervals.remove(interval);
            }
        }

        return intervals;
      }
}
