package de.thm.stat;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Michael Menzel on 28/2/16.
 */
public class EffectSizeTest {

    @Test
    public void testFoldChange() throws Exception {
        EffectSize fc = new EffectSize();

        assertEquals(1., fc.foldChange(6000, 4000, 4700, 5300), 0.0001);
        assertEquals(26.2, fc.foldChange(1139,5,9021,131), 0.1);
        assertEquals(Double.POSITIVE_INFINITY, fc.foldChange(0,1144,86,9066), 0.1);
        assertEquals(Double.POSITIVE_INFINITY, fc.foldChange(0,0,0,0), 0.0001);
        assertEquals(1., fc.foldChange(10,10,10,10), 0.0001);

    }
}