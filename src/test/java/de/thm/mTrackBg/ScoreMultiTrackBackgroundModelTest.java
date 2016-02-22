package de.thm.mTrackBg;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * Created by Michael Menzel on 19/2/16.
 */
public class ScoreMultiTrackBackgroundModelTest {

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

        Interval interval1 = mockInterval(start1, end1);
        Interval interval2 = mockInterval(start2, end2);

        interval1.setIntervalScore(scores1);
        interval2.setIntervalScore(scores2);

        List<Interval> intervals = new ArrayList<>();
        intervals.add(interval1);
        intervals.add(interval2);

        Sites sites = new Sites() {
            @Override
            public List<Long> getPositions() {
                List<Long> l = new ArrayList<>();

                l.add(5L); //.5.4
                l.add(7L); //.5.4
                l.add(8L); //.5.4
                l.add(17L);// ||

                l.add(37L); // .6
                l.add(55L); // .7.8

                return l;
            }

            @Override
            public int getPositionCount() {
                return 6;
            }
        };


        ScoreMultiTrackBackgroundModel m =  new ScoreMultiTrackBackgroundModel();


        Map<String, Double> result_map = new HashMap<>();
        result_map.put("|0.5|0.4",3.0);
        result_map.put("||",1.0);
        result_map.put("||0.6",1.);
        result_map.put("|0.7|0.8",1.);


        assertEquals(result_map, m.fillOccurenceMap(intervals, sites));



    }

    private Interval mockInterval(List<Long> start, List<Long> end) {
        Interval interval = new Interval();

        interval.setIntervalsStart(start);
        interval.setIntervalsEnd(end);


        interval.setType(Interval.Type.inout);

        return interval;
    }

}