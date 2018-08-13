// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.*;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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

    public void generateInGenes() throws Exception {

        TrackEntry te = new TrackEntry("Known genes", "desc", "hg19/inout/knownGenes.bed", "inout", "hg19", "", 20, "", 42, "", "");
        TrackFactory.getInstance().loadTrack(te);

        Track t = TrackFactory.getInstance().getTrackByName("Known genes", Genome.Assembly.hg19);

        Collection<Long> sites = SingleTrackBackgroundModel.randPositions(100000, t);

        Path path = Paths.get("/home/menzel/allInGenes.bed");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            for (Long site : sites) {
                Pair<String, Long> p = ChromosomSizes.getInstance().mapToChr(Genome.Assembly.hg19, site);
                writer.write(p.getLeft() + "\t" + p.getRight() + "\t" + p.getRight() + 1 + "\n");
            }
        }
    }


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


        //create random sites INSIDE the base track
        List<Long> sites = new ArrayList<>(SingleTrackBackgroundModel.randPositions(3000, base));
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


        //create random sites INSIDE the base track
        List<Long> sites = new ArrayList<>(SingleTrackBackgroundModel.randPositions(count, base));

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


        //mock contigs track
        Track contigs = mock(InOutTrack.class);
        when(contigs.getStarts()).thenReturn(new long[]{0});
        when(contigs.getEnds()).thenReturn(new long[]{Collections.max(ends)});
        when(contigs.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(contigs.getName()).thenReturn("Contigs");
        //end mock contigs track

        Track filteredInvertTrack = Tracks.intersect(Tracks.invert(base), contigs);

        //create random sites INSIDE the base track
        List<Long> sites = new ArrayList<>(SingleTrackBackgroundModel.randPositions(count, filteredInvertTrack));

        Intersect<InOutTrack> intersect = new Intersect<>();
        Sites sitesObject = mock(Sites.class);
        when(sitesObject.getPositions()).thenReturn(sites);

        //intersect sites with the base track
        TestTrackResult result = intersect.searchSingleInterval((InOutTrack) contigs, sitesObject);
        assertEquals(0, result.getOut()); // no sites should be outside the contigs
        assertEquals(count, result.getIn()); // all positions should be somewhere inside the contigs
    }
}