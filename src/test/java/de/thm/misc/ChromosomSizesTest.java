package de.thm.misc;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 7/1/16.
 */
public class ChromosomSizesTest {

    @Test
    public void testOffset() throws Exception {
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        System.out.println(chrSizes.getChrSize("chr1"));

        assertEquals(chrSizes.getChrSize("chr1"), chrSizes.getChrSize("chr1"));

    }
}