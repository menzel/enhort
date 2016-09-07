package de.thm.genomeData;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.misc.ChromosomSizes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for tracks class
 *
 * Created by Michael Menzel on 19/1/16. *
 */
public class TracksTest {



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


        try {
            // sum(a,b) == -TestTrack(-a, -b)
            assertEquals(Tracks.sum(interval1,interval2).getIntervalsStart(), Tracks.invert(Tracks.intersect(Tracks.invert(interval1), Tracks.invert(interval2))).getIntervalsStart());

            // sum(-a, -b) == -TestTrack(a, b)
            assertEquals(Tracks.sum(Tracks.invert(interval1), Tracks.invert(interval2)).getIntervalsStart(), Tracks.invert(Tracks.intersect(interval1, interval2)).getIntervalsStart());

            // sum(a, -b) == TestTrack(-a, b)
            assertEquals(Tracks.sum(interval1, Tracks.invert(interval2)).getIntervalsStart(), Tracks.intersect(Tracks.invert(interval1), interval2).getIntervalsStart());

        }  catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
            intervalTypeNotAllowedExcpetion.printStackTrace();
        }

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

        assertEquals(expectedStarts, result.getIntervalsStart());
        assertEquals(expectedEnds, result.getIntervalsEnd());

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


        Track result = null;
        try {
            result = Tracks.sum(trackList);
        } catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
            intervalTypeNotAllowedExcpetion.printStackTrace();
        }

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

        assertEquals(expectedStarts, result.getIntervalsStart());
        assertEquals(expectedEnds, result.getIntervalsEnd());


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

        assertEquals(expectedStarts, result.getIntervalsStart());
        assertEquals(expectedEnds, result.getIntervalsEnd());

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
        assertEquals(track.getIntervalScore(), result);
        assertEquals(track.getIntervalsStart().size(), 3);
        assertEquals(track.getIntervalsEnd().size(), 3);


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
        expectedEnds.add(ChromosomSizes.getInstance().getGenomeSize());

        assertEquals(expectedStarts, invert.getIntervalsStart());
        assertEquals(expectedEnds, invert.getIntervalsEnd());

        assertEquals(starts, base.getIntervalsStart());
        assertEquals(ends, base.getIntervalsEnd());

        Track doubleInvert = Tracks.invert(Tracks.invert(base));

        assertEquals(starts, doubleInvert.getIntervalsStart());
        assertEquals(ends, doubleInvert.getIntervalsEnd());
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

        assertEquals(expectedScores, result.getIntervalScore());

    }
    private ScoredTrack mockTrack(List<Long> start, List<Long> end, List<String> names, List<Double> scores) {

        return  TrackFactory.getInstance().createScoredTrack(start, end, names, scores,"name", "desc");
    }


    private InOutTrack mockTrack(List<Long> start, List<Long> end) {

        return  TrackFactory.getInstance().createInOutTrack(start, end, "name", "desc");
    }



    @Test
    public void testCast() throws Exception {
        //TOOD

    }
}