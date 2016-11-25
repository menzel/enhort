package de.thm.misc;

import de.thm.logo.GenomeFactory;
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
    private final Map<GenomeFactory.Assembly, Map<String, Integer>> chromosomeSizes = new HashMap<>();
    private final Map<GenomeFactory.Assembly, List<String>> names = new HashMap<> ();
    private final Map<String, Long> offsets = new HashMap<>();
    private Map<String, Long> genomeSize = new HashMap<>();

    /**
     * Private Constructor
     */
    private ChromosomSizes() {

        Map<String, Integer> hg19 = new HashMap<>();

        hg19.put("chr1", 249250621);
        hg19.put("chr2", 243199373);
        hg19.put("chr3", 198022430);
        hg19.put("chr4", 191154276);
        hg19.put("chr5", 180915260);
        hg19.put("chr6", 171115067);
        hg19.put("chr7", 159138663);
        hg19.put("chrX", 155270560);
        hg19.put("chr8", 146364022);
        hg19.put("chr9", 141213431);
        hg19.put("chr10", 135534747);
        hg19.put("chr11", 135006516);
        hg19.put("chr12", 133851895);
        hg19.put("chr13", 115169878);
        hg19.put("chr14", 107349540);
        hg19.put("chr15", 102531392);
        hg19.put("chr16", 90354753);
        hg19.put("chr17", 81195210);
        hg19.put("chr18", 78077248);
        hg19.put("chr20", 63025520);
        hg19.put("chrY", 59373566);
        hg19.put("chr19", 59128983);
        hg19.put("chr22", 51304566);
        hg19.put("chr21", 48129895);

        chromosomeSizes.put(GenomeFactory.Assembly.hg19, hg19);

        names.put(GenomeFactory.Assembly.hg19, new ArrayList<>(hg19.keySet()));
        java.util.Collections.sort(names.get(GenomeFactory.Assembly.hg19));

        // get whole genome length for hg19
        long gz = 0;
        for (String key : hg19.keySet()) {
            gz += hg19.get(key);
        }

        genomeSize.put("hg19", gz);
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

    public Long getChrSize(GenomeFactory.Assembly assembly, String chr) {
        return new Long(chromosomeSizes.get(assembly).get(chr));

    }

    public long getGenomeSize(GenomeFactory.Assembly assembly) {

        return genomeSize.get(assembly);
    }

    /**
     * Returns the offset for a specific chromosome by name.
     *
     * @param chromosomeName - name of the chromosome to get offset for
     * @return offset for new map
     */
    public Long offset(GenomeFactory.Assembly assembly, String chromosomeName) {


        if (!offsets.containsKey(chromosomeName)) {
            offsets.put(chromosomeName, calcOffset(assembly, chromosomeName));
        }

        return offsets.get(chromosomeName);

    }

    /**
     * Calculates the offset for a given chromosome
     *
     * @param chromosomeName - name of the chromosome
     * @return offset as Long
     */
    private Long calcOffset(GenomeFactory.Assembly assembly, String chromosomeName) {

        long offset = 0;

        for (String name : names.get(assembly)) {
            if (name.equals(chromosomeName))
                return offset;
            else
                offset += chromosomeSizes.get(assembly).get(name);
        }

        return null;
    }

    /**
     * Invert of offset + position. The original position and chr name is restored.
     *
     * @param position - position in genome
     * @return a pair containing chromosome name and position inside that chromosome.
     */
    public Pair<String, Long> mapToChr(GenomeFactory.Assembly assembly, Long position) {
        int i = 0;
        Long chrSize = Long.valueOf(chromosomeSizes.get(assembly).get(names.get(assembly).get(0)));

        try {

            while (position > chrSize) {
                position -= chrSize;
                chrSize = Long.valueOf(chromosomeSizes.get(assembly).get(names.get(assembly).get(++i)));
            }
        } catch (IndexOutOfBoundsException e){
            System.err.println("iofe");
            System.err.println("for: " + position);
            return null;
        }

        return new ImmutablePair<>(names.get(assembly).get(i), position);
    }

}
