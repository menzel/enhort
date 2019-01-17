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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Supplies chromosome sizes for HG19
 * Created by Michael Menzel on 8/12/15.
 */
public final class ChromosomSizes {

    private static volatile ChromosomSizes instance;
    private final Map<Genome.Assembly, Map<String, Integer>> chromosomeSizes;
    private final SortedMap<Genome.Assembly, List<String>> names = new TreeMap<> ();
    private final Map<Genome.Assembly, Map<String, Long>> offsets = new HashMap<>();
    private final Map<Genome.Assembly, Long> genomeSize = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(ChromosomSizes.class);

    /**
     * Private Constructor
     */
    private ChromosomSizes() {

        chromosomeSizes = getChrSizes();

        for(Genome.Assembly assembly: chromosomeSizes.keySet()) {
            Map<String, Integer> hg = chromosomeSizes.get(assembly);

            names.put(assembly, new ArrayList<>(hg.keySet()));
            names.get(assembly).sort(new ChromosomeComparator());

            // get whole genome length for the genome
            long gz = 0;
            for (String key : hg.keySet()) gz += hg.get(key);
            genomeSize.put(assembly, gz);
        }

        calcOffsets();
    }

    private Map<Genome.Assembly, Map<String, Integer>> getChrSizes() {
        String input = "hg19,chr1,249250621\n" +
                "hg19,chr2,243199373\n" +
                "hg19,chr3,198022430\n" +
                "hg19,chr4,191154276\n" +
                "hg19,chr5,180915260\n" +
                "hg19,chr6,171115067\n" +
                "hg19,chr7,159138663\n" +
                "hg19,chr8,146364022\n" +
                "hg19,chr9,141213431\n" +
                "hg19,chr10,135534747\n" +
                "hg19,chr11,135006516\n" +
                "hg19,chr12,133851895\n" +
                "hg19,chr13,115169878\n" +
                "hg19,chr14,107349540\n" +
                "hg19,chr15,102531392\n" +
                "hg19,chr16,90354753\n" +
                "hg19,chr17,81195210\n" +
                "hg19,chr18,78077248\n" +
                "hg19,chr19,59128983\n" +
                "hg19,chr20,63025520\n" +
                "hg19,chr21,48129895\n" +
                "hg19,chr22,51304566\n" +
                "hg19,chrX,155270560\n" +
                "hg19,chrY,59373566\n" +
                "GRCh38,chr1,248956422\n" +
                "GRCh38,chr2,242193529\n" +
                "GRCh38,chr3,198295559\n" +
                "GRCh38,chr4,190214555\n" +
                "GRCh38,chr5,181538259\n" +
                "GRCh38,chr6,170805979\n" +
                "GRCh38,chr7,159345973\n" +
                "GRCh38,chr8,145138636\n" +
                "GRCh38,chr9,138394717\n" +
                "GRCh38,chr10,133797422\n" +
                "GRCh38,chr11,135086622\n" +
                "GRCh38,chr12,133275309\n" +
                "GRCh38,chr13,114364328\n" +
                "GRCh38,chr14,107043718\n" +
                "GRCh38,chr15,101991189\n" +
                "GRCh38,chr16,90338345\n" +
                "GRCh38,chr17,83257441\n" +
                "GRCh38,chr18,80373285\n" +
                "GRCh38,chr19,58617616\n" +
                "GRCh38,chr20,64444167\n" +
                "GRCh38,chr21,46709983\n" +
                "GRCh38,chr22,50818468\n" +
                "GRCh38,chrX,156040895\n" +
                "GRCh38,chrY,57227415";

        Map<Genome.Assembly, Map<String, Integer>> chrs = new HashMap<>();

        for( String line: input.split("\n")) {
            String[] parts = line.split(",");

            if (!chrs.containsKey(Genome.Assembly.valueOf(parts[0])))
                chrs.put(Genome.Assembly.valueOf(parts[0]), new HashMap<>());
            chrs.get(Genome.Assembly.valueOf(parts[0])).put(parts[1], Integer.parseInt(parts[2]));
        }
        return chrs;
    }

