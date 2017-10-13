package de.thm.logo;

import de.thm.positionData.Sites;
import de.thm.misc.Genome;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class to provide access to different genome versions
 *
 * Created by menzel on 11/10/16.
 */
public final class GenomeFactory {

    private static GenomeFactory instance;
    private final Map<Genome.Assembly, GenomeSequence> genomes = new HashMap<>();
    private GenomeFactory(){


        if(System.getenv("HOME").contains("menzel")){
            genomes.put(Genome.Assembly.hg19, new GenomeSequence(Genome.Assembly.hg19, new File("/home/menzel/Desktop/chromosomes").toPath()));
        } else {
            genomes.put(Genome.Assembly.hg19, new GenomeSequence(Genome.Assembly.hg19, new File("/tempData/chromosomes").toPath()));
        }
        //genomes.put(Track.Genome.Assembly.hg38, new GenomeSequence(new File("/home/menzel/Desktop/").toPath()));  TODO

    }

    public static GenomeFactory getInstance() {
        if (instance == null)
            instance = new GenomeFactory();
        return instance;
    }

    /**
     * Return a list of sequences with a width of width that are selected by the given sites object
     *
     * @param assembly - assembly number (from the enum found in track)
     * @param sites - positions to check
     * @param width - width of the sequences
     *
     * @return list of sequences at sites for given assembly. null if assembly is not known
     */
    public List<String> getSequence(Genome.Assembly assembly, Sites sites, int width, int count){

        if(genomes.containsKey(assembly))
            return genomes.get(assembly).getSequence(sites, width, count);
        return null;
    }

    /**
     * Return a list of sequences with a width of width that are selected by the given list of positions
     *
     * @param assembly - assembly number (from the enum found in track)
     * @param positions - positions to check
     * @param width - width of the sequences
     *
     * @return list of sequences at sites for given assembly. null if assembly is not known
     */
    public List<String> getSequence(Genome.Assembly assembly, List<Long> positions, int width, int count){

        if(genomes.containsKey(assembly))
            return genomes.get(assembly).getSequence(positions, width, count);
        return null;
    }

    public List<Long> getPositions(Genome.Assembly assembly, String logo, int count){
        if(genomes.containsKey(assembly))
            return genomes.get(assembly).getPositions(logo, count);
         return null;

    }

}
