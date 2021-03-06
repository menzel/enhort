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
package de.thm.backgroundModel;

import de.thm.misc.Genome;
import de.thm.positionData.AbstractSites;

import java.util.List;

public class BackgroundModel extends AbstractSites {

    public BackgroundModel(List<Long> positions, List<Character> strands, Genome.Assembly assembly) {
        this.positions = positions;
        this.strands = strands;
        this.assembly = assembly;
    }

    public BackgroundModel(List<Long> positions, Genome.Assembly assembly) {
        this.positions = positions;
        this.assembly = assembly;
    }
}
