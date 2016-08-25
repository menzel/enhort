package de.thm.backgroundModel;

import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by menzel on 8/25/16.
 */
public class SecondScoreMultiTrackBackgroundModelTest {
    @Test
    public void generateProbabilityInterval() throws Exception {

    }

    @Test
    public void fillOccurenceMap() throws Exception {


        ///// Create Tracks /////////

        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();
        List<Long> start3 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();
        List<Long> end3 = new ArrayList<>();

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


        start3.add(4L);
        end3.add(42L);

        List<Double> scores1 = new ArrayList<>();
        List<Double> scores2 = new ArrayList<>();
        List<Double> scores3 = new ArrayList<>();

        scores1.add(0.5);
        scores1.add(0.2);
        scores1.add(0.7);
        scores1.add(0.1);

        scores2.add(0.4);
        scores2.add(0.6);
        scores2.add(0.8);

        scores3.add(1.);

        ScoredTrack interval1 = mockInterval(start1, end1, null, scores1);
        ScoredTrack interval2 = mockInterval(start2, end2, null, scores2);
        ScoredTrack interval3 = mockInterval(start3, end3, null, scores3);


        List<ScoredTrack> tracks = new ArrayList<>();
        tracks.add(interval1);
        tracks.add(interval2);
        tracks.add(interval3);

        //////// create positions ////////

        Sites sites = new Sites() {

            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {
                List<Long> l = new ArrayList<>();

                l.add(5L); //.5.4
                l.add(7L); //.5.4
                l.add(8L); //.5.4
                l.add(17L);// ||

                l.add(37L); // .6
                l.add(55L); // .7.8
                l.add(70L); // ||

                return l;
            }

            @Override
            public void setPositions(List<Long> positions) {}

            @Override
            public int getPositionCount() {
                return 7;
            }
        };



        /////// call bg model ////////

        SecondScoreMultiTrackBackgroundModel model = new SecondScoreMultiTrackBackgroundModel();

        Map<ScoreSet, Double> result = model.fillOccurenceMap(tracks, sites);

        /////// check result //////////

        Map<ScoreSet, Double> expected = new HashMap<>();
        expected.put(new ScoreSet(new Double[]{0.5, 0.4, 1.0}), 3.0);

        expected.put(new ScoreSet(new Double[]{null,null, 1.0}), 1.0);

        expected.put(new ScoreSet(new Double[]{null,0.6, 1.0}), 1.0);

        expected.put(new ScoreSet(new Double[]{0.1,0.8, 1.0}), 2.0);

        assertEquals(expected, result);
    }


    private ScoredTrack mockInterval(List<Long> start, List<Long> end, List<String> names, List<Double> scores) {

        return  TrackFactory.getInstance().createScoredTrack(start, end, names, scores,"name", "desc");
    }


}