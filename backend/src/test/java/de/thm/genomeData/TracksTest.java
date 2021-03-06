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
package de.thm.genomeData;

import de.thm.genomeData.tracks.*;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for tracks class
 *
 * Created by Michael Menzel on 19/1/16. *
 */
public class TracksTest {
    @Test
    public void sumOfIntervals() throws Exception {
        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();

        starts.add(5L);
        starts.add(15L);
        starts.add(25L);

        ends.add(10L);
        ends.add(20L);
        ends.add(26L);

        InOutTrack base = mockTrack(starts, ends);

        assertEquals(11, Tracks.sumOfIntervals(base));
    }


    @Test
    public void crossvalidationSumIntersect() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(45L);
        start1.add(100L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(80L);
        end1.add(110L);

        end2.add(15L);
        end2.add(40L);
        end2.add(55L);

        Track interval1 = mockTrack(start1, end1);
        Track interval2 = mockTrack(start2, end2);


        // sum(a,b) == -TestTrack(-a, -b)
        assertArrayEquals(Tracks.sum(interval1, interval2).getStarts(), Tracks.invert(Tracks.intersect(Tracks.invert(interval1), Tracks.invert(interval2))).getStarts());

        // sum(-a, -b) == -TestTrack(a, b)
        assertArrayEquals(Tracks.sum(Tracks.invert(interval1), Tracks.invert(interval2)).getStarts(), Tracks.invert(Tracks.intersect(interval1, interval2)).getStarts());

    }

    @Test
    public void testIntersect() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(10L);
        start1.add(30L);
        start1.add(80L);
        start1.add(110L);

        end1.add(20L);
        end1.add(45L);
        end1.add(100L);
        end1.add(3095677412L); // chromosome end


        start2.add(0L);
        start2.add(15L);
        start2.add(40L);
        start2.add(55L);

        end2.add(5L);
        end2.add(35L);
        end2.add(50L);
        end2.add(3095677412L); // chromosome end

        Track track1 = mockTrack(start1, end1);
        Track track2 = mockTrack(start2, end2);

        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();

        expectedStarts.add(15L);
        expectedStarts.add(30L);
        expectedStarts.add(40L);
        expectedStarts.add(80L);
        expectedStarts.add(110L);

        expectedEnds.add(20L);
        expectedEnds.add(35L);
        expectedEnds.add(45L);
        expectedEnds.add(100L);
        expectedEnds.add(3095677412L); // chromosome end

        Track result = Tracks.intersect(track1, track2);

