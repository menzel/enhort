package de.thm.genomeData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 14/1/16.
 */
public class IntervalIntersectTest {
        @Test
    public void testIntersectRecursion() throws Exception {


        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();
        List<Long> start3 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();
        List<Long> end3 = new ArrayList<>();

        start1.add(0L);
        start1.add(13L);
        start1.add(20L);
        start1.add(42L);
        start1.add(65L);

        end1.add(12L);
        end1.add(16L);
        end1.add(25L);
        end1.add(44L);
        end1.add(70L);

        start2.add(3L);
        start2.add(7L);
        start2.add(14L);
        start2.add(28L);
        start2.add(40L);
        start2.add(70L);

        end2.add(5L);
        end2.add(9L);
        end2.add(18L);
        end2.add(32L);
        end2.add(50L);
        end2.add(80L);

        start3.add(3L);
        start3.add(40L);

        end3.add(6L);
        end3.add(45L);



        Interval interval1 = mockInterval(start1, end1);
        Interval interval2 = mockInterval(start2, end2);
        Interval interval3 = mockInterval(start3, end3);

        List<Interval> intervalList = new ArrayList<>();

        intervalList.add(interval2);
        intervalList.add(interval1);
        intervalList.add(interval3);

        Interval newInterval = IntervalIntersect.intersect(intervalList);

        List<Long> expected_start = new ArrayList<>();
        expected_start.add(3L);
        expected_start.add(42L);

        List<Long> expected_end = new ArrayList<>();
        expected_end.add(5L);
        expected_end.add(44L);

        assertEquals(expected_start, newInterval.getIntervalsStart());
        assertEquals(expected_end, newInterval.getIntervalsEnd());

    }


    @Test
    public void testIntersect() throws Exception {


        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(13L);
        start1.add(20L);
        start1.add(42L);
        start1.add(65L);

        end1.add(12L);
        end1.add(16L);
        end1.add(25L);
        end1.add(44L);
        end1.add(70L);

        start2.add(3L);
        start2.add(7L);
        start2.add(14L);
        start2.add(28L);
        start2.add(40L);
        start2.add(70L);

        end2.add(5L);
        end2.add(9L);
        end2.add(18L);
        end2.add(32L);
        end2.add(50L);
        end2.add(80L);


        Interval interval1 = mockInterval(start1, end1);
        Interval interval2 = mockInterval(start2, end2);

        Interval newInterval = IntervalIntersect.intersect(interval1,interval2);

        /**
        for(int i = 0; i < newInterval.getIntervalsStart().size(); i++){
            System.out.print(newInterval.getIntervalsStart().get(i) + " - ");
            System.out.println(newInterval.getIntervalsEnd().get(i));
        }
         **/

        List<Long> expected_start = new ArrayList<>();
        expected_start.add(3L);
        expected_start.add(7L);
        expected_start.add(14L);
        expected_start.add(42L);

        List<Long> expected_end = new ArrayList<>();
        expected_end.add(5L);
        expected_end.add(9L);
        expected_end.add(16L);
        expected_end.add(44L);

        assertEquals(expected_start, newInterval.getIntervalsStart());
        assertEquals(expected_end, newInterval.getIntervalsEnd());

    }



    private Interval mockInterval(List<Long> start, List<Long> end) {
        Interval interval = new Interval();

        interval.setIntervalsStart(start);
        interval.setIntervalsEnd(end);

        return interval;
    }
}