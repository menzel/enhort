package de.thm.genomeData;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.misc.ChromosomSizes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 19/1/16.
 */
public class TracksTest {

    @Test
    public void crossvalidationSumIntersect() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(45L);
        start1.add(100L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(80L);
        end1.add(110L);

        end2.add(15L);
        end2.add(40L);
        end2.add(55L);

        List<String> names = new ArrayList<>();

        names.add("foo");
        names.add("foo");
        names.add("foo");

        GenomeInterval interval1 = mockInterval(start1, end1);
        GenomeInterval interval2 = mockInterval(start2, end2);


        interval1.setIntervalName(names);
        interval2.setIntervalName(names);


        // sum(a,b) == -TestTrack(-a, -b)
        try {
            assertEquals(Tracks.sum(interval1,interval2).getIntervalsStart(), Tracks.invert(Tracks.intersect(Tracks.invert(interval1), Tracks.invert(interval2))).getIntervalsStart());

            // sum(-a, -b) == -TestTrack(a, b)
            assertEquals(Tracks.sum(Tracks.invert(interval1), Tracks.invert(interval2)).getIntervalsStart(), Tracks.invert(Tracks.intersect(interval1, interval2)).getIntervalsStart());

            // sum(a, -b) == TestTrack(-a, b)
            assertEquals(Tracks.sum(interval1, Tracks.invert(interval2)).getIntervalsStart(), Tracks.intersect(Tracks.invert(interval1), interval2).getIntervalsStart());

        }  catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
            intervalTypeNotAllowedExcpetion.printStackTrace();
        }

    }

    @Test
    public void testIntersect() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(10L);
        start1.add(30L);
        start1.add(80L);
        start1.add(110L);

        end1.add(20L);
        end1.add(45L);
        end1.add(100L);
        end1.add(3095677412L); // chromosome end


        start2.add(0L);
        start2.add(15L);
        start2.add(40L);
        start2.add(55L);

        end2.add(5L);
        end2.add(35L);
        end2.add(50L);
        end2.add(3095677412L); // chromosome end

        Track track1 = mockInterval(start1, end1);
        Track track2 = mockInterval(start2, end2);

        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();

        expectedStarts.add(15L);
        expectedStarts.add(30L);
        expectedStarts.add(40L);
        expectedStarts.add(80L);
        expectedStarts.add(110L);

        expectedEnds.add(20L);
        expectedEnds.add(35L);
        expectedEnds.add(45L);
        expectedEnds.add(100L);
        expectedEnds.add(3095677412L); // chromosome end

        Track result = Tracks.intersect(track1, track2);

        assertEquals(expectedStarts, result.getIntervalsStart());
        assertEquals(expectedEnds, result.getIntervalsEnd());

    }


    @Test
    public void testSum() throws Exception {

        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(45L);
        start1.add(100L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(80L);
        end1.add(110L);

        end2.add(15L);
        end2.add(40L);
        end2.add(55L);

        List<String> names = new ArrayList<>();

        names.add("foo");
        names.add("foo");
        names.add("foo");

        GenomeInterval interval1 = mockInterval(start1, end1);
        GenomeInterval interval2 = mockInterval(start2, end2);


        List<Track> trackList = new ArrayList<>();

        trackList.add(interval1);
        trackList.add(interval2);

        interval1.setIntervalName(names);
        interval2.setIntervalName(names);

        Track result = null;
        try {
            result = Tracks.sum(trackList);
        } catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
            intervalTypeNotAllowedExcpetion.printStackTrace();
        }

        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();

        expectedStarts.add(0L);
        expectedStarts.add(20L);
        expectedStarts.add(35L);
        expectedStarts.add(45L);
        expectedStarts.add(100L);

        expectedEnds.add(15L);
        expectedEnds.add(30L);
        expectedEnds.add(40L);
        expectedEnds.add(80L);
        expectedEnds.add(110L);

        assertEquals(expectedStarts, result.getIntervalsStart());
        assertEquals(expectedEnds, result.getIntervalsEnd());


    }

    @Test
    public void testXor() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(60L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end2.add(15L);
        end2.add(40L);
        end2.add(60L);

        List<String> names = new ArrayList<>();

        names.add("foo");
        names.add("foo");
        names.add("foo");

        GenomeInterval interval1 = mockInterval(start1, end1);
        GenomeInterval interval2 = mockInterval(start2, end2);


        List<Track> trackList = new ArrayList<>();

        trackList.add(interval1);
        trackList.add(interval2);

        interval1.setIntervalName(names);
        interval2.setIntervalName(names);


        Track result = Tracks.xor(interval1, interval2);

        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();

        expectedStarts.add(0L);
        expectedStarts.add(10L);
        expectedStarts.add(20L);
        expectedStarts.add(35L);

        expectedEnds.add(5L);
        expectedEnds.add(15L);
        expectedEnds.add(30L);
        expectedEnds.add(40L);

        assertEquals(expectedStarts, result.getIntervalsStart());
        assertEquals(expectedEnds, result.getIntervalsEnd());

    }

    private GenomeInterval mockInterval(List<Long> start, List<Long> end) {
        GenomeInterval interval = new GenomeInterval();

        interval.setIntervalsStart(start);
        interval.setIntervalsEnd(end);


        interval.setType(Track.Type.inout);

        return interval;
    }


    @Test
    public void testSubsetScore() throws Exception {
        List<Long> start1 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(50L);
        start1.add(80L);
        start1.add(100L);

        end1.add(10L);
        end1.add(30L);
        end1.add(60L);
        end1.add(90L);
        end1.add(110L);


        List<Double> scores = new ArrayList<>();

        scores.add(0.5);
        scores.add(0.5);
        scores.add(0.3);
        scores.add(0.2);
        scores.add(0.5);

        GenomeInterval interval1 = mockInterval(start1, end1);

        interval1.setIntervalScore(scores);

        List<Double> result = new ArrayList<>();

        result.add(0.5);
        result.add(0.5);
        result.add(0.5);

        Track track = Tracks.subsetScore(interval1, 0.5);
        assertEquals(track.getIntervalScore(), result);
        assertEquals(track.getIntervalsStart().size(), 3);
        assertEquals(track.getIntervalsEnd().size(), 3);


    }

    @Test
    public void testCombine() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(1L);
        start1.add(20L);
        start1.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(60L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);
        start2.add(100L);

        end2.add(15L);
        end2.add(40L);
        end2.add(60L);
        end2.add(110L);

        List<Double> scores1 = new ArrayList<>();
        List<Double> scores2 = new ArrayList<>();

        scores1.add(0.5);
        scores1.add(0.2);
        scores1.add(0.7);

        scores2.add(0.4);
        scores2.add(0.6);
        scores2.add(0.8);

        scores2.add(0.11);

        GenomeInterval interval1 = mockInterval(start1, end1);
        GenomeInterval interval2 = mockInterval(start2, end2);

        interval1.setIntervalScore(scores1);
        interval2.setIntervalScore(scores2);

        Map<String, Double> map = new HashMap<>();
        map.put("|0.5|", 5d);
        map.put("|0.2|", 5d);
        map.put("|0.7|", 5d);

        map.put("||0.4", 5d);
        map.put("||0.6", 5d);
        map.put("||0.8", 5d);

        map.put("|0.5|0.4", 5d);
        map.put("|0.7|0.8", 5d);

        map.put("||", 1d);
        map.put("||0.11", 2d);


        Track result = Tracks.combine(interval1, interval2, map);

        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();
        List<Double> expectedScores = new ArrayList<>();

        expectedStarts.add(0L);
        expectedStarts.add(1L);
        expectedStarts.add(5L);
        expectedStarts.add(10L);
        expectedStarts.add(15L);
        expectedStarts.add(20L);
        expectedStarts.add(30L);
        expectedStarts.add(35L);
        expectedStarts.add(40L);
        expectedStarts.add(50L);
        expectedStarts.add(60L);
        expectedStarts.add(100L);
        expectedStarts.add(110L);

        expectedEnds.add(1L);
        expectedEnds.add(5L);
        expectedEnds.add(10L);
        expectedEnds.add(15L);
        expectedEnds.add(20L);
        expectedEnds.add(30L);
        expectedEnds.add(35L);
        expectedEnds.add(40L);
        expectedEnds.add(50L);
        expectedEnds.add(60L);
        expectedEnds.add(100L);
        expectedEnds.add(110L);
        expectedEnds.add(ChromosomSizes.getInstance().getGenomeSize());

        expectedScores.add(1d);
        expectedScores.add(5d);
        expectedScores.add(5d);
        expectedScores.add(5d);
        expectedScores.add(1d);
        expectedScores.add(5d);
        expectedScores.add(1d);
        expectedScores.add(5d);
        expectedScores.add(1d);
        expectedScores.add(5d);
        expectedScores.add(1d);
        expectedScores.add(2d);
        expectedScores.add(1d);

        assertEquals(expectedStarts, result.getIntervalsStart());
        assertEquals(expectedEnds, result.getIntervalsEnd());
        assertEquals(expectedScores, result.getIntervalScore());

    }

     @Test
    public void testInvert() throws Exception {
        GenomeInterval base = new GenomeInterval();

        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();

        starts.add(5L);
        starts.add(15L);
        starts.add(25L);

        ends.add(10L);
        ends.add(20L);
        ends.add(26L);

        base.setIntervalsStart(starts);
        base.setIntervalsEnd(ends);

        Track invert  = Tracks.invert(base);
        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();

        expectedStarts.add(0L);
        expectedStarts.add(10L);
        expectedStarts.add(20L);
        expectedStarts.add(26L);

        expectedEnds.add(5L);
        expectedEnds.add(15L);
        expectedEnds.add(25L);
        expectedEnds.add(ChromosomSizes.getInstance().getGenomeSize());

        assertEquals(expectedStarts, invert.getIntervalsStart());
        assertEquals(expectedEnds, invert.getIntervalsEnd());

        assertEquals(starts, base.getIntervalsStart());
        assertEquals(ends, base.getIntervalsEnd());

        Track doubleInvert = Tracks.invert(Tracks.invert(base));

        assertEquals(starts, doubleInvert.getIntervalsStart());
        assertEquals(ends, doubleInvert.getIntervalsEnd());
    }

    @Test
    public void testConvertToScore() throws Exception {
         GenomeInterval base = new GenomeInterval();

        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();

        starts.add(5L);
        starts.add(15L);
        starts.add(25L);

        ends.add(10L);
        ends.add(20L);
        ends.add(26L);

        base.setIntervalsStart(starts);
        base.setIntervalsEnd(ends);

        List<Double> expectedScores = new ArrayList<>();


        expectedScores.add(1d);
        expectedScores.add(1d);
        expectedScores.add(1d);

        Track result = Tracks.cast(base);

        assertEquals(expectedScores, result.getIntervalScore());

    }

    @Test
    public void testCast() throws Exception {
        //TOOD

    }
}