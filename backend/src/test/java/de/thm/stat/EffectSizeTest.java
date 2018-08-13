// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
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
        assertEquals(1.07, fc.foldChange(0, 1144, 86, 9066), 0.1);
        assertEquals(0.0, fc.foldChange(0, 0, 0, 0), 0.0001);
        assertEquals(0.0, fc.foldChange(10, 10, 10, 10), 0.0001);
        assertEquals(0.0, fc.foldChange(25395, 0, 10000, 0), 0.0001);
    }
}