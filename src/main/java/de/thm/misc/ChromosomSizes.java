package de.thm.misc;

import java.util.*;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class ChromosomSizes {

    private long genomeSize = 0;
    private Map<String, Integer> sizes;
    private static ChromosomSizes instance;
    private Collection<String> names;

    public static ChromosomSizes getInstance(){
        if (instance == null){
            instance = new ChromosomSizes();
        }

        return instance;
    }


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

        names = sizes.keySet();


       for(String key: sizes.keySet()){
            genomeSize += sizes.get(key);
       }
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

        Map<String, Long> offsets = new HashMap<>();

        if(!offsets.containsKey(chromosomeName)){
            offsets.put(chromosomeName, calcOffset(chromosomeName));
        }

        return offsets.get(chromosomeName);

    }

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
}
