package de.thm.genomeData;

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
public class IntervalsTest {

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


        // sum(a,b) == -Intersect(-a, -b)
        assertEquals(Intervals.sum(interval1,interval2).getIntervalsStart(), Intervals.invert(Intervals.intersect(Intervals.invert(interval1),Intervals.invert(interval2))).getIntervalsStart());

        // sum(-a, -b) == -Intersect(a, b)
        assertEquals(Intervals.sum(Intervals.invert(interval1), Intervals.invert(interval2)).getIntervalsStart(), Intervals.invert(Intervals.intersect(interval1, interval2)).getIntervalsStart());

        // sum(a, -b) == Intersect(-a, b)
        assertEquals(Intervals.sum(interval1, Intervals.invert(interval2)).getIntervalsStart(), Intervals.intersect(Intervals.invert(interval1), interval2).getIntervalsStart());


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

        Interval interval1 = mockInterval(start1, end1);
        Interval interval2 = mockInterval(start2, end2);

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

        Interval result = Intervals.intersect(interval1, interval2);

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


        List<Interval> intervalList = new ArrayList<>();

        intervalList.add(interval1);
        intervalList.add(interval2);

        interval1.setIntervalName(names);
        interval2.setIntervalName(names);

        Interval result = Intervals.sum(intervalList);

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


        List<Interval> intervalList = new ArrayList<>();

        intervalList.add(interval1);
        intervalList.add(interval2);

        interval1.setIntervalName(names);
        interval2.setIntervalName(names);


        Interval result = Intervals.xor(interval1, interval2);

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


        interval.setType(Intervals.Type.inout);

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

        Interval interval = Intervals.subsetScore(interval1, 0.5);
        assertEquals(interval.getIntervalScore(), result);
        assertEquals(interval.getIntervalsStart().size(), 3);
        assertEquals(interval.getIntervalsEnd().size(), 3);


    }

    @Test
    public void testCombine() throws Exception {
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

        List<Double> scores1 = new ArrayList<>();
        List<Double> scores2 = new ArrayList<>();

        scores1.add(0.5);
        scores1.add(0.2);
        scores1.add(0.7);

        scores2.add(0.4);
        scores2.add(0.6);
        scores2.add(0.8);

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

        Interval result = Intervals.combine(interval1, interval2, map);

        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();
        List<Double> expectedScores = new ArrayList<>();

        expectedStarts.add(0L);
        expectedStarts.add(5L);
        expectedStarts.add(10L);
        expectedStarts.add(20L);
        expectedStarts.add(35L);
        expectedStarts.add(50L);

        expectedEnds.add(5L);
        expectedEnds.add(10L);
        expectedEnds.add(15L);
        expectedEnds.add(30L);
        expectedEnds.add(40L);
        expectedEnds.add(60L);

        expectedScores.add(5d);
        expectedScores.add(5d);
        expectedScores.add(5d);
        expectedScores.add(5d);
        expectedScores.add(5d);
        expectedScores.add(5d);


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

        Interval invert  = Intervals.invert(base);
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

        Interval doubleInvert = Intervals.invert(Intervals.invert(base));

        assertEquals(starts, doubleInvert.getIntervalsStart());
        assertEquals(ends, doubleInvert.getIntervalsEnd());
    }
}