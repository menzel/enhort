package de.thm.calc;

import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by menzel on 2/21/17.
 */
public class Hotspot {

    public ScoredTrack findHotspots(Sites sites, int windowSize){

        long genomeSize = ChromosomSizes.getInstance().getGenomeSize(sites.getAssembly());
        List<Long> starts = new ArrayList<>();
        List<Long> ends = new ArrayList<>();
        List<Double> score = new ArrayList<>();
        List<Long> positions = sites.getPositions();

        //TODO smart algorithm for sliding window

        for(long i = 0; i < genomeSize-windowSize && i < positions.get(positions.size()-1)+windowSize; i+=windowSize/10){
            // find sites between i and i + windowSize

            long finalI = i;
            long count =  positions.stream().filter(p -> p >= finalI && p < (finalI +windowSize)).count();

            starts.add(i);
            ends.add(i+windowSize);
            score.add((double) count);
        }

        return TrackFactory.getInstance().createScoredTrack(starts,ends,null,score, "CIS", "CIS");
    }

}
