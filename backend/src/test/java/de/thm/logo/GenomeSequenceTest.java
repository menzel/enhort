// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.logo;

import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Test GenomeSequence class to get sequences by positions
 *
 * Created by menzel on 11/8/16.
 */
public class GenomeSequenceTest {
    @Test
    public void getPositions() throws Exception {
        GenomeFactory genome = GenomeFactory.getInstance();
        String motif = "TATA";

        List<Long> pos = genome.getPositions(Genome.Assembly.hg19, motif, 1000);
        List<String> seq = genome.getSequence(Genome.Assembly.hg19, pos, 5, Integer.MAX_VALUE);

        assert seq != null;
        assertEquals(motif,LogoCreator.createLogo(seq).getConsensus().substring(0,motif.length()));
    }


    public void getSequence_another() throws Exception {

        // TODO Fix getting some sites in here
        GenomeFactory genome = GenomeFactory.getInstance(); //new GenomeSequence(new File("/home/menzel/Desktop/chromosomes").toPath());
        Sites sleepingBeauty = null; //new UserData(Genome.Assembly.hg19, new File("/home/menzel/Desktop/THM/lfba/enhort/sleeping_beauty.hg19.bed").toPath());

        List<String> seqs = genome.getSequence(Genome.Assembly.hg19, sleepingBeauty, 8, Integer.MAX_VALUE);

        //check if there  are any results:
        assertTrue(0 < seqs.stream()
                .map(String::toUpperCase)
                .count());

        // check if there are any sequences with not 'N' in it
        assertTrue(0 < seqs.stream()
                .map(String::toUpperCase)
                .filter(s -> !s.contains("N"))
                .count());

        // filter the remaining sites by TA, there should be none left
        assertEquals(0, seqs.stream()
                .map(String::toUpperCase)
                .filter(s -> !s.contains("N"))
                .filter(s -> !s.contains("TA"))
                .count());
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
              public List<Character> getStrands() {
                  return null;
              }

             @Override
             public int getPositionCount() {
                 return 0;
             }

              @Override
              public Genome.Assembly getAssembly() {
                  return Genome.Assembly.hg19;
              }

              @Override
              public String getCellline() {
                  return null;
              }
          };

        List<String> seq = genome.getSequence(Genome.Assembly.hg19, sites, 5, Integer.MAX_VALUE);


        assert seq != null;
        assertTrue(seq.get(0).contains("taac"));
        assertTrue(seq.get(1).contains("ATAA"));
        assertTrue(seq.get(2).contains("tta"));
        assertTrue(seq.get(3).contains("tatg"));
        assertTrue(seq.get(4).contains("gtat"));


    }

}