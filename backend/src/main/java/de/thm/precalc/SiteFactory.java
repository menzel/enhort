package de.thm.precalc;

import de.thm.backgroundModel.BackgroundModel;
import de.thm.genomeData.tracks.Track;
import de.thm.logo.Logo;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.random.MersenneTwister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Factory class to provide access to sites created by the site creator.
 * Stores the sites for a given assembly
 *
 * Created by menzel on 2/8/17.
 */
public final class SiteFactory {

    private final Genome.Assembly assembly;
    private IndexTable indexTable = new IndexTable();

    private final Logger logger = LoggerFactory.getLogger(SiteFactory.class);

    /**
     * Creates a site factory with given count for given assembly
     *
     * @param assembly - assembly number
     * @param count - count of positions to precalculate
     */
    SiteFactory(Genome.Assembly assembly, int count) {
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

        return new BackgroundModel(positions, assembly);
    }


    /**
     * Returns count positions that have a similar sequencelogo together as the given sequencelogo.
     *
     * @param sequencelogo - sequencelogo to fit sites to
     * @param count - count of positions to return
     *
     * @return positions with a sequencelogo similar to sequencelogo
     */
    public Sites getByLogo(Logo sequencelogo, int count) {

        List<String> seq;

        try {
            seq = indexTable.getSequences(sequencelogo.getConsensus().length());
        } catch (IllegalArgumentException e) {
            logger.error("Exception {}", e.getMessage(), e);
            return null;
        }

        List<Long> pos = indexTable.getPositions();
        List<Pair<Long, String>> positions = new ArrayList<>();

        for (int i = 0; i < pos.size(); i++) {
            ImmutablePair<Long, String> immutablePair = new ImmutablePair<>(pos.get(i), seq.get(i));
            positions.add(immutablePair);
        }

        Collections.shuffle(positions);

        List<Long> new_pos = new ArrayList<>(); // newly generated positions
        MersenneTwister rand = new MersenneTwister();
        Map<String, Double> scores = calculateScores(sequencelogo, seq);

        double sum = scores.values().stream().mapToDouble(d -> d).sum();
        List<Double> rands = new ArrayList<>();

        double cum = 0;
        int i = 0; //counter over seq/scores
        int j = 0; //counter over rands

        while(true){

            if(j >= rands.size()){
                rands.clear();
                IntStream.range(0, count).forEach(x -> rands.add(rand.nextDouble()*sum));
                Collections.sort(rands);
                j = 0;
                cum = 0;
            }

            while(cum < rands.get(j) && i < positions.size()) {
                double s = scores.get(positions.get(i++).getRight());
                cum += s;
            }

            j++;

            if(new_pos.size() < 1 || ! new_pos.get(new_pos.size()-1).equals(positions.get(i-1).getLeft())) {
               new_pos.add(positions.get(i - 1).getLeft());
            }

            if(i >= positions.size() || new_pos.size() >= count)
                break;
        }

        Collections.sort(new_pos); // dont forget to sort the sites here.

        return new BackgroundModel(new_pos, assembly);
    }

    /**
     *
     * Calculates the scores for a given list of sequences using the score function
     * Result values are streched to by the factor 1/max to have the highest value to be 1.0
     *
     * @param sequencelogo - sequencelogo to match against
     * @param seq - sequences to score
     *
     * @return scores as a map of sequences to scores
     */
    Map<String, Double> calculateScores(Logo sequencelogo, List<String> seq) {

        Map<String, Double> scores = new HashMap<>();

        //calc scores for each sequence and put in a map
        new HashSet<>(seq).forEach(s -> scores.put(s, score(sequencelogo, s)));

        //strech all scores by factor: 1/max_score to set highest score to 1.0
        double factor = 1/Collections.max(scores.values());
        scores.keySet().forEach(i -> scores.put(i, scores.get(i) * factor));


        //print best scores
        /*

        List<Double> tmp = new ArrayList<>(scores.values());
        Collections.sort(tmp);
        Collections.reverse(tmp);

        for(Double d: tmp.subList(0,min(10,tmp.size()-1))){
            for(String key: scores.keySet()){
                if(scores.get(key).equals(d)){
                    logger.info(key + " " + d);
                }
            }
        }
        */



        return scores;
    }

    /**
     * Scores a sequence against a sequencelogo. Returns a similarity values between 0.0 and 1.0 (inclusive)
     *
     * @param sequencelogo - sequencelogo to match against
     * @param sequence - sequence to match
     *
     * @return similarity from 0.0 to 1.0
     */
    Double score(Logo sequencelogo, String sequence) {
        double score = 1.0;
        sequence = sequence.toLowerCase();
        double pseudocount = Double.MIN_VALUE;

        //remove all seq that have 'n' positons, because those screw up the score calc. TODO multiply some really number for 'n' bases
        if(sequence.contains("n")) return 0.0;

        List<List<Map<String, String>>> values = sequencelogo.getValues();

        int i = 0; // sequence position counter

        for(List<Map<String, String>> position: values) { // for each position
            String base = Character.toString(sequence.charAt(i));

            for (Map<String, String> letter : position) { //for each letter
                if (i < sequence.length() && letter.get("letter").equals(base)) {
                    score *= Double.parseDouble(letter.get("bits")) / 2 + pseudocount;
                }
            }
            i++;
        }


        return score;
    }
}
