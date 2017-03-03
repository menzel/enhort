package de.thm.calc;

import de.thm.genomeData.ScoredTrack;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by menzel on 2/21/17.
 */
public class HotspotTest {
    @Test
    public void findHotspots() throws Exception {

        Sites sites = new Sites() {

            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {
                List<Long> l = new ArrayList<>();

                l.add(5L);
                l.add(7L);
                l.add(8L);
                l.add(37L);

                l.add(70L);
                l.add(71L);
                l.add(73L);
                l.add(74L);
                l.add(78L);

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
            public GenomeFactory.Assembly getAssembly() {
                return GenomeFactory.Assembly.hg19;
            }

        };


        Hotspot hotspot = new Hotspot();


        ScoredTrack track = hotspot.findHotspots(sites, 10);
        System.out.println(track.getIntervalScore());
        //TODO assert



    }

}