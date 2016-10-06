package de.thm.calc;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.NamedTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class implements the intersect algorithm to compare a list of positions to a set of intervals from a track.
 * The algorithm sequentially looks through the lists of start and end while iteration over the positions.
 *
 * Positions and interval lists have to be sorted prior to usage here
 */
public final class Intersect<T extends Track> implements TestTrack<T> {


    @Override
    public TestTrackResult searchTrack(T intv, Sites pos) {
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


        List<Long> intervalStart = intv.getStarts();
        List<Long> intervalEnd = intv.getEnds();
        List<String> names = intv.getIntervalName();

        ResultNames resultNames = new ResultNames();


        int intervalCount = intervalStart.size() - 1;

        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if(i == intervalCount && p >= intervalEnd.get(i)) { //not inside last interval
                out += pos.getPositions().size() - pos.getPositions().indexOf(p); //add remaining positions to out
                break; //and end the loop
            }

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


        List<Long> intervalStart = intv.getStarts();
        List<Long> intervalEnd = intv.getEnds();

        int intervalCount = intervalStart.size() - 1;


        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if(i == intervalCount && p >= intervalEnd.get(i)) { //not inside last interval
                out += pos.getPositions().size() - pos.getPositions().indexOf(p); //add remaining positions to out
                break; //and end the loop
            }

            if (p >= intervalStart.get(i)) in++;
            else out++;
        }


        return new TestTrackResult(intv, in, out);
    }

    public TestTrackResult searchSingleInterval(ScoredTrack intv, Sites pos) {

        int out = 0;
        int in = 0;
        int i = 0;

        List<Long> intervalStart = intv.getStarts();
        List<Long> intervalEnd = intv.getEnds();
        List<Double> intervalScore = intv.getIntervalScore();

        List<Double> resultsScores = new ArrayList<>();

        int intervalCount = intervalStart.size() - 1;

        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if(i == intervalCount && p >= intervalEnd.get(i)) { //not inside last interval
                out += pos.getPositions().size() - pos.getPositions().indexOf(p); //add remaining positions to out
                break; //and end the loop
            }


            if (p >= intervalStart.get(i)){
                resultsScores.add(intervalScore.get(i));
                in++;
            }
            else out++;
        }

        return new TestTrackResult(intv, in, out, resultsScores);
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

}