package de.thm.calc;

import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.StrandTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test intersect alg
 *
 * Created by menzel on 2/6/17.
 */
public class IntersectTest {
    private static Sites sites;
    private static InOutTrack iotrack;
    private static ScoredTrack sctrack;
    private static StrandTrack sttrack;

    private static InOutTrack mockTrack(List<Long> start, List<Long> end) {

        return  TrackFactory.getInstance().createInOutTrack(start, end, "name", "desc", GenomeFactory.Assembly.hg19);
    }

    private static ScoredTrack mockTrack(List<Long> start, List<Long> end, List<Double> scores) {

        return  TrackFactory.getInstance().createScoredTrack(start, end, null, scores,"name", "desc");
    }

    private static StrandTrack mockTrack(List<Long> start, List<Long> end, List<Character> strands, String foo) {
        return  TrackFactory.getInstance().createStrandTrack(start, end, strands, "name", foo, GenomeFactory.Assembly.hg19, null);

    }

    @BeforeClass
    public static void prepare(){

        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(1L);
        start1.add(2L);
        start1.add(20L);
        start1.add(50L);

        end1.add(10L);
        end1.add(4L);
        end1.add(30L);
        end1.add(60L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end2.add(15L);
        end2.add(40L);
        end2.add(60L);


        List<Double> scores1 = new ArrayList<>();

        scores1.add(0.5);
        scores1.add(0.2);
        scores1.add(0.7);
        scores1.add(0.1);

        List<Character> strands = new ArrayList<>();

        strands.add('-');
        strands.add('+');
        strands.add('+');
        strands.add('-');



        sctrack = mockTrack(start1, end1, scores1);
        iotrack = mockTrack(start2, end2);
        sttrack = mockTrack(start1,end1, strands, "needed to prevent clash with other mockTrack method");


        sites = new Sites() {
            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                sites.add(1L);
                sites.add(4L);
                sites.add(5L);
                sites.add(6L);
                sites.add(14L);
                sites.add(44L);
                sites.add(52L);
                sites.add(61L);
                sites.add(191L);
                sites.add(200L);

                return sites;

            }

            @Override
            public void setPositions(List<Long> positions) {}

            @Override
            public List<Character> getStrands() {
                List<Character> strands = new ArrayList<>();

                strands.add('+');
                strands.add('+');
                strands.add('+');
                strands.add('+');
                strands.add('+');
                strands.add('-');
                strands.add('-');
                strands.add('-');
                strands.add('-');
                strands.add('-');

                return strands;
            }

            @Override
            public int getPositionCount() {
                return 10;
            }

            @Override
            public GenomeFactory.Assembly getAssembly() {
                return GenomeFactory.Assembly.hg19;
            }
        };


    }

    @Test
    public void searchSingleInterval() throws Exception {

    }

    @Test
    public void searchSingleIntervalInOut() throws Exception {

        Intersect<InOutTrack> sect = new Intersect<>();
        TestTrackResult result = sect.searchSingleInterval(iotrack,sites);

        assertEquals(4, result.getIn());
        assertEquals(6, result.getOut());

    }


    @Test
    public void searchStrandTrack() throws Exception {

        Intersect<StrandTrack> sect = new Intersect<>();
        TestTrackResult result = sect.searchSingleInterval(sttrack,sites);

        assertEquals(1, result.getIn());
        assertEquals(9, result.getOut());

    }



    @Test
    public void searchScoredTrack() throws Exception {

        Intersect<ScoredTrack> sect = new Intersect<>();
        TestTrackResult result = sect.searchSingleInterval(sctrack,sites);

        List<Double> scores = new ArrayList<>();

        scores.add(.5);
        scores.add(.5);
        scores.add(.5);
        scores.add(.5);
        scores.add(.1);

        assertEquals(5, result.getIn());
        assertEquals(5, result.getOut());
        assertEquals(scores, result.getResultScores());
    }
}