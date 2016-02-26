package de.thm.genomeData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Michael Menzel on 7/1/16.
 */
public class TrackTest {

    private String[] mockParts(String chr1, String s, String s1, String name) {
        String[] parts = new String[4];

        parts[0] = chr1;
        parts[1] = s;
        parts[2] = s1;
        parts[3] = name;

        return parts;
    }



    @Test
    public void testCopy() throws Exception {
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

        Track copy = base.clone();

        assertTrue(copy.equals(base));


    }
}