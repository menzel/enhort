package de.thm.misc;

import de.thm.logo.GenomeFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 7/1/16.
 */
public class ChromosomSizesTest {

    @Test
    public void testOffset() throws Exception {
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        //System.out.println(chrSizes.getChrSize("chr1"));

        assertEquals(chrSizes.getChrSize(GenomeFactory.Assembly.hg19, "chr1"), chrSizes.getChrSize( GenomeFactory.Assembly.hg19, "chr1"));

    }

    @Test
    public void testMapToChr() throws Exception {
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        Long p = 9980L;
        Long pos = chrSizes.offset(GenomeFactory.Assembly.hg19, "chr1") + p;
        assertEquals(new ImmutablePair<>("chr1", p),chrSizes.mapToChr(GenomeFactory.Assembly.hg19, pos));


        p = 110179960L;
        pos = chrSizes.offset(GenomeFactory.Assembly.hg19, "chr6") + p;
        assertEquals(new ImmutablePair<>("chr6", p),chrSizes.mapToChr(GenomeFactory.Assembly.hg19, pos));


        p = 37080600L;
        pos = chrSizes.offset(GenomeFactory.Assembly.hg19, "chr15") + p;
        assertEquals(new ImmutablePair<>("chr15", p),chrSizes.mapToChr(GenomeFactory.Assembly.hg19, pos));


    }
}