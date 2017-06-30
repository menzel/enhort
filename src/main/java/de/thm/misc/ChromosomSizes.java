package de.thm.misc;

import de.thm.logo.GenomeFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Supplies chromosome sizes for HG19
 * Created by Michael Menzel on 8/12/15.
 */
public final class ChromosomSizes {

    private static ChromosomSizes instance;
    private final Map<GenomeFactory.Assembly, Map<String, Integer>> chromosomeSizes = new HashMap<>();
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


        readChrSizes(basePath, GenomeFactory.Assembly.hg18);
        readChrSizes(basePath, GenomeFactory.Assembly.hg19);
        readChrSizes(basePath, GenomeFactory.Assembly.hg38);

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

    /**
     * Reads the chr sizes for the given path and assembly
     *
     * @param basePath - path to data directory
     * @param assembly - assembly name from GenomeFactory.Assembly
     */
    private void readChrSizes(Path basePath, GenomeFactory.Assembly assembly) {
        Map<String, Integer> hg = new HashMap<>();


        try (Stream<String> lines = Files.lines(basePath.resolve(assembly.toString() + "/chrSizes"), StandardCharsets.UTF_8)) {
            Iterator<String> it = lines.iterator();
            Pattern chrPattern = Pattern.compile("(chr(\\d{1,2}|X|Y))\\s(\\d+)");

            while(it.hasNext()){
                String line = it.next();
                Matcher lineMatcher = chrPattern.matcher(line);

                if(lineMatcher.matches())
                    hg.put(lineMatcher.group(1), Integer.valueOf(lineMatcher.group(3)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        chromosomeSizes.put(assembly, hg);

        names.put(assembly, new ArrayList<>(hg.keySet()));
        java.util.Collections.sort(names.get(assembly));

        // get whole genome length for the genome
        long gz = 0;
        for (String key : hg.keySet()) gz += hg.get(key);
        genomeSize.put(assembly, gz);
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

