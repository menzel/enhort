package de.thm.calc;

import de.thm.exception.TrackTypeNotAllowedExcpetion;
import de.thm.genomeData.tracks.*;
import de.thm.positionData.Sites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    private final Logger logger = LoggerFactory.getLogger(Intersect.class);

    @Override
    public TestTrackResult searchTrack(T track, Sites pos) throws TrackTypeNotAllowedExcpetion{
        if (track instanceof StrandTrack)
            return searchSingleInterval((StrandTrack) track, pos);
        if (track instanceof InOutTrack)
            return searchSingleInterval((InOutTrack) track, pos);
        if (track instanceof ScoredTrack)
            return searchSingleInterval((ScoredTrack) track, pos);
        if (track instanceof NamedTrack)
            return searchSingleInterval((NamedTrack) track, pos);
        else {
            if (logger.isDebugEnabled())
                logger.warn(track.getClass() + " not allowed in searchTrack of Intersect.java");
            throw new TrackTypeNotAllowedExcpetion("Type not allowed in intersect");
        }

    }

    TestTrackResult searchSingleInterval(StrandTrack track, Sites sites) {

        if(sites.getStrands().size() != sites.getPositions().size()) {
            if(logger.isDebugEnabled())
                logger.warn("No strand information in Sites object in searchSingleInterval in Intersect.java");

            return new TestTrackResult(track, 0, 0); //return 0s if there is no strand data in user data
        }

        int out = 0;
        int in = 0;
        int i = 0;

        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();
        char[] strands = track.getStrands();
        List<Long> positions = sites.getPositions();

        int intervalCount = intervalStart.length - 1;


        if (logger.isDebugEnabled()) {
            if (intervalStart.length != intervalEnd.length || intervalStart.length == 0) {
                logger.warn("Intersect (line 89) There is something wrong with the track: " + track.getName());
                return new TestTrackResult(track, 0, 0);
            }
        }



        for (int j = 0; j < positions.size(); j++) {
            long p = positions.get(j);

            while (i < intervalCount && intervalEnd[i] <= p)
                i++;

            if (i == intervalCount && p >= intervalEnd[i]) { //not inside last interval
                out += sites.getPositions().size() - j; //add remaining positions to out
                break; //and end the loop
            }

            if (p >= intervalStart[i] && (sites.getStrands().get(j).equals(strands[i]) || strands[i] == 'o'))
                in++;
            else out++;


            if (Thread.currentThread().isInterrupted()) return new TestTrackResult(track, in, out);
        }


        return new TestTrackResult(track, in, out);
    }



    TestTrackResult searchSingleInterval(NamedTrack track, Sites pos) {

        int out = 0;
        int in = 0;
        int i = 0;

        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();
        String[] names = track.getIntervalName();

        if (logger.isDebugEnabled()) {
            if (intervalStart.length != intervalEnd.length || intervalStart.length == 0) {
                logger.warn("Intersect (line 89) There is something wrong with the track: " + track.getName());
                return new TestTrackResult(track, 0, 0);
            }
        }


        ResultNames resultNames = new ResultNames();


        int intervalCount = intervalStart.length - 1;

        for (long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd[i] <= p)
                i++;

            if (i == intervalCount && p >= intervalEnd[i]) { //not inside last interval
                out += pos.getPositions().size() - pos.getPositions().indexOf(p); //add remaining positions to out
                break; //and end the loop
            }

            if (p >= intervalStart[i]) {

                resultNames.add(names[i]);
                in++;

            } else {
                out++;
            }

            if (Thread.currentThread().isInterrupted()) return new TestTrackResult(track, in, out);
        }


        return new TestTrackResult(track, in, out, resultNames.resultNames);
    }

    public TestTrackResult searchSingleInterval(InOutTrack track, Sites pos) {
        int out = 0;
        int in = 0;
        int i = 0;

        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();

        if (logger.isDebugEnabled()) {
            if (intervalStart.length != intervalEnd.length || intervalStart.length == 0) {
                logger.warn("Intersect (line 129) There is something wrong with the track: " + track.getName());
                return new TestTrackResult(track, 0, 0);
            }
        }

        int intervalCount = intervalStart.length - 1;


        for (long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd[i] <= p)
                i++;

            if (i == intervalCount && p >= intervalEnd[i]) { //not inside last interval
                out += pos.getPositions().size() - pos.getPositions().indexOf(p); //add remaining positions to out
                break; //and end the loop
            }

            if (p >= intervalStart[i]) in++;
            else out++;

            if (Thread.currentThread().isInterrupted()) return new TestTrackResult(track, in, out);
        }


        return new TestTrackResult(track, in, out);
    }

    TestTrackResult searchSingleInterval(ScoredTrack track, Sites pos) {

        int out = 0;
        int in = 0;
        int i = 0;

        long[] intervalStart = track.getStarts();
        long[] intervalEnd = track.getEnds();
        double[] intervalScore = track.getIntervalScore();

        List<Double> resultsScores = new ArrayList<>();


        if (logger.isDebugEnabled()) {
            if (intervalStart.length != intervalEnd.length || intervalStart.length == 0) {
                logger.warn("Intersect (line 180) There is something wrong with the track: " + track.getName());
                return new TestTrackResult(track, 0, 0);
            }
        }


        int intervalCount = intervalStart.length - 1;

        for (long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd[i] <= p)
                i++;

            if (i == intervalCount && p >= intervalEnd[i]) { //not inside last interval
                out += pos.getPositions().size() - pos.getPositions().indexOf(p); //add remaining positions to out
                break; //and end the loop
            }


            if (p >= intervalStart[i]) {
                resultsScores.add(intervalScore[i]);
                in++;
            }
            else out++;

            if (Thread.currentThread().isInterrupted()) return new TestTrackResult(track, in, out);
        }

        return new TestTrackResult(track, in, out, resultsScores);
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
            StringBuilder result = new StringBuilder();

            for(String key: resultNames.keySet()){

                for(int i = 0; i < resultNames.get(key) ; i++)
                    result.append(key).append(",");

            }
            return result.toString();
        }


    }

}