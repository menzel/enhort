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
package de.thm.misc;

import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.Tracks;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 * Test cases for the build method of the track builder class
 *
 * Created by menzel on 10/11/16.
 */
public class TrackBuilderTest {

    @Test
    public void build() throws Exception {

        // create expressions
        List<String> expressions = new ArrayList<>();


        //create tracks
        List<Track> tracks = createTracks();


        expressions.add(tracks.get(0).getUid() + " or " + tracks.get(1).getUid());
        expressions.add(tracks.get(1).getUid() + " or "+ tracks.get(0).getUid());
        expressions.add(tracks.get(0).getUid() + " or (" + tracks.get(1).getUid() + " or "+ tracks.get(2).getUid() + ")");
        expressions.add(tracks.get(1).getUid() + " and " + tracks.get(0).getUid());
        expressions.add(tracks.get(0).getUid() + " or " + tracks.get(1).getUid() + " + and "+ tracks.get(2).getUid());

        TrackBuilder builder = new TrackBuilder();

        //create results
        List<Track> results = new ArrayList<>();

        results.add(Tracks.sum(tracks.get(0), tracks.get(1)));
        results.add(Tracks.sum(tracks.get(1), tracks.get(0)));
        results.add(Tracks.sum(tracks.get(0), Tracks.sum(tracks.get(1), tracks.get(2))));
        results.add(Tracks.intersect(tracks.get(1), tracks.get(0)));
        results.add(Tracks.intersect(Tracks.sum(tracks.get(0), tracks.get(1)), tracks.get(2)));


        int i = 0;

        for(String expression: expressions){

            Track result = builder.build(expression);
            assertArrayEquals(results.get(i).getStarts(), result.getStarts());
            assertArrayEquals(results.get(i++).getEnds(), result.getEnds());
            //System.err.println("done '" + expression + "' without error");

        }
    }

    private List<Track> createTracks() {

        List<Track> tracks;

        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();
        List<Long> start3 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();
        List<Long> end3 = new ArrayList<>();

        start1.add(1L);
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


        start3.add(4L);
        end3.add(42L);

        Track interval1 = mockTrack(start1, end1, 41);
        Track interval2 = mockTrack(start2, end2, 42);
        Track interval3 = mockTrack(start3, end3, 43);


        tracks = new ArrayList<>();
        tracks.add(interval1);
        tracks.add(interval2);
        tracks.add(interval3);


        return tracks;
    }

    private Track mockTrack(List<Long> start, List<Long> end, int uid) {

        Track track = TrackFactory.getInstance().createInOutTrack(start, end,"name", "desc", Genome.Assembly.hg19);

        Class<?> inner = track.getClass();
        try {
            Field id = inner.getSuperclass().getDeclaredField("id");
            id.setAccessible(true);

            id.setInt(track, uid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackFactory.getInstance().addTrack(track);

        return track;
    }


}