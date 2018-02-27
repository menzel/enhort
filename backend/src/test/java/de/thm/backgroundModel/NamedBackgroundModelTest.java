package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.NamedTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class NamedBackgroundModelTest {

    @Test
    public void testNamedBgModelCreation() {
        ArrayList<Long> startList = new ArrayList<>();
        ArrayList<Long> endList = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        startList.add(5L);
        startList.add(20L);
        startList.add(50L);
        startList.add(80L);

        endList.add(15L);
        endList.add(30L);
        endList.add(80L);
        endList.add(90L);

        names.add("a");
        names.add("b");
        names.add("a");
        names.add("c");

        NamedTrack track = mockTrack(startList, endList, names);

        // mock sites
        Sites sites = mock(Sites.class);
        List<Long> r = Arrays.asList(1L, 10L, 12L, 22L, 35L, 60L, 70L, 100L);

        when(sites.getPositions()).thenReturn(r);
        when(sites.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(sites.getPositionCount()).thenReturn(r.size());
        // end mock sites

        //mock contigs track
        Track contigs = mock(InOutTrack.class);
        when(contigs.getStarts()).thenReturn(new long[]{0});
        when(contigs.getEnds()).thenReturn(new long[]{Long.MAX_VALUE});
        when(contigs.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(contigs.getName()).thenReturn("Contigs");

        TrackFactory factory = TrackFactory.getInstance();
        factory.addTrack(contigs);
        //end mock contigs track


        BackgroundModel backgroundModel = NamedBackgroundModel.create(sites, 100, track);

        //assertTrue(backgroundModel.positions.size() > 90);

        Intersect calc = new Intersect();
        TestTrackResult resultBg = calc.searchSingleInterval(track, backgroundModel);
        TestTrackResult resultOrig = calc.searchSingleInterval(track, sites);

        assertEquals(((double) resultOrig.getOut()) / sites.getPositionCount(), ((double) resultBg.getOut()) / backgroundModel.getPositionCount(), 0.3);
        assertEquals(((double) resultOrig.getIn()) / sites.getPositionCount(), ((double) resultBg.getIn()) / backgroundModel.getPositionCount(), 0.3);

    }


    private NamedTrack mockTrack(List<Long> start, List<Long> end, List<String> names) {

        return TrackFactory.getInstance().createNamedTrack(start, end, names, "name", "desc", Genome.Assembly.hg19, "None");
    }

}