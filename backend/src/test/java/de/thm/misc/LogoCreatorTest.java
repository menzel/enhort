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
package de.thm.misc;

import de.thm.logo.LogoCreator;
import de.thm.logo.Sequencelogo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the creation of sequence logos
 * Created by menzel on 11/9/16.
 */
public class LogoCreatorTest {
    @Test
    public void createLogo() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("AATT");
        sequences.add("AACT");
        sequences.add("AATT");
        sequences.add("AATT");

        Sequencelogo result = new Sequencelogo();

        Map<String, Double> a = new HashMap<>();
        a.put("a", 2d);
        a.put("c", 0d);
        a.put("t", 0d);
        a.put("g", 0d);
        result.add(a);

        Map<String, Double> b = new HashMap<>();
        b.put("a", 2d);
        b.put("c", 0d);
        b.put("t", 0d);
        b.put("g", 0d);
        result.add(b);

        Map<String, Double> c = new HashMap<>();
        c.put("a", 0d);
        c.put("c", 0.29718046888521676);
        c.put("t", 0.8915414066556503);
        c.put("g", 0d);
        result.add(c);

        Map<String, Double> d = new HashMap<>();
        d.put("a", 0d);
        d.put("c", 0d);
        d.put("t", 2d);
        d.put("g", 0d);
        result.add(d);


        assertEquals(result, LogoCreator.createLogo(sequences));

    }

     @Test
     public void testToJSON() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("AATT");
        sequences.add("AACT");
        sequences.add("AATT");
        sequences.add("AATT");

        String result = "[[{letter=a, bits=2.0}, {letter=c, bits=0.0}, {letter=t, bits=0.0}, {letter=g, bits=0.0}], [{letter=a, bits=2.0}, {letter=c, bits=0.0}, {letter=t, bits=0.0}, {letter=g, bits=0.0}], [{letter=a, bits=0.0}, {letter=c, bits=0.29718046888521676}, {letter=t, bits=0.8915414066556503}, {letter=g, bits=0.0}], [{letter=a, bits=0.0}, {letter=c, bits=0.0}, {letter=t, bits=2.0}, {letter=g, bits=0.0}]]";

        assertEquals(result, LogoCreator.createLogo(sequences).toString());
     }
}