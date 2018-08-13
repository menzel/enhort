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

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Logo extends Serializable {
    /**
     * Add the data for one position for the sequencelogo
     *
     * @param height - the heights of one position of a sequencelogo
     */
    void add(Map<String, Double> height);

    /**
     * Returns the heights for letters of the sequencelogo as JSONArray
     *
     * @return - heights of letters for sequencelogo
     */
    JSONArray getHeights();

    List<List<Map<String, String>>> getValues();

    /**
     * Returns the consensus sequence of the known sequence logo data
     *
     * @return - consensus sequence
     */
    String getConsensus();

    String getRegex();

    String getName();

    void setName(String name);
}
