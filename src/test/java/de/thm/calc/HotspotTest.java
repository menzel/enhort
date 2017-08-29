package de.thm.calc;

import de.thm.genomeData.tracks.ScoredTrack;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by menzel on 2/21/17.
 */
public class HotspotTest {
    @Test
    public void findHotspots() throws Exception {
        Sites sites = mock(Sites.class);
        List<Long> r = Arrays.asList(5L, 7L, 8L, 37L, 70L, 71L, 73L, 74L, 1001L);

        when(sites.getPositions()).thenReturn(r);
        when(sites.getAssembly()).thenReturn(GenomeFactory.Assembly.hg19);
        when(sites.getPositionCount()).thenReturn(5);

        Hotspot hotspot = new Hotspot();

        ScoredTrack track = hotspot.findHotspots(sites, 1000);
        System.out.println(Arrays.toString(track.getIntervalScore()));

        assertArrayEquals(new double[]{
                8.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
                }, track.getIntervalScore(),0.0);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testTooSmallWindowSize() throws Exception {
        Sites sites = mock(Sites.class);
        when(sites.getAssembly()).thenReturn(GenomeFactory.Assembly.hg19);

        Hotspot hotspot = new Hotspot();
        hotspot.findHotspots(sites, 10); // throws exception
    }

}