package de.thm.genomeData;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 15/12/15.
 */
public class IntervalDumperTest {
    String basePath;
    Interval interval;
    IntervalDumper dumper;

    @Before
    public void setUp() throws Exception {

        basePath = "/home/menzel/Desktop/THM/lfba/projekphase/dat/";
        interval = new GenomeInterval(new File(basePath + "inout/exons.bed"), Intervals.Type.inout, "exons.bed");
        dumper = new IntervalDumper(new File("/tmp/").toPath());

        File folder = new File("/tmp/kryo/");

        if(folder.exists()){
            folder.delete();
        }

        folder.mkdir();
    }

    @Test
    public void testGetInterval() throws Exception {

        dumper.dumpInterval(interval, "foo.bed");
        Interval newInterval = dumper.getInterval(new File("/tmp/kryo/" + "foo.kryo"));
        assertEquals(interval.getIntervalsStart(), newInterval.getIntervalsStart());
    }
}