package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Michael Menzel on 1/2/16.
 */
public class BetterBackgroundModelTest {

    @Test
    public void testRandPositionsScored() throws Exception {
        ArrayList<Long> startList = new ArrayList<>();
        ArrayList<Long> endList = new ArrayList<>();

        startList.add(5L);
        startList.add(20L);
        startList.add(50L);
        startList.add(80L);

        endList.add(15L);
        endList.add(30L);
        endList.add(80L);
        endList.add(90L);

        InOutTrack track = mockInterval(startList,endList);




        Sites sites = new Sites() {
            List<Long> positions = new ArrayList<>();

            @Override
            public void addPositions(Collection<Long> values) {
                this.positions.addAll(values);
            }

            @Override
            public List<Long> getPositions() {
                return this.positions;
            }

            @Override
            public void setPositions(List<Long> positions) {
                this.positions = positions;

            }

            @Override
            public int getPositionCount() {
                return this.positions.size();
            }
        };


        List<Long> positions = new ArrayList<>();

        positions.add(1L);
        positions.add(10L);
        positions.add(12L);
        positions.add(22L);
        positions.add(35L);
        positions.add(60L);
        positions.add(70L);
        positions.add(100L);

        sites.setPositions(positions);

        SingleTrackBackgroundModel better = new SingleTrackBackgroundModel(track , sites, positions.size());

        Intersect sect = new Intersect<>();

        TestTrackResult set = sect.searchSingleInterval(track,sites);
        TestTrackResult gen = sect.searchSingleInterval(track,better);


        assertEquals(set.getOut(),gen.getOut());
        assertEquals(set.getIn(),gen.getIn());

    }

        private InOutTrack mockInterval(List<Long> start, List<Long> end) {

            return TrackFactory.getInstance().createInOutTrack(start,end,"test", "test track");
    }


}