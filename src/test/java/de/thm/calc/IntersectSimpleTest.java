package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalNamed;
import de.thm.positionData.Sites;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 15/12/15.
 */
public class IntersectSimpleTest {

    Interval intv;
    Intersect intersect = new IntersectSimple();

    @Before
    public void setupIntv() throws Exception {
        intv = new IntervalNamed();
        Map<String, ArrayList<Long>> starts = new HashMap<>();
        Map<String, ArrayList<Long>> ends = new HashMap<>();
        Map<String, ArrayList<String>> names = new HashMap<>();

        ArrayList<Long> startList = new ArrayList<>();
        ArrayList<Long> endList = new ArrayList<>();
        ArrayList<String> namesList = new ArrayList<>();

        startList.add(5L);
        startList.add(10L);
        startList.add(20L);

        endList.add(7L);
        endList.add(15L);
        endList.add(22L);

        namesList.add("first");
        namesList.add("second");
        namesList.add("third");

        starts.put("chr1", startList);
        ends.put("chr1", endList );
        names.put("chr1", namesList);

        intv.setIntervalsStart(starts);
        intv.setIntervalsEnd(ends);
        intv.setIntervalName(names);
    }


    @Test
    public void testSearchSingleIntervall() throws Exception {

        Sites sites =  new Sites() {
            @Override
            public Map<String, ArrayList<Long>> getPositions() {

                Map<String, ArrayList<Long>> sites = new HashMap<>();
                sites.put("chr1", new ArrayList<>());

                sites.get("chr1").add(1L);
                sites.get("chr1").add(4L);
                sites.get("chr1").add(5L);
                sites.get("chr1").add(6L);

                sites.get("chr1").add(8L);

                sites.get("chr1").add(21L);
                sites.get("chr1").add(22L);
                sites.get("chr1").add(22L);
                sites.get("chr1").add(23L);
                sites.get("chr1").add(24L);

                return sites;

            }
        };

        Result result = intersect.searchSingleIntervall(intv,sites);
        assertEquals(5, result.getIn());

        assertEquals(5, result.getOut().intValue());
    }
}