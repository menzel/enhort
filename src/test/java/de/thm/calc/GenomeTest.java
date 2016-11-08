package de.thm.calc;

import de.thm.positionData.Sites;
import de.thm.positionData.UserData;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by menzel on 11/8/16.
 */
public class GenomeTest {


    @Test
    public void getSequence_another() throws Exception {

        Genome genome = new Genome(new File("/home/menzel/Desktop/chromosomes").toPath());
        Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/enhort/sleeping_beauty.hg19.bed").toPath());

        List<String> seq = genome.getSequence(userDat, 4);

        seq.forEach(System.out::println);

    }

    @Test
    public void getSequence() throws Exception {

        Genome genome = new Genome(new File("/home/menzel/Desktop/chromosomes").toPath());

          Sites sites =  new Sites() {
             @Override
             public void addPositions(Collection<Long> values) {

             }

             @Override
            public List<Long> getPositions() {

                List<Long> sites = new ArrayList<>();

                sites.add(120L);
                sites.add(4200L);
                sites.add(46000L);
                sites.add(530000L);
                sites.add(5400000L);
                sites.add(60000000L);

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

        genome.getSequence(sites, 5);

    }

}