package de.thm.backgroundModel;

import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Background model to create random distributed sites without covariants.
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
class RandomBackgroundModel implements Sites {

    private final GenomeFactory.Assembly assembly;
    private List<Long> positions;
    private List<Character> strands;

    /**
     * Constructor
     *
     * @param count - of sites to be generated
     */
    RandomBackgroundModel(GenomeFactory.Assembly assembly, int count) {
        this.assembly = assembly;

        strands = new ArrayList<>();

        Track contigs = TrackFactory.getInstance().getTrackByName("Contigs", assembly);
        SingleTrackBackgroundModel singleTrackBackgroundModel = new SingleTrackBackgroundModel(assembly);

        positions = new ArrayList<>(singleTrackBackgroundModel.randPositions(count, contigs));


        MersenneTwister rand = new MersenneTwister();
        for (long i = 0; i < positions.size(); i++)
            strands.add(rand.nextBoolean() ? '+' : '-');
    }

    @Override
    public void addPositions(Collection<Long> values) {
        this.positions.addAll(values);
    }

    @Override
    public List<Long> getPositions() {
        return this.positions;
    }

    @Override
    public void setPositions(List<Long> positions) {
        this.positions = positions;
    }

    @Override
    public List<Character> getStrands() {
        return strands;
    }

    @Override
    public int getPositionCount() {
        return this.positions.size();
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }
}
