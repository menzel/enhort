package de.thm.backgroundModel;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class AdvancedBackgroundModelTest {

    @Test
    public void testAdvancedBg() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();
        List<Long> start3 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();
        List<Long> end3 = new ArrayList<>();

        start1.add(0L);
        start1.add(10L);
        start1.add(30L);
        start1.add(50L);

        end1.add(5L);
        end1.add(15L);
        end1.add(35L);
        end1.add(55L);

        start2.add(0L);
        start2.add(20L);
        start2.add(40L);

        end2.add(5L);
        end2.add(25L);
        end2.add(55L);

        start3.add(0L);
        start3.add(15L);
        start3.add(40L);
        start3.add(50L);

        end3.add(10L);
        end3.add(20L);
        end3.add(45L);
        end3.add(55L);

        Interval interval1 = mockInterval(start1, end1);
        Interval interval2 = mockInterval(start2, end2);
        Interval interval3 = mockInterval(start3, end3);

        List<Interval> intervalList = new ArrayList<>();

        intervalList.add(interval1);
        intervalList.add(interval2);
        intervalList.add(interval3);

        // positions:

         Sites sites =  new Sites() {
            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                sites.add(12L);
                sites.add(42L);
                sites.add(46L);
                sites.add(53L);
                sites.add(54L);

                return sites;

            }
        };



        AdvancedBackgroundModel model = new AdvancedBackgroundModel(intervalList, sites);

    }

    private Interval mockInterval(List<Long> start, List<Long> end) {
        Interval interval = new Interval();

        interval.setIntervalsStart(start);
        interval.setIntervalsEnd(end);

        return interval;
    }
}