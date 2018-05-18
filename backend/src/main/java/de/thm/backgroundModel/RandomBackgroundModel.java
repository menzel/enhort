package de.thm.backgroundModel;

import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;
import java.util.List;

/**
 * Background model to create random distributed sites without covariants.
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
class RandomBackgroundModel {

    /**
     * @param assembly - assembly of the generated sites
     * @param count - of sites to be generated
     */
    static BackgroundModel create(Genome.Assembly assembly, int count) {

        List<Character> strands = new ArrayList<>();
        Track contigs;

        try {
            contigs = TrackFactory.getInstance().getTrackByName("Contigs", assembly);
        } catch (RuntimeException e) {
            // contigs track not found
            contigs = TrackFactory.getInstance().createEmptyTrack(assembly);
        }

        List<Long> positions = new ArrayList<>(SingleTrackBackgroundModel.randPositions(count, contigs));

        MersenneTwister rand = new MersenneTwister();
        for (long i = 0; i < positions.size(); i++)
            strands.add(rand.nextBoolean() ? '+' : '-');

        return new BackgroundModel(positions,strands,assembly);
    }

}
