package de.thm.backgroundModel;

import de.thm.calc.Distances;
import de.thm.genomeData.DistanceTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Testing Distance bg model with three tracks
 *
 * Created by menzel on 10/10/16.
 */
public class DistanceBackgroundModelTest {

    @Test
    public void getPositions() throws Exception {

        List<Long> start1 = new ArrayList<>();


        start1.add(1L);
        start1.add(2L);
        start1.add(20L);
        start1.add(50L);


        DistanceTrack track1 = mockTrack(start1);

        List<Track> tracks;

        tracks = new ArrayList<>();
        tracks.add(track1);

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
            public int getPositionCount() {
                return 5;
            }

        };

        DistanceBackgroundModel model = new DistanceBackgroundModel(tracks,sites);
        Distances dist = new Distances();

        ///////// compare with expected results //////////////

        List<Long> expected = dist.distancesToNext(track1, sites);

        for(Long d: dist.distancesToNext(track1, model)){

            try {
                assertEquals(true, expected.contains(d));

            } catch (AssertionError e){ //catch and throw error again with extended error message

                String message = expected.toString() + model.getPositions().toString() + e;
                throw new AssertionError(message);
            }
        }

    }

    private DistanceTrack mockTrack(List<Long> start) {
        return TrackFactory.getInstance().createDistanceTrack(start, "Test track", "no desc");
    }

}