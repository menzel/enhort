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
package de.thm.calc;

import de.thm.exception.TrackTypeNotAllowedExcpetion;
import de.thm.genomeData.tracks.*;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test intersect alg
 *
 * Created by menzel on 2/6/17.
 */
public class IntersectTest {
    private static Sites sites;
    private static InOutTrack iotrack;
    private static ScoredTrack sctrack;
    private static StrandTrack sttrack;
    private static NamedTrack nmtrack;

    @BeforeClass
    public static void prepare(){


        iotrack  = mock(InOutTrack.class);
        sctrack = mock(ScoredTrack.class);
        sttrack = mock(StrandTrack.class);
        nmtrack = mock(NamedTrack.class);


        when(iotrack.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(sctrack.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(sttrack.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(nmtrack.getAssembly()).thenReturn(Genome.Assembly.hg19);

        when(iotrack.getStarts()).thenReturn(new long[]{5L,35L,50L});
        when(iotrack.getEnds()).thenReturn(new long[]{15L,40L,60L});

        when(sctrack.getStarts()).thenReturn(new long[]{1L,20L,50L});
        when(sctrack.getEnds()).thenReturn(new long[]{10L,30L,60L});
        when(sctrack.getIntervalScore()).thenReturn(new double[]{0.5, .2,.7,.1});

        when(sttrack.getStarts()).thenReturn(new long[]{1L,20L,50L});
        when(sttrack.getEnds()).thenReturn(new long[]{10L,30L,60L});
        when(sttrack.getStrands()).thenReturn(new char[]{'-','+','+'});

        when(nmtrack.getStarts()).thenReturn(new long[]{1L,20L,50L});
        when(nmtrack.getEnds()).thenReturn(new long[]{10L,30L,60L});
        when(nmtrack.getIntervalName()).thenReturn(new String[]{"a", "c", "b"});

        sites = mock(Sites.class);
        List<Long> r = Arrays.asList(1L, 4L, 5L, 6L, 14L, 44L, 52L, 61L, 191L, 200L);

        when(sites.getPositions()).thenReturn(r);
        when(sites.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(sites.getPositionCount()).thenReturn(r.size());
        when(sites.getStrands()).thenReturn(Arrays.asList('+','-','+','+','-','+','+','-','+','+'));

    }

    @Test(expected = TrackTypeNotAllowedExcpetion.class)
    public void searchNotAllowed() throws Exception{

        Intersect<Track> sect = new Intersect<>();
        Track track = mock(Track.class);
        assertNull(sect.searchTrack(track, sites));
    }

    @Test
    public void searchSingleIntervalNamed() throws Exception {

        Intersect<NamedTrack> sect = new Intersect<>();
        TestTrackResult result = sect.searchSingleInterval(nmtrack,sites);

        assertEquals(5, result.getIn());
        assertEquals(5, result.getOut());
        assertArrayEquals(new String[]{"a","b"}, result.getResultNames().keySet().toArray());

    }

    @Test
    public void searchSingleIntervalInOut() throws Exception {

        Intersect<InOutTrack> sect = new Intersect<>();
        TestTrackResult result = sect.searchSingleInterval(iotrack,sites);

        assertEquals(4, result.getIn());
        assertEquals(6, result.getOut());

    }


    @Test
    public void searchStrandTrack() throws Exception {

        Intersect<StrandTrack> sect = new Intersect<>();
        TestTrackResult result = sect.searchSingleInterval(sttrack,sites);

        assertEquals(2, result.getIn());
        assertEquals(8, result.getOut());

    }



    @Test
    public void searchScoredTrack() throws Exception {

        Intersect<ScoredTrack> sect = new Intersect<>();
        TestTrackResult result = sect.searchSingleInterval(sctrack,sites);

        List<Double> scores = new ArrayList<>();

        scores.add(.5);
        scores.add(.5);
        scores.add(.5);
        scores.add(.5);
        scores.add(.7);

        assertEquals(5, result.getIn());
        assertEquals(5, result.getOut());
        assertEquals(scores, result.getResultScores());
    }
}