    private Map<Genome.Assembly,Map<String,Integer>> getChrSizesFromFile(String filepath) {
        Map<Genome.Assembly, Map<String, Integer>> chrs = new HashMap<>();

        try(Stream<String> stream = Files.lines(Paths.get(filepath))){
            stream.forEach(line -> {

                String[] parts = line.split(",");

                if(!chrs.containsKey(Genome.Assembly.valueOf(parts[0])))
                    chrs.put(Genome.Assembly.valueOf(parts[0]), new HashMap<>());
                chrs.get(Genome.Assembly.valueOf(parts[0])).put(parts[1], Integer.parseInt(parts[2]));

            });

        } catch (IOException e) {
            e.printStackTrace();
        }


        return chrs;
    }

    /**
     * Returns the offset for a specific chromosome by name.
     *
     * @param chromosomeName - name of the chromosome to get offset for
     * @return offset for new map
     */
    public Long offset(Genome.Assembly assembly, String chromosomeName) {

        return offsets.get(assembly).get(chromosomeName);

    }

    /**
     * get instance for singleton pattern
     *
     * @return intance of ChromosomeSizes.
     */
    public static ChromosomSizes getInstance() {
        if (instance == null) {
            instance = new ChromosomSizes();
        }

        return instance;
    }

    public Long getChrSize(Genome.Assembly assembly, String chr) throws NoSuchElementException{
        if(chromosomeSizes.containsKey(assembly))
            if(chromosomeSizes.get(assembly).containsKey(chr))
                return Integer.toUnsignedLong(chromosomeSizes.get(assembly).get(chr));
        throw new NoSuchElementException("Either the assembly or chr number is unkown: " + assembly +  " " + chr);
    }

    public long getGenomeSize(Genome.Assembly assembly) {

        return genomeSize.get(assembly);
    }

    /**
     * Calculates the offsets for all assemblies and chrs
     *
     */
    private void calcOffsets() {

        for(Genome.Assembly assembly: chromosomeSizes.keySet()){

            Map<String, Long> tmp = new HashMap<>();
            Map<String, Integer> chrSizes = chromosomeSizes.get(assembly);
            Long sum = 0L;

            for(String chrNum: names.get(assembly)){
                tmp.put(chrNum, sum);
                sum += chrSizes.get(chrNum);
            }

            offsets.put(assembly, tmp);
        }
    }

    private class ChromosomeComparator implements Comparator<String>{

        @Override
        public int compare(String o1, String o2){
            String s1 = o1.substring(3);
            String s2 = o2.substring(3);

            if(o1.equals(o2)) return 0;
            else if(StringUtils.isNumeric(s1) && StringUtils.isNumeric(s2))
                return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
            else if(StringUtils.isNumeric(s1)) return -1;
            else if(StringUtils.isNumeric(s2)) return 1;
            else return o1.contains("X") ? -1 : 1;
        }
    }

    /**
     * Invert of offset + position. The original position and chr name is restored.
     *
     * @param position - position in genome
     * @return a pair containing chromosome name and position inside that chromosome.
     */
    public Pair<String, Long> mapToChr(Genome.Assembly assembly, Long position) {
        long finalPosition = position;
        int i = 0;
        Long chrSize = Long.valueOf(chromosomeSizes.get(assembly).get(names.get(assembly).get(0)));

        try {

            while (position > chrSize) {
                position -= chrSize;
                chrSize = Long.valueOf(chromosomeSizes.get(assembly).get(names.get(assembly).get(++i)));
            }
        } catch (IndexOutOfBoundsException e){ // if i is over 24 (24 chromosomes)

            //throw an exception that does not need to be catched
            throw new IllegalArgumentException("Position is outside of the genome " + finalPosition);

        }

        return new ImmutablePair<>(names.get(assembly).get(i), position);
    }

}

