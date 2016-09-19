package de.thm.calc;

import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
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

        Track track = TrackFactory.getInstance().createInOutTrack(startList, null, "testtrack", "no desc");

        Sites sites =  new Sites() {

            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

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
            public int getPositionCount() {
                return 0;
            }
        };


        // run distances
        Distances distances = new Distances();
        TestTrackResult result = distances.searchTrack((InOutTrack) track, sites);

        // create expected data

        ArrayList<Double> expected = new ArrayList<>();

        expected.add(2.);
        expected.add(0.);
        expected.add(2.);
        expected.add(1.);
        expected.add(5.);

        assertEquals(expected, result.getResultScores());



    }

}