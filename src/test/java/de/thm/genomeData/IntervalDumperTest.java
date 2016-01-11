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

        long startTime = System.nanoTime();
        interval = new Interval(new File(basePath + "inout/exons.bed"), Interval.Type.inout);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("duration file " + duration/1000000);


        dumper = new IntervalDumper(new File("/tmp/").toPath());
    }

    @Test
    public void testGetInterval() throws Exception {
        dumper.dumpInterval(interval, "foo");

        long startTime = System.nanoTime();

        Interval newInterval = dumper.getInterval(new File("/tmp/" + "foo"));

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("duration object " + duration/1000000);

        assertEquals(interval.getIntervalsStart(), newInterval.getIntervalsStart());
    }
}