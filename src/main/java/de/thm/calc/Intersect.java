package de.thm.calc;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.*;
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
        if (intv instanceof StrandTrack)
            return searchSingleInterval((StrandTrack) intv, pos);
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

    TestTrackResult searchSingleInterval(StrandTrack track, Sites sites) {

        if(sites.getStrands().size() != sites.getPositions().size())
            return new TestTrackResult(track, 0,0); //return 0s if there is no strand data in user data

        int out = 0;
        int in = 0;
        int i = 0;

        List<Long> intervalStart = track.getStarts();
        List<Long> intervalEnd = track.getEnds();
        List<Character> strands = track.getStrands();
        List<Long> positions = sites.getPositions();

        int intervalCount = intervalStart.size() - 1;

        for (int j = 0; j < positions.size(); j++) {
            long p = positions.get(j);

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if (i == intervalCount && p >= intervalEnd.get(i)) { //not inside last interval
                out += sites.getPositions().size() - j; //add remaining positions to out
                break; //and end the loop
            }

            if (p >= intervalStart.get(i)  && sites.getStrands().get(j).equals(strands.get(i))) in++;
            else out++;
        }


        return new TestTrackResult(track, in, out);
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

        for (long p : pos.getPositions()) {

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


        for (long p : pos.getPositions()) {

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

        for (long p : pos.getPositions()) {

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

        private final Map<String, Integer> resultNames = new HashMap<>();

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