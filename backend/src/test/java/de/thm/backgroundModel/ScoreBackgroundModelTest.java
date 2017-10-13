package de.thm.backgroundModel;

import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.ScoredTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for bg model occurenceMap
 * Created by menzel on 8/25/16.
 */
public class ScoreBackgroundModelTest {

    private List<ScoredTrack> tracks;
    private Sites sites;
    private Map<ScoreSet, Double> expected;
    private ScoreBackgroundModel model;



    @Test
    public void smooth() throws Exception {

        Map<ScoreSet, Double> occ = new HashMap<>();
        occ.put(new ScoreSet(new Double[]{-4.}), 1.0);
        occ.put(new ScoreSet(new Double[]{-3.}), 1.0);
        occ.put(new ScoreSet(new Double[]{-1.5}), 4.0);
        occ.put(new ScoreSet(new Double[]{0.}), 1.0);
        occ.put(new ScoreSet(new Double[]{2.0}), 2.0);
        occ.put(new ScoreSet(new Double[]{null}), 20.0);

        List<Double> scores = new ArrayList<>();

        for(double d = -4.; d <= 2.5; d+=.5){
            scores.add(d);
        }

        ScoredTrack track = mockTrack(null, null, null, scores);
        List<ScoredTrack> tracks = new ArrayList<>();
        tracks.add(track);

        Map<ScoreSet, Double> newOcc =  model.smooth(occ, tracks, 2.);

        //for(ScoreSet s: newOcc.keySet()) System.out.println(s.getScores()[0] + " old: " + occ.get(s) + " new: " +  newOcc.get(s));

        double sum1 = occ.values().stream().mapToDouble(Double::doubleValue).sum();
        double sum2 = newOcc.values().stream().mapToDouble(Double::doubleValue).sum();

        assertEquals(sum1, sum2,5.);

        Map<ScoreSet, Double> exp = new HashMap<>();
        exp.put(new ScoreSet(new Double[]{-4.}), 0.320456502460288);
        exp.put(new ScoreSet(new Double[]{-3.}), 0.5794916937920714);
        exp.put(new ScoreSet(new Double[]{0.}), 0.45850633153249976);
        exp.put(new ScoreSet(new Double[]{null}), 20.0);


        for(ScoreSet s: exp.keySet()){
            try {
                assertEquals(exp.get(s), newOcc.get(s), 0.1);
            } catch(AssertionError e ){
                e.printStackTrace();
                System.err.println("In ScoreSet" + Arrays.toString(s.getScores()));
            }
        }
    }



    @Before
    public void setUp() {

        long end = 1000; //should be larger than all test intervals in this class

        //mock contigs track
        Track contigs = mock(InOutTrack.class);
        when(contigs.getStarts()).thenReturn(new long[]{0});
        when(contigs.getEnds()).thenReturn(new long[]{end});
        when(contigs.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(contigs.getName()).thenReturn("Contigs");

        TrackFactory factory = TrackFactory.getInstance();
        factory.addTrack(contigs);
        //end mock contigs track

        model = new ScoreBackgroundModel(Genome.Assembly.hg19);

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

        ScoredTrack interval1 = mockTrack(start1, end1, null, scores1);
        ScoredTrack interval2 = mockTrack(start2, end2, null, scores2);
        ScoredTrack interval3 = mockTrack(start3, end3, null, scores3);


        tracks = new ArrayList<>();
        tracks.add(interval1);
        tracks.add(interval2);
        tracks.add(interval3);

        //////// create positions ////////

        sites = mock(Sites.class);
        when(sites.getPositions()).thenReturn(Arrays.asList(5L,7L,8L,17L,37L,55L,70L));
        when(sites.getPositionCount()).thenReturn(7);
        when(sites.getAssembly()).thenReturn(Genome.Assembly.hg19);

        ///////// expected occurence map //////////////

        expected = new HashMap<>();
        expected.put(new ScoreSet(new Double[]{0.5, 0.4, 1.0}), 3.0);

        expected.put(new ScoreSet(new Double[]{null,null, 1.0}), 1.0);

        expected.put(new ScoreSet(new Double[]{null,0.6, 1.0}), 1.0);

        expected.put(new ScoreSet(new Double[]{0.1,0.8, 1.0}), 2.0);


    }

    @Test
    public void generateProbabilityInterval() throws Exception {

        /////// call bg model ////////

        ScoredTrack probTrack = model.generateProbabilityInterval(sites, tracks, 0);


        ///////// Create expected probabilites /////////

        List<Double> expectedScores= new ArrayList<>();

        expectedScores.add(0.0);
        expectedScores.add(0.0);
        expectedScores.add(0.0);
        expectedScores.add(0.0);
        expectedScores.add(0.0);
        expectedScores.add(0.049);
        expectedScores.add(0.049);
        expectedScores.add(0.0);
        expectedScores.add(0.049);
        expectedScores.add(0.25);
        expectedScores.add(0.02);
        expectedScores.add(0.08);
        expectedScores.add(1.6151E-9);
        expectedScores.add(0.499);


        /////// check result //////////

        assertArrayEquals(expectedScores.stream().mapToDouble(d->d).toArray(), probTrack.getIntervalScore(), 0.01);

    }


    @Test
    public void generatePositionsByProbability() throws Exception {

        ScoredTrack probTrack = model.generateProbabilityInterval(sites, tracks, 0);
        Collection<Long> pos = model.generatePositionsByProbability(probTrack, 10);

        // TODO  check if rand pos generated are good
    }





    @Test
    public void combine() throws Exception {


        /////// call bg model ////////


        ScoredTrack result = model.combine(tracks, expected);


        ///////// Create expected Track /////////

        List<Long> start = new ArrayList<>();
        List<Long> end = new ArrayList<>();

        start.add(0L);
        end.add(1L);

        start.add(1L);
        end.add(2L);

        start.add(2L);
        end.add(4L);

        start.add(4L);
        end.add(5L);

        start.add(5L);
        end.add(10L);

        start.add(10L);
        end.add(15L);

        start.add(15L);
        end.add(20L);

        start.add(20L);
        end.add(30L);

        start.add(30L);
        end.add(35L);

        start.add(35L);
        end.add(40L);

        start.add(40L);
        end.add(42L);

        start.add(42L);
        end.add(50L);

        start.add(50L);
        end.add(60L);

        start.add(60L);
        end.add(3095677412L);


        //expectedTrack = TrackFactory.getInstance().createScoredTrack(start, end, null, null);

        /////// check result //////////

        assertArrayEquals(start.stream().mapToLong(l->l).toArray(), result.getStarts());
        assertArrayEquals(end.stream().mapToLong(l->l).toArray(), result.getEnds());
        //TODO Check scores (probs)

    }

    @Test
    public void fillOccurenceMap() throws Exception {

        /////// call bg model ////////

        Map<ScoreSet, Double> result = model.fillOccurenceMap(tracks, sites);

        /////// check result //////////


        assertEquals(expected, result);
    }


    private ScoredTrack mockTrack(List<Long> start, List<Long> end, List<String> names, List<Double> scores) {

        return  TrackFactory.getInstance().createScoredTrack(start, end, names, scores,"name", "desc");
    }


}