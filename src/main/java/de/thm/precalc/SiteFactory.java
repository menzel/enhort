package de.thm.precalc;

import de.thm.genomeData.Track;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.positionData.Sites;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Factory class to provide access to sites created by the site creator.
 * Stores the sites for a given assembly
 *
 * Created by menzel on 2/8/17.
 */
final class SiteFactory {

    private final GenomeFactory.Assembly assembly;
    private IndexTable indexTable = new IndexTable();

    /**
     * Creates a site factory with given count for given assembly
     *
     * @param assembly - assembly number
     * @param count - count of positions to precalculate
     */
    SiteFactory(GenomeFactory.Assembly assembly, int count) {
        this.assembly = assembly;

        SiteCreator creator = new SiteCreator();
        indexTable = creator.create(assembly, count);
    }

    /**
     * Gets sites that have given inout count on the given track
     *
     * @param track - track to observe
     * @param in - inside count
     * @param out - outside count
     *
     * @return returns n (for n = in+out) positions
     */
    Sites getSites(Track track, int in, int out){

        List<Long> positions = new ArrayList<>();

        Pair<List<Long>, List<Integer>> pair = indexTable.getByTrack(track);

        List<Long> left = pair.getLeft();

        //inside positions
        for (int i = 0; i < left.size(); i++) {

            Long pos = left.get(i);
            if(in <= 0) break;

            if (pair.getRight().get(i) == 1){
                positions.add(pos);
                in--;
            }

        }

        //outside positions
        for (int i = 0; i < left.size(); i++) {

            Long pos = left.get(i);
            if(out <= 0) break;

            if (pair.getRight().get(i) == 0){
                positions.add(pos);
                out--;
            }

        }

        return new PrecalcBackgroundModel(assembly, positions);
    }


    /**
     * Returns count positions that have a similar logo together as the given logo.
     *
     * @param logo - logo to fit sites to
     * @param count - count of positions to return
     *
     * @return positions with a logo similar to logo
     */
    public Sites getByLogo(Logo logo, int count){
        List<String> seq = indexTable.getSequences(logo.getConsensus().length());
        List<Long> pos = indexTable.getPositions();
        List<Long> new_pos = new ArrayList<>();
        MersenneTwister rand = new MersenneTwister();
        Map<String, Double> scores = calculateScores(logo, seq);

        //select scores based on propability
        double sum = scores.values().stream().mapToDouble(d -> d).sum();

        List<Double> rands = new ArrayList<>();
        IntStream.range(0, count).forEach(i -> rands.add(rand.nextDouble()*sum));
        Collections.sort(rands);

        double cum = 0;
        int i = 0; //counter over seq/scores
        int j = 0; //counter over rands

        while(j < rands.size()){

            while(cum < rands.get(j)) {
                double s = scores.get(seq.get(i++));
                cum += s;
            }

            j++;

            new_pos.add(pos.get(i-1));

            if(i >= pos.size() || new_pos.size() >= count)
                break;
        }

        return new PrecalcBackgroundModel(assembly, new_pos);
    }

    /**
     *
     * Calculates the scores for a given list of sequences using the score function
     * Result values are streched to by the factor 1/max to have the highest value to be 1.0
     *
     * @param logo - logo to match against
     * @param seq - sequences to score
     *
     * @return scores as a map of sequences to scores
     */
    Map<String,Double> calculateScores(Logo logo, List<String> seq) {

        Map<String, Double> scores = new HashMap<>();

        //calc scores for each sequence and put in a map
        seq.stream().collect(Collectors.toSet()).forEach(s -> scores.put(s, score(logo,s)));

        //strech all scores by factor: 1/max_score to set highest score to 1.0
        double factor = 1/Collections.max(scores.values());
        scores.keySet().forEach(i -> scores.put(i, scores.get(i) * factor));

        return scores;
    }

    /**
     * Scores a sequence against a logo. Returns a similarity values between 0.0 and 1.0 (inclusive)
     *
     * @param logo - logo to match against
     * @param sequence - sequence to match
     *
     * @return similarity from 0.0 to 1.0
     */
    Double score(Logo logo, String sequence) {
        double score = 1.0;
        sequence = sequence.toLowerCase();

        List<List<Map<String, String>>> values =  logo.getValues();

        int i = 0; // sequence position counter

        for(List<Map<String, String>> position: values) { // for each position

            for (Map<String, String> letter : position) //for each letter
                if(i < sequence.length() && letter.get("letter").equals(Character.toString(sequence.charAt(i))))
                    score *= Double.parseDouble(letter.get("bits"))/2  + 0.001;

            i++;
        }

        if(score == 1.0) return 0.0; // set score to 0 for sequence with no match (eg. nn...n)

        return score;
    }
}
