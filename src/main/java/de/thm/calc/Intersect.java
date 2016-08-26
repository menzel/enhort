package de.thm.calc;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.NamedTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import de.thm.positionData.Sites;

import java.util.*;

/**
 * The class implements the intersect algorithm to compare a list of positions to a set of intervals from a track.
 * The algorithm sequentially looks through the lists of start and end while iteration over the positions.
 *
 * Positions and interval lists have to be sorted prior to usage here
 */
public final class Intersect<T extends Track> implements TestTrack<T> {


    @Override
    public TestTrackResult searchSingleInterval(T intv, Sites pos) {
        if (intv instanceof InOutTrack)
            return searchSingleInterval((InOutTrack) intv, pos);
        if (intv instanceof ScoredTrack)
            return searchSingleInterval((ScoredTrack) intv, pos);
        if (intv instanceof NamedTrack)
            return searchSingleInterval((NamedTrack) intv, pos);
        else
            try {
                throw new IntervalTypeNotAllowedExcpetion("Type not allowed in intersect");
            } catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
                intervalTypeNotAllowedExcpetion.printStackTrace();
                return null;
            }

    }

    private TestTrackResult searchSingleInterval(NamedTrack intv, Sites pos) {

        int out = 0;
        int in = 0;
        int i = 0;


        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();
        List<String> names = intv.getIntervalName();

        ResultNames resultNames = new ResultNames();


        int intervalCount = intervalStart.size() - 1;

        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if (p >= intervalStart.get(i)) {

                resultNames.add(names.get(i));
                in++;

            } else {
                out++;
            }
        }


        return new TestTrackResult(intv, in, out, resultNames.resultNames);
    }

    public TestTrackResult searchSingleInterval(InOutTrack intv, Sites pos) {
        int out = 0;
        int in = 0;
        int i = 0;


        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();

        int intervalCount = intervalStart.size() - 1;


        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if (p >= intervalStart.get(i)) in++;
            else out++;
        }


        return new TestTrackResult(intv, in, out);
    }

    public TestTrackResult searchSingleInterval(ScoredTrack intv, Sites pos) {

        int out = 0;
        int in = 0;
        int i = 0;

        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();
        List<Double> intervalScore = intv.getIntervalScore();

        List<Double> resultsScores = new ArrayList<>();

        int intervalCount = intervalStart.size() - 1;

        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if (p >= intervalStart.get(i)){
                resultsScores.add(intervalScore.get(i));
                in++;
            }
            else out++;
        }

        return new TestTrackResult(intv, in, out, resultsScores);
    }

    /**
     * Counts distances between positions
     *
     * @param intv
     * @param pos
     * @return
     * @deprecated
     */
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
     *  Inner class to keep results names hash
     */
    private class ResultNames {

        private Map<String, Integer> resultNames = new HashMap<>();

        /**
         * Adds 1 to the given result name
         *
         * @param name - result name
         */
        void add(String name) {

            if (resultNames.containsKey(name)) {
                resultNames.put(name, resultNames.get(name) + 1);
            } else {
                resultNames.put(name, 1);
            }

        }

        @Override
        public String toString(){
            String result = "";

            for(String key: resultNames.keySet()){

                for(int i = 0; i < resultNames.get(key) ; i++)
                    result += key + ",";

            }
            return result;
        }


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