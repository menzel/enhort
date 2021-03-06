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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Results of one intersect run with the count of inside values. And depening on the interval type result names and result scores.
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
public final class TestTrackResult implements Serializable {


    private final Map<String, Integer> resultNames;
    private final List<Double> resultScores;
    private final Track usedInterval;
    private final int in;
    private final int out;


    /**
     * Constructor
     */
    public TestTrackResult(Track usedInterval, int in, int out) {
        this.usedInterval = usedInterval;
        this.in = in;
        this.out = out;
        this.resultNames = null;
        this.resultScores = null;
    }

    /**
     * Constructor
     */
    public TestTrackResult(Track usedInterval, int in, int out, Map<String, Integer> names) {
        this.usedInterval = usedInterval;
        this.in = in;
        this.out = out;
        this.resultNames= names;
        this.resultScores = null;
    }

    /**
     * Constructor
     */
    public TestTrackResult(Track usedInterval, int in, int out, List<Double> scores) {
        this.usedInterval = usedInterval;
        this.in = in;
        this.out = out;
        this.resultNames = null;
        this.resultScores = scores;
    }


    public Map<String, Integer> getResultNames() {
        return resultNames;
    }


    public List<Double> getResultScores() {
        return resultScores;
    }

    @Override
    public String toString() {
        return usedInterval.getName() + "\t" + in + "\t" + out;
    }

    public int getIn() {
        return in;
    }

    public int getOut() { return this.out; }

    public Class getType() {
        return getClass();
    }

    public Track getUsedTrack() {
        return usedInterval;
    }
}

