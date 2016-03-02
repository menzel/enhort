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
 * Simple version of intersect, going list by list.
 */
public final class IntersectCalculate<T extends Track> implements Intersect<T> {


    @Override
    public IntersectResult searchSingleInterval(T intv, Sites pos) {
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

    private IntersectResult searchSingleInterval(NamedTrack intv, Sites pos) {

        int out = 0;
        int in = 0;
        int i = 0;


        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();
        List<String> names = intv.getIntervalName();

        ResultNames resultNames = new ResultNames();


        int intervalCount = intervalStart.size() - 1;


        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalStart.get(i) <= p) {
                i++;
            }

            if (i == 0) {
                out++;

            } else if (i == intervalCount && p > intervalEnd.get(i - 1)) { //last Interval and p not in previous
                if (p < intervalEnd.get(i) && p >= intervalStart.get(i)) {

                    in++;
                    resultNames.add(names.get(i - 1));

                } else {
                    out++;
                }
            } else {
                if (p >= intervalEnd.get(i - 1)) {
                    out++;

                } else {
                    in++;
                    resultNames.add(names.get(i - 1));
                }
            }
        }

        return new IntersectResult(intv, in, out, resultNames.resultNames);
    }

    public IntersectResult searchSingleInterval(InOutTrack intv, Sites pos) {
        int out = 0;
        int in = 0;
        int i = 0;


        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();

        int intervalCount = intervalStart.size() - 1;


        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalStart.get(i) <= p) {
                i++;
            }

            if (i == 0) {
                out++;

            } else if (i == intervalCount && p > intervalEnd.get(i - 1)) { //last Interval and p not in previous
                if (p < intervalEnd.get(i) && p >= intervalStart.get(i)) {

                    in++;

                } else {
                    out++;
                }
            } else {
                if (p >= intervalEnd.get(i - 1)) {
                    out++;

                } else {
                    in++;
                }
            }
        }


        return new IntersectResult(intv, in, out);
    }

    public IntersectResult searchSingleInterval(ScoredTrack intv, Sites pos) {

        int out = 0;
        int in = 0;
        int i = 0;



        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();
        List<Double> intervalScore = intv.getIntervalScore();

        List<Double> resultsScores = new ArrayList<>();

        int intervalCount = intervalStart.size() - 1;


        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalStart.get(i) <= p) {
                i++;
            }

            if (i == 0) {
                out++;

            } else if (i == intervalCount && p > intervalEnd.get(i - 1)) { //last Interval and p not in previous
                if (p < intervalEnd.get(i) && p >= intervalStart.get(i)) {

                    in++;
                    resultsScores.add(intervalScore.get(i - 1));

                } else {
                    out++;
                }
            } else {
                if (p >= intervalEnd.get(i - 1)) {
                    out++;

                } else {
                    in++;
                    resultsScores.add(intervalScore.get(i - 1));
                }
            }
        }

        return new IntersectResult(intv, in, out, resultsScores);
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
    }

}