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
package de.thm.misc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Test chrSizes class
 *
 * Created by Michael Menzel on 7/1/16.
 */
public class ChromosomSizesTest {
    @Test
    public void getChrSize() throws Exception {

        Map<String, Long> hg19 = new HashMap<>();
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        hg19.put("chr1", 249250621L);
        hg19.put("chr2", 243199373L);
        hg19.put("chr3", 198022430L);
        hg19.put("chr4", 191154276L);
        hg19.put("chr5", 180915260L);
        hg19.put("chr6", 171115067L);
        hg19.put("chr7", 159138663L);
        hg19.put("chrX", 155270560L);
        hg19.put("chr8", 146364022L);
        hg19.put("chr9", 141213431L);
        hg19.put("chr10", 135534747L);
        hg19.put("chr11", 135006516L);
        hg19.put("chr12", 133851895L);
        hg19.put("chr13", 115169878L);
        hg19.put("chr14", 107349540L);
        hg19.put("chr15", 102531392L);
        hg19.put("chr16", 90354753L);
        hg19.put("chr17", 81195210L);
        hg19.put("chr18", 78077248L);
        hg19.put("chr20", 63025520L);
        hg19.put("chrY", 59373566L);
        hg19.put("chr19", 59128983L);
        hg19.put("chr22", 51304566L);
        hg19.put("chr21", 48129895L);

        long sum = 0;


        for(String chr: hg19.keySet()){
            assertEquals(hg19.get(chr),chrSizes.getChrSize(Genome.Assembly.hg19, chr));
            sum += hg19.get(chr);
        }

        assertEquals(sum, chrSizes.getGenomeSize(Genome.Assembly.hg19));

    }

    @Test
    public void testOffset() throws Exception {
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        //System.out.println(chrSizes.getChrSize("chr1"));

        assertEquals(chrSizes.getChrSize(Genome.Assembly.hg19, "chr1"), chrSizes.getChrSize( Genome.Assembly.hg19, "chr1"));
    }



    @Test
    public void testHg38() throws Exception {

        Pair<String, Long> oldPos= new ImmutablePair<>("chrX", 11014L);
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        Long mappedPos = chrSizes.offset(Genome.Assembly.GRCh38, oldPos.getLeft()) + oldPos.getRight();

        Pair<String, Long> newPos = chrSizes.mapToChr(Genome.Assembly.GRCh38, mappedPos);

        assertEquals(oldPos.getLeft(), newPos.getLeft());
        assertEquals(oldPos.getRight(), newPos.getRight());
    }

    @Test
    public void testMapToChr() throws Exception {
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        Long p = 9980L;
        Long pos = chrSizes.offset(Genome.Assembly.hg19, "chr1") + p;
        assertEquals(new ImmutablePair<>("chr1", p),chrSizes.mapToChr(Genome.Assembly.hg19, pos));


        p = 110179960L;
        pos = chrSizes.offset(Genome.Assembly.hg19, "chr6") + p;
        assertEquals(new ImmutablePair<>("chr6", p),chrSizes.mapToChr(Genome.Assembly.hg19, pos));


        p = 37080600L;
        pos = chrSizes.offset(Genome.Assembly.hg19, "chr15") + p;
        assertEquals(new ImmutablePair<>("chr15", p),chrSizes.mapToChr(Genome.Assembly.hg19, pos));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfChr() throws Exception {
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        Long p = 3095679876L;
        Long pos = chrSizes.offset(Genome.Assembly.hg19, "chrX") + p;
        assertEquals(null,chrSizes.mapToChr(Genome.Assembly.hg19, pos));
    }


}
