package de.thm.backgroundModel;

import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Background model to create random distributed sites without covariants.
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
class RandomBackgroundModel implements Sites {

    private final GenomeFactory.Assembly assembly;
    private transient MersenneTwister rand;
    private List<Long> positions = new ArrayList<>();

    /**
     * Constructor
     *
     * @param count - of sites to be generated
     */
    RandomBackgroundModel(GenomeFactory.Assembly assembly, int count) {
        this.assembly = assembly;

        rand  = new MersenneTwister();

        createSites(assembly, count);
    }

    /**
     * Creates random sites.
     *
     * @param sites count of sites to be created.
     */
    private void createSites(GenomeFactory.Assembly assembly, int sites) {

        long genomeSize = ChromosomSizes.getInstance().getGenomeSize(assembly)-1;

        for (long i = 0; i < sites; i++) {
            long r = Math.round(rand.nextDouble() * ((double) genomeSize));

            positions.add(r);
        }

        Collections.sort(positions);

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
    public int getPositionCount() {
        return this.positions.size();
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }
}
