package de.thm.calc;

import de.thm.genomeData.tracks.InOutTrack;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PositionalIntersectTest {

    private static InOutTrack iotrack;
    private static Sites sites;

    @BeforeClass
    public static void init() {

        iotrack = mock(InOutTrack.class);

        when(iotrack.getStarts()).thenReturn(new long[]{5L, 35L, 50L});
        when(iotrack.getEnds()).thenReturn(new long[]{15L, 40L, 60L});


        sites = mock(Sites.class);
        List<Long> r = Arrays.asList(1L, 4L, 5L, 6L, 14L, 44L, 52L, 61L, 191L, 200L);

        when(sites.getPositions()).thenReturn(r);
        when(sites.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(sites.getPositionCount()).thenReturn(r.size());
        when(sites.getStrands()).thenReturn(Arrays.asList('+', '-', '+', '+', '-', '+', '+', '-', '+', '+'));


    }

    @Test
    public void searchTrack() {
        PositionalIntersect positionalIntersect = new PositionalIntersect();

        TestTrackResult testTrackResult = positionalIntersect.searchTrack(iotrack, sites);

        System.out.println(testTrackResult);

        assertEquals(testTrackResult.getResultScores().get(0), 4.0, 0.0);
        //assertEquals(testTrackResult.getResultScores().subList(1,3000).stream().sum(), 4.0, 0.0);

    }
}