package de.thm.genomeData;

import de.thm.misc.ChromosomSizes;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 7/1/16.
 */
public class IntervalTest {

    @Test
    public void testHandleParts() throws Exception {
        Interval intv = new Interval();
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        intv.handleParts(mockParts("chr1", "10", "20", "name"));

        assertEquals(10 + chrSizes.offset("chr1"), intv.getIntervalsStart().get(0).longValue());
        assertEquals(20 + chrSizes.offset("chr1"), intv.getIntervalsEnd().get(0).longValue());

    }

    private String[] mockParts(String chr1, String s, String s1, String name) {
        String[] parts = new String[4];

        parts[0] = chr1;
        parts[1] = s;
        parts[2] = s1;
        parts[3] = name;

        return parts;
    }
}