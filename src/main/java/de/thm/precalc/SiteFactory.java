package de.thm.precalc;

import de.thm.genomeData.Track;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by menzel on 2/8/17.
 */
public class SiteFactory {

    private GenomeFactory.Assembly assembly;
    private IndexTable indexTable = new IndexTable();

    public SiteFactory(GenomeFactory.Assembly assembly, int count) {
        this.assembly = assembly;

        SiteCreator creator = new SiteCreator();
        indexTable = creator.create(assembly, count);
    }


    public List<Long> getSites(Track track, int in, int out){

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

        return positions;
    }


    public List<Long> getByLogo(Logo logo, int count){
        List<Double> scores = new ArrayList<>();
        List<String> seq = indexTable.getSequences();
        List<Long> pos = indexTable.getPositions();
        List<Long> new_pos = new ArrayList<>();
        MersenneTwister rand = new MersenneTwister();

        //create scores for each seq
        for(String s: seq)
            scores.add(score(logo, s));

        //select scores based on propability
        double sum = scores.stream().mapToDouble(d -> d).sum();
        List<Double> rands = new ArrayList<>();

        for(int i = 0; i <= count ; i++)
            rands.add(rand.nextDouble()*sum); //get some random values
        Collections.sort(rands);

        double c = 0;
        for(int i = 0; i <= pos.size(); i++){

            if(c >= rands.get(i)) new_pos.add(pos.get(i));
            c += rands.get(i);

            if(new_pos.size() >= count)
                break;
        }

        return new_pos;
    }

    Double score(Logo logo, String sequence) {
        double score = 0.0;
        sequence = sequence.toLowerCase();

        List<List<Map<String, String>>> values =  logo.getValues();

        for(List<Map<String, String>> position: values) { // for each position
            int i = 0;

            for (Map<String, String> letter : position) { //for each letter
                if(letter.get("letter").equals(Character.toString(sequence.charAt(i++))))
                    score += Double.parseDouble(letter.get("bits"))/2;
            }
        }

        score /= values.size();

        return score;
    }
}
