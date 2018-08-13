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

import de.thm.genomeData.tracks.InOutTrack;
import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;

public class PCATest {
    @Test
    public void createBedPCA() throws Exception {

        ResultCollector collector = new ResultCollector(null, null, Collections.emptyList());

        for (int i = 0; i < 31; i++) {

            double x = i * Math.PI;
            InOutTrack tmp = mock(InOutTrack.class);
            TestResult ts = new TestResult(0, new TestTrackResult(tmp, 0, 1), new TestTrackResult(tmp, 1, 0), x, tmp);
            collector.addResult(ts);
        }

        PCA pca = new PCA();
        pca.createBedPCA(collector);

        //TODO assert

    }

}