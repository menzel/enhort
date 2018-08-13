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
package de.thm.logo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by menzel on 11/24/16.
 */
public class LogoTest {
    @Test
    public void getConsensus() throws Exception {

        List<String> sequences = new ArrayList<>();

        sequences.add("AATT");
        sequences.add("AACT");
        sequences.add("AATT");
        sequences.add("AATT");
        sequences.add("AATG");


        assertEquals("AATT", LogoCreator.createLogo(sequences).getConsensus());


    }

}