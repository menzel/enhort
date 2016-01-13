package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 15/12/15.
 */
public class IntersectSimpleTest {

    Interval intv;
    Intersect intersect = new IntersectCalculate();

    @Before
    public void setupIntv() throws Exception {
        intv = new Interval();
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();
        long offset = chrSizes.offset("chr4");

        ArrayList<Long> startList = new ArrayList<>();
        ArrayList<Long> endList = new ArrayList<>();
        ArrayList<String> namesList = new ArrayList<>();

        startList.add(5L);
        startList.add(10L);
        startList.add(20L);

        endList.add(7L);
        endList.add(15L);
        endList.add(22L);

        startList.add(5L + offset);
        startList.add(10L + offset);
        startList.add(20L + offset);

        endList.add(7L + offset);
        endList.add(15L + offset);
        endList.add(22L + offset);


        namesList.add("first");
        namesList.add("second");
        namesList.add("third");

        namesList.add("first");
        namesList.add("second");
        namesList.add("third");

        intv.setIntervalsStart(startList);
        intv.setIntervalsEnd(endList);
        intv.setIntervalName(namesList);
    }


    @Test
    public void testLastInterval() throws Exception {

        Sites sites =  new Sites() {
            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                ChromosomSizes chrSizes = ChromosomSizes.getInstance();
                long offset = chrSizes.offset("chr4");

                sites.add(1L);
                sites.add(4L);
                sites.add(5L);
                sites.add(6L);
                sites.add(1L + offset);
                sites.add(4L + offset);
                sites.add(5L + offset);
                sites.add(6L + offset);
                sites.add(19 + offset);
                sites.add(20 + offset);


                return sites;

            }
        };

        IntersectResult intersectResult = intersect.searchSingleInterval(intv,sites);
        assertEquals(5, intersectResult.getIn());

        assertEquals(5, intersectResult.getOut().intValue());
    }


    @Test
    public void testOffsetSearch() throws Exception {

        Sites sites =  new Sites() {
            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                ChromosomSizes chrSizes = ChromosomSizes.getInstance();
                long offset = chrSizes.offset("chr4");

                sites.add(1L);
                sites.add(4L);
                sites.add(5L);
                sites.add(6L);
                sites.add(1L + offset);
                sites.add(4L + offset);
                sites.add(5L + offset);
                sites.add(6L + offset);


                return sites;

            }
        };

        IntersectResult intersectResult = intersect.searchSingleInterval(intv,sites);
        assertEquals(4, intersectResult.getIn());

        assertEquals(4, intersectResult.getOut().intValue());
    }


    @Test
    public void testSearchSingleIntervall() throws Exception {

        Sites sites =  new Sites() {
            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                sites.add(1L); // out
                sites.add(4L); // out

                sites.add(5L);
                sites.add(6L);

                sites.add(8L); // out

                sites.add(21L);
                sites.add(22L); // out
                sites.add(22L); // out

                sites.add(23L); // out
                sites.add(24L); // out
                sites.add(24L); // out
                sites.add(26L); // out
                sites.add(128L); // out

                return sites;

            }
        };

        IntersectResult intersectResult = intersect.searchSingleInterval(intv,sites);

        assertEquals(10, intersectResult.getOut().intValue());

        assertEquals(3, intersectResult.getIn());

    }
}