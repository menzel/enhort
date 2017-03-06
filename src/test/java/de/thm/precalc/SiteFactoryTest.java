package de.thm.precalc;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.logo.LogoCreator;
import de.thm.positionData.Sites;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by menzel on 2/9/17.
 */
public class SiteFactoryTest {

    private static SiteFactory factory;

    @BeforeClass
    public static void init(){

        Path path = new File("/home/menzel/Desktop/THM/lfba/enhort/dat/hg19/inout/knownGenes.bed").toPath();

        try {
            TrackFactory.getInstance().loadTrack(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        factory = new SiteFactory(GenomeFactory.Assembly.hg19, 100000);

    }

    private static InOutTrack mockTrack(List<Long> start, List<Long> end, String name) {

        return  TrackFactory.getInstance().createInOutTrack(start, end, name, "desc", GenomeFactory.Assembly.hg19);
    }

    private static ScoredTrack mockTrack(List<Long> start, List<Long> end, List<Double> scores, String name) {

        return  TrackFactory.getInstance().createScoredTrack(start, end, null, scores,name, "desc", GenomeFactory.Assembly.hg19, null);
    }

    @Test
    public void score() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("CCAT");
        sequences.add("CTAT");
        sequences.add("CTAA");
        sequences.add("CCAT");

        Logo logo = LogoCreator.createLogo(sequences);


        List<String> seq = new ArrayList<>();
        seq.add("CCAT");
        seq.add("GGGG");
        seq.add("GGAG");

        Map<String,Double> results =  factory.calculateScores(logo, seq);

        assertEquals(1.0, results.get("CCAT"), 0.1);
        assertEquals(0.0, results.get("GGGG"), 0.1);
        assertEquals(.1, results.get("GGAG"), 0.1);

        List<String> sequences2 = new ArrayList<>();
        sequences2.add("TA");
        sequences2.add("TA");

        Logo logo2 = LogoCreator.createLogo(sequences2);
        assertEquals(1., factory.score(logo2, "TA"), 0.1);
    }

    @Test
    public void testAllSites() throws Exception {}

    @Test
    public void getSites() throws Exception {
        InOutTrack track = (InOutTrack) TrackFactory.getInstance().getTracks(GenomeFactory.Assembly.hg19).get(0);

        int in = 50;
        int out = 100;

        if(track == null)
            return;

        List<Long> pos = factory.getSites(track, in, out).getPositions();

        Sites sites = new Sites() {
            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {
                return pos;
            }

            @Override
            public void setPositions(List<Long> positions) {}

            @Override
            public List<Character> getStrands() {
                return null;
            }

            @Override
            public int getPositionCount() {
                return in+out;
            }

            @Override
            public GenomeFactory.Assembly getAssembly() {
                return GenomeFactory.Assembly.hg19;
            }
        };

        Intersect<InOutTrack> intersect = new Intersect<>();

        TestTrackResult result = intersect.searchSingleInterval(track, sites);

        assertEquals(result.getIn(), in);
        assertEquals(result.getOut(), out);
    }

    @Test
    public void getByLogo() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("AATT");
        sequences.add("AACT");
        sequences.add("AATT");
        sequences.add("AATT");
        sequences.add("AATG");

        Logo logo = LogoCreator.createLogo(sequences);

        List<Long> sites = factory.getByLogo(logo, 100).getPositions();
        List<String> seq = GenomeFactory.getInstance().getSequence(GenomeFactory.Assembly.hg19, sites,4, Integer.MAX_VALUE);
        Logo newLogo = LogoCreator.createLogo(seq);


        System.out.println(newLogo.getConsensus());

        assertEquals(newLogo.getConsensus(), "AATT");
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

        List<Long> sites = factory.getByLogo(logo, 10).getPositions();
        List<String> seq = GenomeFactory.getInstance().getSequence(GenomeFactory.Assembly.hg19, sites,4, Integer.MAX_VALUE);

        assert seq != null;
        assertEquals(seq.size(), 10);

        Logo newLogo = LogoCreator.createLogo(seq);

        //compare newLogo and logo

        //System.out.println(logo.getConsensus());
        //System.out.println(newLogo.getConsensus());
    }
}