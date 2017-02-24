package de.thm.calc;

import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;

/**
 * Hotspot calculator across the genome. Returns a track with integration scores for sliding windows
 *
 * Created by menzel on 2/21/17.
 */
class Hotspot {

    /**
     * Find hotspots with sliding window. Returns a track with found integration scores as count for the window
     *
     * @param sites - sites to measure
     * @param windowSize - window size for sliding windows, should be large enough to reduce computation time
     *
     * @return track with integration count as scores
     */
    ScoredTrack findHotspots(Sites sites, int windowSize){

        long genomeSize = ChromosomSizes.getInstance().getGenomeSize(sites.getAssembly());

        int size = (int) (genomeSize/(windowSize/10));

        List<Long> starts = new ArrayList<>(size);
        List<Long> ends = new ArrayList<>(size);
        List<Double> score = new ArrayList<>(size);
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
