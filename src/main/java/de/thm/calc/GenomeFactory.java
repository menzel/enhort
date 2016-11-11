package de.thm.calc;

import de.thm.genomeData.Track;
import de.thm.positionData.Sites;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class to provide access to different genome versions
 *
 * Created by menzel on 11/10/16.
 */
final class GenomeFactory {

    private static GenomeFactory instance;
    private Map<Track.Assembly, Genome> genomes = new HashMap<>();

    private GenomeFactory(){

        genomes.put(Track.Assembly.hg19, new Genome(new File("/home/menzel/Desktop/chromosomes").toPath()));
        //genomes.put(Track.Assembly.hg38, new Genome(new File("/home/menzel/Desktop/").toPath()));  TODO

    }

    public static GenomeFactory getInstance() {
        if (instance == null)
            instance = new GenomeFactory();
        return instance;
    }


    /**
     * Return a list of sequences with a width of width that are selected by the given list of positions
     *
     * @param assembly - assembly number (from the enum found in track)
     * @param sites - positions to check
     * @param width - width of the sequences
     *
     * @return list of sequences at sites for given assembly. null if assembly is not known
     */
    List<String> getSequence(Track.Assembly assembly, Sites sites, int width, int count){

        if(genomes.containsKey(assembly))
            return genomes.get(assembly).getSequence(sites, width, count);
        return null;
    }

}
