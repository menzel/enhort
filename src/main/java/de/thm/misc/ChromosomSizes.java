package de.thm.misc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supplies chromosome sizes for HG19
 * Created by Michael Menzel on 8/12/15.
 */
public final class ChromosomSizes {

    private static ChromosomSizes instance;
    private final Map<String, Integer> sizes;
    private final List<String> names = new ArrayList<>();
    private long genomeSize = 0;
    private final Map<String, Long> offsets = new HashMap<>();

    /**
     * Private Constructor
     *
     */
    private ChromosomSizes() {

        sizes = new HashMap<>();

        sizes.put("chr1", 249250621);
        sizes.put("chr2", 243199373);
        sizes.put("chr3", 198022430);
        sizes.put("chr4", 191154276);
        sizes.put("chr5", 180915260);
        sizes.put("chr6", 171115067);
        sizes.put("chr7", 159138663);
        sizes.put("chrX", 155270560);
        sizes.put("chr8", 146364022);
        sizes.put("chr9", 141213431);
        sizes.put("chr10", 135534747);
        sizes.put("chr11", 135006516);
        sizes.put("chr12", 133851895);
        sizes.put("chr13", 115169878);
        sizes.put("chr14", 107349540);
        sizes.put("chr15", 102531392);
        sizes.put("chr16", 90354753);
        sizes.put("chr17", 81195210);
        sizes.put("chr18", 78077248);
        sizes.put("chr20", 63025520);
        sizes.put("chrY", 59373566);
        sizes.put("chr19", 59128983);
        sizes.put("chr22", 51304566);
        sizes.put("chr21", 48129895);

        names.addAll(sizes.keySet());
        java.util.Collections.sort(names);


       for(String key: sizes.keySet()){
            genomeSize += sizes.get(key);
       }
    }

    /**
     * get instance for singleton pattern
     *
     * @return intance of ChromosomeSizes.
     */
    public static ChromosomSizes getInstance(){
        if (instance == null){
            instance = new ChromosomSizes();
        }

        return instance;
    }

    public Long getChrSize(String chr) {
      return new Long(sizes.get(chr));

    }

    public long getGenomeSize(){

        return genomeSize;
    }

    /**
     * Returns the offset for a specific chromosome by name.
     *
     * @param chromosomeName - name of the chromosome to get offset for
     * @return offset for new map
     */
    public Long offset(String chromosomeName) {


        if(!offsets.containsKey(chromosomeName)){
            offsets.put(chromosomeName, calcOffset(chromosomeName));
        }

        return offsets.get(chromosomeName);

    }

    /**
     * Calculates the offset for a given chromosome
     *
     * @param chromosomeName - name of the chromosome
     *
     * @return  offset as Long
     */
    private Long calcOffset(String chromosomeName) {

        long offset = 0;

        for(String name: names){
            if(name.equals(chromosomeName))
                return offset;
            else
                offset += sizes.get(name);
        }

        return null;
    }

    /**
     * Invert of offset + position. The original position and chr name is restored.
     *
     * @param position - position in genome
     *
     * @return a pair containing chromosome name and position inside that chromosome.
     */
    public Pair<String, Long> mapToChr(Long position){
        int i = 0;
        Long chrSize = Long.valueOf(sizes.get(names.get(0)));

        while(position > chrSize){
            position -= chrSize;
            chrSize = Long.valueOf(sizes.get(names.get(++i)));
        }

        return new ImmutablePair<>(names.get(i),position);
    }

}
