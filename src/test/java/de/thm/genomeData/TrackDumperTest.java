package de.thm.genomeData;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 15/12/15.
 */
public class TrackDumperTest {
    String basePath;
    Track track;
    IntervalDumper dumper;

    @Before
    public void setUp() throws Exception {

        basePath = "/home/menzel/Desktop/THM/lfba/projekphase/dat/";
        track = IntervalFactory.getInstance().getAllIntervals().get("exons.bed");
        dumper = new IntervalDumper(new File("/tmp/").toPath());

        File folder = new File("/tmp/kryo/");

        if(folder.exists()){
            folder.delete();
        }

        folder.mkdir();
    }

    @Test
    public void testGetInterval() throws Exception {

        dumper.dumpInterval(track, "foo.bed");
        Track newTrack = dumper.getInterval(new File("/tmp/kryo/" + "foo.kryo"));
        assertEquals(track.getIntervalsStart(), newTrack.getIntervalsStart());
    }
}