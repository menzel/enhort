package de.thm.genomeData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

        Interval interval1 = mockInterval(start1, end1);
        Interval interval2 = mockInterval(start2, end2);

        interval1.setType(Interval.Type.inout);
        interval2.setType(Interval.Type.inout);

        interval1.setIntervalName(names);
        interval2.setIntervalName(names);

        // sum(a, -b) == intersect(-a, b)

        assertEquals(Intervals.sum(interval1, interval2.invert().invert()).getIntervalsStart(), Intervals.intersect(interval1.invert(), interval2).getIntervalsStart());


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

        Interval interval1 = mockInterval(start1, end1);
        Interval interval2 = mockInterval(start2, end2);

        interval1.setType(Interval.Type.inout);
        interval2.setType(Interval.Type.inout);

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

    private Interval mockInterval(List<Long> start, List<Long> end) {
        Interval interval = new Interval();

        interval.setIntervalsStart(start);
        interval.setIntervalsEnd(end);

        return interval;
    }
}