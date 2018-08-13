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
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Michael Menzel on 1/2/16.
 */
public class BetterBackgroundModelTest {

    @Test
    public void testRandPositionsScored() throws Exception {
        ArrayList<Long> startList = new ArrayList<>();
        ArrayList<Long> endList = new ArrayList<>();

        startList.add(5L);
        startList.add(20L);
        startList.add(50L);
        startList.add(80L);

        endList.add(15L);
        endList.add(30L);
        endList.add(80L);
        endList.add(90L);

        InOutTrack track = mockInterval(startList,endList);

        // mock sites
        Sites sites = mock(Sites.class);
        List<Long> r = Arrays.asList(1L,10L,12L,22L,35L,60L,70L,100L);

        when(sites.getPositions()).thenReturn(r);
        when(sites.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(sites.getPositionCount()).thenReturn(r.size());
        // end mock sites

        //mock contigs track
        Track contigs = mock(InOutTrack.class);
        when(contigs.getStarts()).thenReturn(new long[]{0});
        when(contigs.getEnds()).thenReturn(new long[]{Collections.max(r)});
        when(contigs.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(contigs.getName()).thenReturn("Contigs");

        TrackFactory factory = TrackFactory.getInstance();
        factory.addTrack(contigs);
        //end mock contigs track

        BackgroundModel better = SingleTrackBackgroundModel.create(track , sites, sites.getPositionCount());

        Intersect sect = new Intersect<>();

        TestTrackResult set = sect.searchSingleInterval(track,sites);
        TestTrackResult gen = sect.searchSingleInterval(track,better);


        assertEquals(set.getOut(),gen.getOut());
        assertEquals(set.getIn(),gen.getIn());

    }

        private InOutTrack mockInterval(List<Long> start, List<Long> end) {

            return TrackFactory.getInstance().createInOutTrack(start,end,"test", "test track", Genome.Assembly.hg19);
    }


}