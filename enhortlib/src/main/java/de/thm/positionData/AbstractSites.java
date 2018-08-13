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
package de.thm.positionData;


import de.thm.misc.Genome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Skeletal implementation for the Sites interface.
 * <p>
 * Created by Michael Menzel on 23/2/16.
 */
public abstract class AbstractSites implements Sites {

    public List<Long> positions;
    public Genome.Assembly assembly;
    public List<Character> strands = new ArrayList<>();
    public String cellline;

    @Override
    public void addPositions(Collection<Long> values) {
        this.positions.addAll(values);
    }

    @Override
    public List<Long> getPositions() {
        return this.positions;
    }

    @Override
    public void setPositions(List<Long> positions) {
        this.positions = positions;
    }

    @Override
    public int getPositionCount() {
        return positions.size();
    }

    @Override
    public List<Character> getStrands() {
        return this.strands;
    }

    @Override
    public Genome.Assembly getAssembly() {
        return this.assembly;
    }

    @Override
    public String getCellline() {
        if (cellline == null)
            return "Unknown";
        return this.cellline;
    }

}

