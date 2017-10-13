package de.thm.lib;

import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LibTest {

    @Test
    public void chrSizesTest() throws Exception{
        assertTrue(ChromosomSizes.getInstance().getGenomeSize(Genome.Assembly.hg19) > 10000);
    }
}
