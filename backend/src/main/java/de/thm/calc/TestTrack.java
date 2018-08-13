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
package de.thm.calc;

import de.thm.genomeData.tracks.Track;
import de.thm.positionData.Sites;

/**
 * Calculates the intersect between an interval and some points. Handles in/out count, names and scores
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
public interface TestTrack<T extends Track> {

    /**
     * Calculates the intersect between an interval and some points. Handles in/out count, names and scores.
     *
     * @param track - interval to find positions
     * @param sites - positions to find
     *
     * @return Result which contains the in/out count, names or scores
     */
    TestTrackResult searchTrack(T track, Sites sites);

}
