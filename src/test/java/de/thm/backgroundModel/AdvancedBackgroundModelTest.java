package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectCalculate;
import de.thm.calc.IntersectResult;
import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class AdvancedBackgroundModelTest {

    //@Test
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

        //check list count of pos in list 1:
        assertEquals(1,model.getAppearanceTable().getAppearance(intervalList.subList(0,1)));

        //check list 2:
        assertEquals(1,model.getAppearanceTable().getAppearance(intervalList.subList(1,2)));


        //check count of pos which are in all lists:
        assertEquals(2,model.getAppearanceTable().getAppearance(intervalList));

        // check pos in list 2 and 3:
        assertEquals(1,model.getAppearanceTable().getAppearance(intervalList.subList(1,3)));

        //check 0 values for other lists:
        assertEquals(0,model.getAppearanceTable().getAppearance(intervalList.subList(2,3))); //list 3

        List<Interval> zeroList = new ArrayList<>();
        zeroList.add(interval1);
        zeroList.add(interval3);

        assertEquals(0,model.getAppearanceTable().getAppearance(zeroList));

    }

    private Interval mockInterval(List<Long> start, List<Long> end) {
        Interval interval = new Interval();

        interval.setIntervalsStart(start);
        interval.setIntervalsEnd(end);

        return interval;
    }

    @Test
    public void testRandPositions() throws Exception {

        // intervals:

        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(45L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(80L);

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


        // appearance table
        AppearanceTable app = new AppearanceTable();
        Map<String, Integer> appearance_map = new HashMap<>();
        Set<Integer> containing = new TreeSet<>();

        int inFirst = 123;
        int inSecond = 21;
        int inBoth = 110;
        int out = 78;

        containing.add(interval1.getUid());
        appearance_map.put(app.hash(containing), inFirst);

        containing.add(interval2.getUid());
        appearance_map.put(app.hash(containing), inBoth);

        containing.clear();

        containing.add(interval2.getUid());
        appearance_map.put(app.hash(containing), inSecond);

        appearance_map.put("[]", out);

        app.setAppearance(appearance_map);

        AdvancedBackgroundModel bg = new AdvancedBackgroundModel();

        bg.addPositions(bg.randPositions(app, intervalList));

        Intersect calc = new IntersectCalculate();

        //check in first:
        IntersectResult result1 = calc.searchSingleInterval(interval1, bg);
        assertEquals(inFirst + inBoth, result1.getIn());


        //check in second:
        IntersectResult result2 = calc.searchSingleInterval(interval2, bg);
        assertEquals(inSecond + inBoth, result2.getIn());

        //check outsiders first:
        assertEquals(new Integer(inSecond + out), result1.getOut());

        //check outsiders second:
        assertEquals(new Integer(inFirst + out), result2.getOut());

    }
}