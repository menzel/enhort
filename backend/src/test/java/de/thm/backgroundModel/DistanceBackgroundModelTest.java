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

import de.thm.calc.Distances;
import de.thm.genomeData.tracks.DistanceTrack;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Testing Distance bg model with three tracks
 *
 * Created by menzel on 10/10/16.
 */
public class DistanceBackgroundModelTest {

    @Test
    public void getPositions() throws Exception {

        List<Long> start1 = new ArrayList<>();



        start1.add(0L);
        start1.add(1L);
        start1.add(2L);
        start1.add(20L);
        start1.add(50L);
        start1.add(100L);


        DistanceTrack track1 = mockTrack(start1);

        //////// create positions ////////

        Sites sites = new Sites() {

            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {
                List<Long> l = new ArrayList<>();

                l.add(5L);// 1:3
                l.add(7L);// 1:5
                l.add(8L);// 1:6

                l.add(37L);// 1:-13
                l.add(70L);// 1:20

                return l;
            }

            @Override
            public void setPositions(List<Long> positions) {}

            @Override
            public List<Character> getStrands() {
                return null;
            }

            @Override
            public int getPositionCount() {
                return 5;
            }

            @Override
            public Genome.Assembly getAssembly() {
                return Genome.Assembly.hg19;
            }

            @Override
            public String getCellline() {
                return null;
            }

        };

        BackgroundModel model = DistanceBackgroundModel.create(track1,sites,1);
        Distances dist = new Distances();

        ///////// compare with expected results //////////////

        List<Long> expected = dist.distancesToNext(track1, sites);

        for(Long d: dist.distancesToNext(track1, model)){

            try {
                boolean any = false;

                //check results with 2 bp window
                for (int i = -2; i <= 3; i++) {
                    if (expected.contains(d - i)) {
                        any = true;
                        break;
                    }
                }
                assertTrue(any);

            } catch (AssertionError e){ //catch and throw error again with extended error message

                String message = "exp: " + expected.toString() + " got: " + d + " for " + dist.distancesToNext(track1, model).toString() + e;
                message += ".\n Since the setting of values is a random process it is possible to fail sometimes to get the expected values.";
                throw new AssertionError(message);
            }
        }

    }

    private DistanceTrack mockTrack(List<Long> start) {
        return TrackFactory.getInstance().createDistanceTrack(start, "Test track", "no desc", Genome.Assembly.hg19);
    }

}