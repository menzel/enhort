package de.thm.logo;

import de.thm.positionData.Sites;
import de.thm.positionData.UserData;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Test Genome class to get sequences by positions
 *
 * Created by menzel on 11/8/16.
 */
public class GenomeTest {
    @Test
    public void getPositions() throws Exception {
        GenomeFactory genome = GenomeFactory.getInstance();
        String motif = "TATA";

        List<Long> pos = genome.getPositions(GenomeFactory.Assembly.hg19, motif, 1000);
        List<String> seq = genome.getSequence(GenomeFactory.Assembly.hg19, pos, 5, Integer.MAX_VALUE);

        assert seq != null;
        seq.forEach(s -> assertTrue(s.contains("TATA")));
    }


    @Test
    public void getSequence_another() throws Exception {

        GenomeFactory genome = GenomeFactory.getInstance(); //new Genome(new File("/home/menzel/Desktop/chromosomes").toPath());
        Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/enhort/sleeping_beauty.hg19.bed").toPath());

        List<String> seq = genome.getSequence(GenomeFactory.Assembly.hg19, userDat, 8, Integer.MAX_VALUE);

        assert seq != null;

        for(String s: seq){
            s = s.toLowerCase();
            if(!s.contains("ta")) //comparing sb with known TA motif
                System.err.println("Sequence was " + s);
            assertTrue(s.contains("ta"));

        }

    }

    @Test
    public void getSequence() throws Exception {

        GenomeFactory genome = GenomeFactory.getInstance();

          Sites sites =  new Sites() {
             @Override
             public void addPositions(Collection<Long> values) {

             }

             @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                // https://genome.ucsc.edu/cgi-bin/das/hg19/dna?segment=chr1:817942,817946
                sites.add(10002L); // nta|acc
                sites.add(817944L); // ca|taa
                sites.add(820981L); // tta
                sites.add(943737L);
                sites.add(987788L); // tgtat


                return sites;

            }

             @Override
             public void setPositions(List<Long> positions) {

             }

             @Override
             public int getPositionCount() {
                 return 0;
             }
         };

        List<String> seq = genome.getSequence(GenomeFactory.Assembly.hg19, sites, 5, Integer.MAX_VALUE);


        assert seq != null;
        assertTrue(seq.get(0).contains("taac"));
        assertTrue(seq.get(1).contains("ATAA"));
        assertTrue(seq.get(2).contains("tta"));
        assertTrue(seq.get(3).contains("tatg"));
        assertTrue(seq.get(4).contains("gtat"));


    }

}