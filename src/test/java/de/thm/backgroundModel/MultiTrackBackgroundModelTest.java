package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrack;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases for multi track bg model
 *
 * Created by Michael Menzel on 13/1/16.
 */
public class MultiTrackBackgroundModelTest {

    @BeforeClass
    public static void setUp(){
        long end = 1000; //should be larger than all test intervals in this class

        //mock contigs track
        Track contigs = mock(InOutTrack.class);
        when(contigs.getStarts()).thenReturn(new long[]{0});
        when(contigs.getEnds()).thenReturn(new long[]{end});
        when(contigs.getAssembly()).thenReturn(GenomeFactory.Assembly.hg19);
        when(contigs.getName()).thenReturn("Contigs");

        TrackFactory factory = TrackFactory.getInstance();
        factory.addTrack(contigs);
        //end mock contigs track
    }

    @Test
    public void testAdvancedBg() throws Exception {
        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();
        List<Long> start3 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();
        List<Long> end3 = new ArrayList<>();

        start1.add(0L);
        start1.add(10L);
        start1.add(30L);
        start1.add(50L);

        end1.add(5L);
        end1.add(15L);
        end1.add(35L);
        end1.add(55L);

        start2.add(0L);
        start2.add(20L);
        start2.add(40L);

        end2.add(5L);
        end2.add(25L);
        end2.add(55L);

        start3.add(0L);
        start3.add(15L);
        start3.add(40L);
        start3.add(50L);

        end3.add(10L);
        end3.add(20L);
        end3.add(45L);
        end3.add(55L);

        Track track1 = mockTrack(start1, end1);
        Track track2 = mockTrack(start2, end2);
        Track track3 = mockTrack(start3, end3);

        List<Track> trackList = new ArrayList<>();

        trackList.add(track1);
        trackList.add(track2);
        trackList.add(track3);

        // positions:

         Sites sites =  new Sites() {
             @Override
             public void addPositions(Collection<Long> values) {

             }

             @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                sites.add(12L);
                sites.add(42L);
                sites.add(46L);
                sites.add(53L);
                sites.add(54L);

                sites.add(60L);
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
             public GenomeFactory.Assembly getAssembly() {
                 return GenomeFactory.Assembly.hg19;
             }
         };


        MultiTrackBackgroundModel model = new MultiTrackBackgroundModel(trackList, sites, sites.getPositionCount());

        //check list count of pos in list 1:
        assertEquals(1,model.getAppearanceTable().getAppearance(trackList.subList(0,1)));

        //check list 2:
        assertEquals(1,model.getAppearanceTable().getAppearance(trackList.subList(1,2)));


        //check count of pos which are in all lists:
        assertEquals(2,model.getAppearanceTable().getAppearance(trackList));

        // check pos in list 2 and 3:
        assertEquals(1,model.getAppearanceTable().getAppearance(trackList.subList(1,3)));

        //check 0 values for other lists:
        assertEquals(0,model.getAppearanceTable().getAppearance(trackList.subList(2,3))); //list 3

        List<Track> zeroList = new ArrayList<>();
        zeroList.add(track1);
        zeroList.add(track3);

        assertEquals(0,model.getAppearanceTable().getAppearance(zeroList));

    }

    @Test
    public void testRandPositions() throws Exception {

        // intervals:

        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        start1.add(20L);
        start1.add(45L);

        start2.add(5L);
        start2.add(35L);
        start2.add(50L);

        end1.add(10L);
        end1.add(30L);
        end1.add(80L);

        end2.add(15L);
        end2.add(40L);
        end2.add(55L);

        Track track1 = mockTrack(start1, end1);
        Track track2 = mockTrack(start2, end2);

        List<Track> trackList = new ArrayList<>();

        trackList.add(track1);
        trackList.add(track2);

        int inFirst = 123;
        int inSecond = 21;
        int inBoth = 110;
        int out = 78;


        // generate custom appearance table
        AppearanceTable app = new AppearanceTable(inFirst + inSecond + out);
        Map<String, Integer> appearance_map = new HashMap<>();
        Set<Integer> containing = new TreeSet<>();

        containing.add(track1.getUid());
        appearance_map.put(app.hash(containing), inFirst);

        containing.add(track2.getUid());
        appearance_map.put(app.hash(containing), inBoth);

        containing.clear();

        containing.add(track2.getUid());
        appearance_map.put(app.hash(containing), inSecond);

        appearance_map.put("[]", out);

        app.setAppearance(appearance_map);


        // generate bg model with the custom appearance table
        MultiTrackBackgroundModel bg = new MultiTrackBackgroundModel();

        bg.addPositions(bg.randPositions(app, trackList));

        // evaluate with intersect
        TestTrack<InOutTrack> calc = new Intersect<>();
        TestTrackResult result1 = calc.searchTrack((InOutTrack) track1, bg);

        // test results

        //check in first:
        assertEquals(inFirst + inBoth, result1.getIn());

        //check in second:
        TestTrackResult result2 = calc.searchTrack((InOutTrack) track2, bg);
        assertEquals(inSecond + inBoth, result2.getIn());

        //check outsiders first:
        assertEquals(inSecond + out, result1.getOut());

        //check outsiders second:
        assertEquals(inFirst + out, result2.getOut());

    }



    @Test
    public void testInfinityTrack() throws Exception {

        // intervals:

        List<Long> start1 = new ArrayList<>();
        List<Long> start2 = new ArrayList<>();

        List<Long> end1 = new ArrayList<>();
        List<Long> end2 = new ArrayList<>();

        start1.add(0L);
        end1.add(10L);

        start1.add(20L);
        end1.add(30L);

        start1.add(45L);
        end1.add(80L);


        start2.add(0L);

        long middle = ChromosomSizes.getInstance().getGenomeSize(GenomeFactory.Assembly.hg19)/2;

        end2.add(middle);
        start2.add(middle+1);
        end2.add(ChromosomSizes.getInstance().getGenomeSize(GenomeFactory.Assembly.hg19));

        Track track1 = mockTrack(start1, end1);
        Track track2 = mockTrack(start2, end2);

        List<Track> trackList = new ArrayList<>();

        trackList.add(track1);
        trackList.add(track2);



         Sites sites =  new Sites() {
             @Override
             public void addPositions(Collection<Long> values) {}

             @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                sites.add(12L);
                sites.add(42L);
                sites.add(46L);
                sites.add(53L);
                sites.add(54L);

                sites.add(60L);
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
                 return 6;
             }

             @Override
             public GenomeFactory.Assembly getAssembly() {
                 return GenomeFactory.Assembly.hg19;
             }
         };


        MultiTrackBackgroundModel model = new MultiTrackBackgroundModel(trackList, sites, sites.getPositionCount());
        //TODO ASSERTS
    }






    private InOutTrack mockTrack(List<Long> start, List<Long> end) {

        return  TrackFactory.getInstance().createInOutTrack(start, end, "name", "desc", GenomeFactory.Assembly.hg19);
    }


}