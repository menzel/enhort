package de.thm.calc;

import de.thm.genomeData.Track;
import de.thm.misc.LogoCreator;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by menzel on 11/8/16.
 */
public class GenomeTest {


    @Test
    public void getSequence_another() throws Exception {

        GenomeFactory genome = GenomeFactory.getInstance(); //new Genome(new File("/home/menzel/Desktop/chromosomes").toPath());
        //Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/enhort/sleeping_beauty.hg19.bed").toPath());
        Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/enhort/HIV-hg19.bed").toPath());

        List<String> seq = genome.getSequence(Track.Assembly.hg19, userDat, 8, Integer.MAX_VALUE);

        //seq.forEach(System.out::println);
        System.out.println(LogoCreator.createLogo(seq));

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
                 /*
                 sites.add(14L);
                 sites.add(98L);
                 sites.add(102L);
                */

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

        List<String> seq = genome.getSequence(Track.Assembly.hg19, sites, 5, Integer.MAX_VALUE);

        //seq.forEach(System.out::println);

        assertTrue(seq.get(0).contains("taac"));
        assertTrue(seq.get(1).contains("ATAA"));
        assertTrue(seq.get(2).contains("tta"));
        assertTrue(seq.get(3).contains("tatg"));
        assertTrue(seq.get(4).contains("gtat"));


    }

}