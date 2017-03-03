package de.thm.calc;

import de.thm.genomeData.DistanceTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test Distance calc
 *
 * Created by menzel on 9/19/16.
 */
public class DistancesTest {
    @Test
    public void searchTrack() throws Exception {

        //create data
        ArrayList<Long> startList = new ArrayList<>();

        startList.add(5L);
        startList.add(10L);
        startList.add(20L);

        DistanceTrack track = TrackFactory.getInstance().createDistanceTrack(startList, "testtrack", "no desc", GenomeFactory.Assembly.hg19);

        Sites sites =  new Sites() {

            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                sites.add(1L);
                sites.add(7L);
                sites.add(10L);
                sites.add(12L);
                sites.add(19L);
                sites.add(25L);

                return sites;

            }

            @Override
            public void setPositions(List<Long> positions) {}

            @Override
            public List<Character> getStrands() {
                return null;
            }

            @Override
            public int getPositionCount() {
                return 0;
            }

            @Override
            public GenomeFactory.Assembly getAssembly() {
                return GenomeFactory.Assembly.hg19;
            }
        };


        // run distances
        Distances distances = new Distances();
        TestTrackResult result = distances.searchTrack(track, sites);

        // create expected data

        ArrayList<Double> expected = new ArrayList<>();

        expected.add(4.);
        expected.add(2.);
        expected.add(0.);
        expected.add(2.);
        expected.add(-1.);
        expected.add(5.);

        assertEquals(expected, result.getResultScores());



    }

}