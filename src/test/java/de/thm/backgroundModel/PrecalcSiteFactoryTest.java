package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.positionData.Sites;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by menzel on 12/6/16.
 */
public class PrecalcSiteFactoryTest {

    @BeforeClass
    public static void prepare(){
        TrackFactory.getInstance().loadTracks();
        PrecalcSiteFactory.generatePositions(GenomeFactory.Assembly.hg19, 1000);
    }

    @Test
    public void getSitesCount() throws Exception {
        assertEquals(50, PrecalcSiteFactory.getSites(50).getPositionCount());
    }

    @Test
    public void getSitesByBoolean() throws Exception {
        Sites sites = PrecalcSiteFactory.getSites(PrecalcSiteFactory.boolean_keys.GENE, 100);

        Intersect<InOutTrack> calc = new Intersect<>();
        TestTrackResult result = calc.searchSingleInterval((InOutTrack) TrackFactory.getInstance().getTrackByName("Known genes"),sites);

        assertEquals(100, result.getIn());

    }

    @Test
    public void getSitesByDistance() throws Exception {
        throw new AssertionError("Not defined yet");
    }

    @Test
    public void getSitesByLogo() throws Exception {

        //mock logo
        Logo logo = new Logo();

        Map<String, Double> a = new HashMap<>();
        Map<String, Double> b = new HashMap<>();

        a.put("T",1d);
        a.put("A",0.5);
        b.put("A",2d);

        logo.add(a);
        logo.add(b);


        Sites sites = PrecalcSiteFactory.getSites(logo, 0, 100);
        List<String> seq = GenomeFactory.getInstance().getSequence(GenomeFactory.Assembly.hg19, sites,4, Integer.MAX_VALUE);

        assert seq != null;

        assertEquals(seq.size(), 100);
        char[] consensus = new char[seq.get(0).length()];

        for(int i = 0; i < seq.get(0).length();i++){

            Map<Character, Integer> counter = new HashMap<>();

            counter.put('a', 0);
            counter.put('t', 0);
            counter.put('c', 0);
            counter.put('g', 0);

            for(String s: seq){
                Character c = s.toLowerCase().charAt(i);
                counter.put(c, counter.get(c)+1);
            }

            int max = 0;
            char max_char = '.';
            for(Character c: counter.keySet()){
                if(counter.get(c) > max){
                    max = counter.get(c);
                    max_char = c;
                }
            }

            consensus[i] = max_char; //get the char with the max value TODO
        }

        //compare consensus of new positions with logo
        if(!Arrays.toString(consensus).contains("ta")){
            throw new AssertionError(Arrays.toString(consensus) + " does not contain  " +  "ta");

        }
    }

}