        assertArrayEquals(expectedStarts.stream().mapToLong(l -> l).toArray(), result.getStarts());
        assertArrayEquals(expectedEnds.stream().mapToLong(l -> l).toArray(), result.getEnds());
    }


    @Test
    public void testSum() throws Exception {

        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(45L);
        start1.add(100L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(80L);
        end1.add(110L);

        end2.add(15L);
        end2.add(40L);
        end2.add(55L);

        Track interval1 = mockTrack(start1, end1);
        Track interval2 = mockTrack(start2, end2);


        List<Track> trackList = new ArrayList<>();

        trackList.add(interval1);
        trackList.add(interval2);


        Track result = Tracks.sum(trackList);
        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();

        expectedStarts.add(0L);
        expectedStarts.add(20L);
        expectedStarts.add(35L);
        expectedStarts.add(45L);
        expectedStarts.add(100L);

        expectedEnds.add(15L);
        expectedEnds.add(30L);
        expectedEnds.add(40L);
        expectedEnds.add(80L);
        expectedEnds.add(110L);

        Objects.requireNonNull(result);
        assertArrayEquals(expectedStarts.stream().mapToLong(l -> l).toArray(), result.getStarts());
        assertArrayEquals(expectedEnds.stream().mapToLong(l -> l).toArray(), result.getEnds());

    }

    @Test
    public void testXor() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(60L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end2.add(15L);
        end2.add(40L);
        end2.add(60L);

        Track interval1 = mockTrack(start1, end1);
        Track interval2 = mockTrack(start2, end2);

        Track result = Tracks.xor(interval1, interval2);

        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();

        expectedStarts.add(0L);
        expectedStarts.add(10L);
        expectedStarts.add(20L);
        expectedStarts.add(35L);

        expectedEnds.add(5L);
        expectedEnds.add(15L);
        expectedEnds.add(30L);
        expectedEnds.add(40L);

        assertArrayEquals(expectedStarts.stream().mapToLong(l -> l).toArray(), result.getStarts());
        assertArrayEquals(expectedEnds.stream().mapToLong(l -> l).toArray(), result.getEnds());

    }


    //@Test  // runs forver at this time
    public void xor_inner() throws Exception {
        Track first = mock(Track.class);
        when(first.getStarts()).thenReturn(new long[]{1L, 22L});
        when(first.getEnds()).thenReturn(new long[]{5L, 30L});

        Track second = mock(Track.class);
        when(second.getStarts()).thenReturn(new long[]{20L});
        when(second.getEnds()).thenReturn(new long[]{32L});

        Track result = Tracks.xor(first, second);
        assertArrayEquals(new long[]{1L,20L,30L}, result.getStarts());
        assertArrayEquals(new long[]{5L,22L,32L}, result.getEnds());

    }

    @Test
    public void testSubsetScore() throws Exception {
        List<Long> start1 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(50L);
        start1.add(80L);
        start1.add(100L);

        end1.add(10L);
        end1.add(30L);
        end1.add(60L);
        end1.add(90L);
        end1.add(110L);


        List<Double> scores = new ArrayList<>();

        scores.add(0.5);
        scores.add(0.5);
        scores.add(0.3);
        scores.add(0.2);
        scores.add(0.5);

        List<String> names = new ArrayList<>();

        names.add("foobar1");
        names.add("foobar2");
        names.add("foobar3");
        names.add("foobar4");
        names.add("foobar5");

        ScoredTrack interval1 = mockTrack(start1, end1, names, scores);


        List<Double> result = new ArrayList<>();

        result.add(0.5);
        result.add(0.5);
        result.add(0.5);

        ScoredTrack track = (ScoredTrack) Tracks.subsetScore(interval1, 0.5);
        assertArrayEquals(track.getIntervalScore(), result.stream().mapToDouble(Double::doubleValue).toArray(), 0.05);
        assertEquals(track.getStarts().length, 3);
        assertEquals(track.getEnds().length, 3);


    }

     @Test
    public void testInvert() throws Exception {

        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();

        starts.add(5L);
        starts.add(15L);
        starts.add(25L);

        ends.add(10L);
        ends.add(20L);
        ends.add(26L);

        Track base = mockTrack(starts,ends);

        Track invert  = Tracks.invert(base);
        List<Long> expectedStarts = new ArrayList<>();
        List<Long> expectedEnds = new ArrayList<>();

        expectedStarts.add(0L);
        expectedStarts.add(10L);
        expectedStarts.add(20L);
        expectedStarts.add(26L);

        expectedEnds.add(5L);
        expectedEnds.add(15L);
        expectedEnds.add(25L);
        expectedEnds.add(ChromosomSizes.getInstance().getGenomeSize(Genome.Assembly.hg19));

         assertArrayEquals(expectedStarts.stream().mapToLong(l -> l).toArray(), invert.getStarts());
         assertArrayEquals(expectedEnds.stream().mapToLong(l -> l).toArray(), invert.getEnds());

         assertArrayEquals(starts.stream().mapToLong(l -> l).toArray(), base.getStarts());
         assertArrayEquals(ends.stream().mapToLong(l -> l).toArray(), base.getEnds());

        Track doubleInvert = Tracks.invert(Tracks.invert(base));

         assertArrayEquals(starts.stream().mapToLong(l -> l).toArray(), doubleInvert.getStarts());
         assertArrayEquals(ends.stream().mapToLong(l -> l).toArray(), doubleInvert.getEnds());
    }

    @Test
    public void testConvertToScore() throws Exception {

        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();

        starts.add(5L);
        starts.add(15L);
        starts.add(25L);

        ends.add(10L);
        ends.add(20L);
        ends.add(26L);

        InOutTrack base = mockTrack(starts,ends);

        List<Double> expectedScores = new ArrayList<>();


        expectedScores.add(1d);
        expectedScores.add(1d);
        expectedScores.add(1d);

        ScoredTrack result = Tracks.cast(base);

        assertArrayEquals(expectedScores.stream().mapToDouble(l -> l).toArray(), result.getIntervalScore(), 0.1);

    }
    private ScoredTrack mockTrack(List<Long> start, List<Long> end, List<String> names, List<Double> scores) {

        return  TrackFactory.getInstance().createScoredTrack(start, end, names, scores,"name", "desc");
    }


    private InOutTrack mockTrack(List<Long> start, List<Long> end) {

        return  TrackFactory.getInstance().createInOutTrack(start, end, "name", "desc", Genome.Assembly.hg19);
    }



    @Test
    public void testCast() throws Exception {
        InOutTrack inOutTrack = mock(InOutTrack.class);
        when(inOutTrack.getStarts()).thenReturn(new long[]{1L, 10L});
        when(inOutTrack.getEnds()).thenReturn(new long[]{8L, 18L});

        ScoredTrack scoredTrack = Tracks.cast(inOutTrack);

        assertArrayEquals(inOutTrack.getStarts(), scoredTrack.getStarts());
        assertArrayEquals(inOutTrack.getEnds(), scoredTrack.getEnds());
        assertArrayEquals(new double[]{1d, 1d}, scoredTrack.getIntervalScore(), 0.0001);
    }


    @Test
    public void testCastNamed() throws Exception {
        NamedTrack namedTrack = mock(NamedTrack.class);
        when(namedTrack.getStarts()).thenReturn(new long[]{1L, 10L});
        when(namedTrack.getEnds()).thenReturn(new long[]{8L, 18L});
        when(namedTrack.getIntervalName()).thenReturn(new String[]{"first", "second"});

        ScoredTrack scoredTrack = Tracks.cast(namedTrack);

        assertArrayEquals(namedTrack.getStarts(), scoredTrack.getStarts());
        assertArrayEquals(namedTrack.getEnds(), scoredTrack.getEnds());
        assertArrayEquals(namedTrack.getIntervalName(), scoredTrack.getIntervalName());
        assertArrayEquals(new double[]{9.7440432E7, -9.0627982E8}, scoredTrack.getIntervalScore(), 0.001);
    }



         @Test
    public void bin() throws Exception {
        List<Double> vals = new ArrayList<>();

        vals.add(-0.25);
        vals.add(-0.20);
        vals.add(-0.19);
        vals.add(-0.1);
        vals.add(-0.09);

        vals.add(0.11);
        vals.add(0.19);
        vals.add(0.22);
        vals.add(0.23);

        vals.add(0.41);
        vals.add(0.42);
        vals.add(0.431);

        vals.add(0.51);
        vals.add(0.52);
        vals.add(0.53);

        vals.add(0.89);
        vals.add(0.91);
        vals.add(0.934);
        vals.add(0.95);

        vals.add(4.);

        double[] exp = new double[]{-0.25, -0.1, -0.1, -0.1, 0.22, 0.22, 0.22, 0.22, 0.431, 0.431, 0.431, 0.431, 0.89, 0.89, 0.89, 0.89, 4.0, 4.0, 4.0, 4.0};

             ScoredTrack track = Tracks.bin(TrackFactory.getInstance().createScoredTrack(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), vals, "noname", ""), 5);

             assertArrayEquals(exp, track.getIntervalScore(), 0.05);
    }

}