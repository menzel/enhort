package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.Tracks;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for the single track background model
 * <p>
 * Created by menzel on 6/21/17.
 */
public class SingleTrackBackgroundModelTest {
    @Test
    public void randPositions() throws Exception {

        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();

        starts.add(5L);
        starts.add(15L);
        starts.add(25L);

        ends.add(10L);
        ends.add(20L);
        ends.add(26L);

        InOutTrack base = mockTrack(starts, ends);

        SingleTrackBackgroundModel model = new SingleTrackBackgroundModel(Genome.Assembly.hg19);

        //create random sites INSIDE the base track
        List<Long> sites = new ArrayList<>(model.randPositions(3000, base));
        sites = sites.stream().distinct().collect(Collectors.toList());

        Intersect<InOutTrack> intersect = new Intersect<>();
        Sites sitesObject = mock(Sites.class);
        when(sitesObject.getPositions()).thenReturn(sites);

        //intersect sites with the base track
        TestTrackResult result = intersect.searchSingleInterval(base, sitesObject);

        assertEquals(0, result.getOut()); // no sites should be outside
        assertEquals(11, result.getIn()); // all positions should be taken, could fail if the rand pos is too small

        //check if each position is choosen (could fail sometimes)
        assertArrayEquals(sites.toArray(), new Long[]{5L, 6L, 7L, 8L, 9L, 15L, 16L, 17L, 18L, 19L, 25L});
    }


    @Test
    public void randPositionsSecond() throws Exception {

        int count = 300000;

        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();

        starts.add(5L);
        starts.add(15L);
        starts.add(25L);
        starts.add(30L);

        ends.add(10L);
        ends.add(20L);
        ends.add(26L);
        ends.add(10000L);

        InOutTrack base = mockTrack(starts, ends);

        SingleTrackBackgroundModel model = new SingleTrackBackgroundModel(Genome.Assembly.hg19);

        //create random sites INSIDE the base track
        List<Long> sites = new ArrayList<>(model.randPositions(count, base));

        Intersect<InOutTrack> intersect = new Intersect<>();
        Sites sitesObject = mock(Sites.class);
        when(sitesObject.getPositions()).thenReturn(sites);

        //intersect sites with the base track
        TestTrackResult result = intersect.searchSingleInterval(base, sitesObject);

        assertEquals(0, result.getOut()); // no sites should be outside
        assertEquals(count, result.getIn()); // all positions should be somewhere inside
    }


    private InOutTrack mockTrack(List<Long> start, List<Long> end) {

        return TrackFactory.getInstance().createInOutTrack(start, end, "name", "desc", Genome.Assembly.hg19);
    }

    @Test
    public void randPosReal() throws Exception {

        int count = 300000;

        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();

        starts.add(5L);
        starts.add(10000L);
        starts.add(25000L);
        starts.add(3000000L);

        ends.add(10L);
        ends.add(20000L);
        ends.add(26000L);
        ends.add(4000000L);

        InOutTrack base = mockTrack(starts, ends);

        SingleTrackBackgroundModel model = new SingleTrackBackgroundModel(Genome.Assembly.hg19);

        //mock contigs track
        Track contigs = mock(InOutTrack.class);
        when(contigs.getStarts()).thenReturn(new long[]{0});
        when(contigs.getEnds()).thenReturn(new long[]{Collections.max(ends)});
        when(contigs.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(contigs.getName()).thenReturn("Contigs");
        //end mock contigs track

        Track filteredInvertTrack = Tracks.intersect(Tracks.invert(base), contigs);

        //create random sites INSIDE the base track
        List<Long> sites = new ArrayList<>(model.randPositions(count, filteredInvertTrack));

        Intersect<InOutTrack> intersect = new Intersect<>();
        Sites sitesObject = mock(Sites.class);
        when(sitesObject.getPositions()).thenReturn(sites);

        //intersect sites with the base track
        TestTrackResult result = intersect.searchSingleInterval((InOutTrack) contigs, sitesObject);
        assertEquals(0, result.getOut()); // no sites should be outside the contigs
        assertEquals(count, result.getIn()); // all positions should be somewhere inside the contigs
    }
}