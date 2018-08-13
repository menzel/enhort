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

import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.Track;

/**
 * Interface for statistical test evaluation
 *
 * Created by Michael Menzel on 1/3/16.
 */
public interface Test<T extends Track>{

    TestResult test(TestTrackResult testTrackResultA, TestTrackResult testTrackResultB, T track);
}
