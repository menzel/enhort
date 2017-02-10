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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        factory = new SiteFactory(GenomeFactory.Assembly.hg19, 2000);

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

        assertEquals(1., factory.score(logo, "CCAT"), 0.2);

        assertEquals(.5d, factory.score(logo, "GGGG"), 0.2);

        assertEquals(.2, factory.score(logo, "GGAG"), 0.2);
    }

    @Test
    public void testAllSites() throws Exception {}

    @Test
    public void getSites() throws Exception {
        InOutTrack track = (InOutTrack) TrackFactory.getInstance().getTracks(GenomeFactory.Assembly.hg19).get(0);

        int in = 50;
        int out = 100;

        List<Long> pos = factory.getSites(track, in, out);

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
            public int getPositionCount() {
                return in+out;
            }

            @Override
            public GenomeFactory.Assembly getAssembly() {
                return GenomeFactory.Assembly.hg19;
            }
        };

        assert track != null;

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

        factory.getByLogo(logo, 100);
    }



}