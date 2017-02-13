package de.thm.precalc;

import de.thm.genomeData.Track;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.positionData.Sites;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by menzel on 2/8/17.
 */
public final class SiteFactory {

    private GenomeFactory.Assembly assembly;
    private IndexTable indexTable = new IndexTable();

    public SiteFactory(GenomeFactory.Assembly assembly, int count) {
        this.assembly = assembly;

        SiteCreator creator = new SiteCreator();
        indexTable = creator.create(assembly, count);
    }

    public Sites getSites(Track track, int in, int out){

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


    public Sites getByLogo(Logo logo, int count){
        List<String> seq = indexTable.getSequences(logo.getConsensus().length());
        List<Long> pos = indexTable.getPositions();
        List<Long> new_pos = new ArrayList<>();
        MersenneTwister rand = new MersenneTwister();

        Map<String, Double> scores = new HashMap<>();

        //create scores for each seq
        // for(String s: seq.stream().collect(Collectors.toSet())) {
        for(String s: seq.stream().collect(Collectors.toSet())) {
            if(!scores.containsKey(s))
                scores.put(s, score(logo,s));
        }

        //select scores based on propability
        double sum = scores.values().stream().mapToDouble(d -> d).sum();
        if(sum <= 0.0) System.err.println("No fitting scores found");


        List<Double> rands = new ArrayList<>();

        for(int i = 0; i <= count; i++)
            rands.add(rand.nextDouble()*sum); //get some random values
        Collections.sort(rands);

        double cum = 0;
        int i = 0; //counter over seq/scores
        int j = 0; //counter over rands

        while(j < rands.size()){

            while(cum < rands.get(j)) {
                double s = scores.get(seq.get(i++));
                //System.out.println(seq.get(i-1) + " " + s);

                cum += s;
            }

            j++;

            new_pos.add(pos.get(i-1));
            //System.out.println(pos.get(i) + " " + seq.get(i-1) +  " " + scores.get(seq.get(i-1)));

            if(i >= pos.size() || new_pos.size() >= count)
                break;
        }

        return new PrecalcBackgroundModel(assembly, new_pos);
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
        double score = 0.0;
        sequence = sequence.toLowerCase();

        List<List<Map<String, String>>> values =  logo.getValues();

        int i = 0; // sequence position counter

        for(List<Map<String, String>> position: values) { // for each position

            for (Map<String, String> letter : position) //for each letter
                if(i < sequence.length() && letter.get("letter").equals(Character.toString(sequence.charAt(i))))
                    score += Double.parseDouble(letter.get("bits"))/2;
                else
                    score -= Double.parseDouble(letter.get("bits"))/4;

            i++;
        }

        score /= values.size();

        if(score < 0) score = 0.;

        return score;
    }


}
