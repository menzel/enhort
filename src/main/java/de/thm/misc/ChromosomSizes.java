package de.thm.misc;

import de.thm.genomeData.sql.DBConnector;
import de.thm.logo.GenomeFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * Supplies chromosome sizes for HG19
 * Created by Michael Menzel on 8/12/15.
 */
public final class ChromosomSizes {

    private static ChromosomSizes instance;
    private final Map<GenomeFactory.Assembly, Map<String, Integer>> chromosomeSizes;
    private final Map<GenomeFactory.Assembly, List<String>> names = new HashMap<> ();
    private final Map<String, Long> offsets = new HashMap<>();
    private final Map<GenomeFactory.Assembly, Long> genomeSize = new HashMap<>();

    /**
     * Private Constructor
     */
    private ChromosomSizes() {

        Path basePath;

        if(System.getenv("HOME").contains("menzel")){
            basePath = new File("/home/menzel/Desktop/THM/lfba/enhort/dat/").toPath();
        } else {
            basePath = new File("/home/mmnz21/dat/").toPath();
        }
        DBConnector connector = new DBConnector();
        connector.connect();

        chromosomeSizes = connector.getChrSizes();


        for(GenomeFactory.Assembly assembly: chromosomeSizes.keySet()) {
            Map<String, Integer> hg = chromosomeSizes.get(assembly);

            names.put(assembly, new ArrayList<>(hg.keySet()));
            names.get(assembly).sort(Comparator.comparing(o -> o.substring(3))); // sort by Number

            // get whole genome length for the genome
            long gz = 0;
            for (String key : hg.keySet()) gz += hg.get(key);
            genomeSize.put(assembly, gz);
        }


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

    public Long getChrSize(GenomeFactory.Assembly assembly, String chr) throws NoSuchElementException{
        if(chromosomeSizes.containsKey(assembly))
            if(chromosomeSizes.get(assembly).containsKey(chr))
                return new Long(chromosomeSizes.get(assembly).get(chr));
        throw new NoSuchElementException("Either the assembly or chr number is unkown: " + assembly +  " " + chr);
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

