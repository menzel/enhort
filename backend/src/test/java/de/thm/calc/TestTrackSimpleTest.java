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
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 15/12/15.
 */
public class TestTrackSimpleTest {

    TestTrack<InOutTrack> testTrack = new Intersect<>();
    InOutTrack intv;

    @Before
    public void setupIntv() throws Exception {
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();
        long offset = chrSizes.offset(Genome.Assembly.hg19, "chr4");

        ArrayList<Long> startList = new ArrayList<>();
        ArrayList<Long> endList = new ArrayList<>();

        startList.add(5L);
        startList.add(10L);
        startList.add(20L);

        endList.add(7L);
        endList.add(15L);
        endList.add(22L);

        startList.add(5L + offset);
        startList.add(10L + offset);
        startList.add(20L + offset);

        endList.add(7L + offset);
        endList.add(15L + offset);
        endList.add(22L + offset);

        intv = TrackFactory.getInstance().createInOutTrack(startList, endList, "testtrack", "no desc", Genome.Assembly.hg19);

    }


    @Test
    public void testLastInterval() throws Exception {

        Sites sites =  new Sites() {
            @Override
            public void addPositions(Collection<Long> values) {

            }

            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                ChromosomSizes chrSizes = ChromosomSizes.getInstance();
                long offset = chrSizes.offset(Genome.Assembly.hg19, "chr4");

                sites.add(1L);
                sites.add(4L);
                sites.add(5L);
                sites.add(6L);
                sites.add(1L + offset);
                sites.add(4L + offset);
                sites.add(5L + offset);
                sites.add(6L + offset);
                sites.add(19 + offset);
                sites.add(20 + offset);


                return sites;

            }

            @Override
            public List<Character> getStrands() {
                return null;
            }

            @Override
            public void setPositions(List<Long> positions) {

            }

            @Override
            public int getPositionCount() {
                return 0;
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

        TestTrackResult testTrackResult = testTrack.searchTrack(intv,sites);
        assertEquals(5, testTrackResult.getIn());

        assertEquals(5, testTrackResult.getOut());
    }


    @Test
    public void testOffsetSearch() throws Exception {

        Sites sites =  new Sites() {
            @Override
            public void addPositions(Collection<Long> values) {

            }

            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                ChromosomSizes chrSizes = ChromosomSizes.getInstance();
                long offset = chrSizes.offset(Genome.Assembly.hg19, "chr4");

                sites.add(1L);
                sites.add(4L);
                sites.add(5L);
                sites.add(6L);
                sites.add(1L + offset);
                sites.add(4L + offset);
                sites.add(5L + offset);
                sites.add(6L + offset);


                return sites;

            }

            @Override
            public List<Character> getStrands() {
                return null;
            }

            @Override
            public void setPositions(List<Long> positions) {

            }

            @Override
            public int getPositionCount() {
                return 0;
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

        TestTrackResult testTrackResult = testTrack.searchTrack(intv,sites);
        assertEquals(4, testTrackResult.getIn());

        assertEquals(4, testTrackResult.getOut());
    }


    @Test
    public void testSearchSingleIntervall() throws Exception {

        Sites sites =  new Sites() {
            @Override
            public void addPositions(Collection<Long> values) {

            }

            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                sites.add(1L); // out
                sites.add(4L); // out

                sites.add(5L);
                sites.add(6L);

                sites.add(8L); // out

                sites.add(21L);
                sites.add(22L); // out
                sites.add(22L); // out

                sites.add(23L); // out
                sites.add(24L); // out
                sites.add(24L); // out
                sites.add(26L); // out
                sites.add(128L); // out

                return sites;

            }

            @Override
            public void setPositions(List<Long> positions) {

            }

            @Override
            public List<Character> getStrands() {
                return null;
            }

            @Override
            public int getPositionCount() {
                return 0;
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

        TestTrackResult testTrackResult = testTrack.searchTrack(intv,sites);

        assertEquals(10, testTrackResult.getOut());

        assertEquals(3, testTrackResult.getIn());

    }
}