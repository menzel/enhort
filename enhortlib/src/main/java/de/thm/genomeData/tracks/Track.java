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
package de.thm.genomeData.tracks;

import de.thm.misc.Genome;

import java.io.Serializable;

/**
 * Interface for interval data. Holds lists of start and stop positions of intervals as well as their names and scores.
 * <p>
 * Each Interval has a uniq ID by which it is identified.
 * <p>
 * <p>The intervals list can get very big, it is therefore neccessary to not use the list.hashCode method inside
 * this hashCode method because they take very long and slow down the algorithms. A check of the length of the list is better here</p>
 * <p>
 * Created by Michael Menzel on 23/2/16.
 */
public interface  Track extends Serializable, Cloneable {

    long serialVersionUID = 606249588L;

    Genome.Assembly getAssembly();

    String getCellLine();

    String getDescription();

    String getName();

    long[] getStarts();

    long[] getEnds();

    int getUid();

    int getDbid();

    String getPack();

    Track clone();

    int hashCode();

    boolean equals(Object o);

    String toString();

    String getSource();

    String getSourceurl();
}
