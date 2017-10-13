package de.thm.misc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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

    private static ChromosomSizes instance;
    private final Map<Genome.Assembly, Map<String, Integer>> chromosomeSizes;
    private final SortedMap<Genome.Assembly, List<String>> names = new TreeMap<> ();
    private final Map<Genome.Assembly, Map<String, Long>> offsets = new HashMap<>();
    private final Map<Genome.Assembly, Long> genomeSize = new HashMap<>();

    /**
     * Private Constructor
     */
    private ChromosomSizes() {

        chromosomeSizes = getChrSizesFromFile("chrSizes.csv");

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

