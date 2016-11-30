package de.thm.misc;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.genomeData.Tracks;
import de.thm.logo.GenomeFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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


        try {
            results.add(Tracks.sum(tracks.get(0), tracks.get(1)));
            results.add(Tracks.sum(tracks.get(1), tracks.get(0)));
            results.add(Tracks.sum(tracks.get(0), Tracks.sum(tracks.get(1), tracks.get(2))));
            results.add(Tracks.intersect(tracks.get(1), tracks.get(0)));
            results.add(Tracks.intersect(Tracks.sum(tracks.get(0), tracks.get(1)), tracks.get(2)));

        } catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
            intervalTypeNotAllowedExcpetion.printStackTrace();
        }

        int i = 0;

        for(String expression: expressions){

            Track result = builder.build(expression);
            assertEquals(results.get(i).getStarts(), result.getStarts());
            assertEquals(results.get(i++).getEnds(), result.getEnds());
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

        Track interval1 = mockTrack(start1, end1);
        Track interval2 = mockTrack(start2, end2);
        Track interval3 = mockTrack(start3, end3);


        tracks = new ArrayList<>();
        tracks.add(interval1);
        tracks.add(interval2);
        tracks.add(interval3);


        return tracks;
    }

    private Track mockTrack(List<Long> start, List<Long> end) {

        Track track = TrackFactory.getInstance().createInOutTrack(start, end,"name", "desc", GenomeFactory.Assembly.hg19);

        TrackFactory.getInstance().addTrack(track);

        return track;
    }


}