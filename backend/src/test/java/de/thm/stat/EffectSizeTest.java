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

        assertEquals(0.122216, fc.foldChange(6000, 4000, 4700, 5300), 0.0001);
        assertEquals(0.514548, fc.foldChange(1139, 5, 9021, 131), 0.1);
        assertEquals(1.037, fc.foldChange(0, 1144, 86, 9066), 0.1);
        assertEquals(0.0, fc.foldChange(0, 0, 0, 0), 0.0001);
        assertEquals(0.0, fc.foldChange(10, 10, 10, 10), 0.0001);

    }
}