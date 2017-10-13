package de.thm.calc;

import de.thm.genomeData.tracks.DistanceTrack;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test Distance calc
 *
 * Created by menzel on 9/19/16.
 */
public class DistancesTest {
    @Test
    public void searchTrack() throws Exception {

        DistanceTrack track = TrackFactory.getInstance().createDistanceTrack(Arrays.asList(5L,10L,20L), "testtrack", "no desc", Genome.Assembly.hg19);

        Sites sites = mock(Sites.class);
        List<Long> r = Arrays.asList(1L,7L,10L,12L,19L,25L);

        when(sites.getPositions()).thenReturn(r);
        when(sites.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(sites.getPositionCount()).thenReturn(r.size());

        // run distances
        Distances distances = new Distances();
        TestTrackResult result = distances.searchTrack(track, sites);

        // create expected data

        assertEquals(Arrays.asList(4.,2.,0.,2.,-1.,5.), result.getResultScores());
    }

}