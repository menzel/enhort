package de.thm.backgroundModel;

import de.thm.genomeData.GenomeInterval;
import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.*;

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

        start1.add(1L);
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

        List<Interval> intervals = new ArrayList<>();
        intervals.add(interval1);
        intervals.add(interval2);

        Sites sites = new Sites() {
            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {
                List<Long> l = new ArrayList<>();

                l.add(5L); //.5.4
                l.add(7L); //.5.4
                l.add(8L); //.5.4
                l.add(17L);// ||

                l.add(37L); // .6
                l.add(55L); // .7.8
                l.add(70L); // ||

                return l;
            }

            @Override
            public void setPositions(List<Long> positions) {}

            @Override
            public int getPositionCount() {
                return 7;
            }
        };


        ScoreMultiTrackBackgroundModel m =  new ScoreMultiTrackBackgroundModel();


        Map<String, Double> result_map = new HashMap<>();
        result_map.put("|0.5|0.4",3.0);
        result_map.put("||",2.0);
        result_map.put("||0.6",1.);
        result_map.put("|0.7|0.8",1.);


        assertEquals(result_map, m.fillOccurenceMap(intervals, sites));

        /* Test prob interval */

        Interval probInterval = m.generateProbabilityInterval(sites, intervals);

        assertEquals(probInterval.getIntervalScore().stream().mapToDouble(i -> i).sum(), 1.0, 0.01);

    }

    private GenomeInterval mockInterval(List<Long> start, List<Long> end) {
        GenomeInterval interval = new GenomeInterval();

        interval.setIntervalsStart(start);
        interval.setIntervalsEnd(end);


        interval.setType(Interval.Type.inout);

        return interval;
    }

}