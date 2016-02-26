package de.thm.backgroundModel;

import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.*;

/**
 * Background model to create random distributed sites without covariants.
 *
 * Created by Michael Menzel on 8/12/15.
 */
class RandomBackgroundModel implements Sites {

    private final Random rand;
    private List<Long> positions = new ArrayList<>();

    /**
     * Constructor
     *
     * @param sites count of sites to be generated
     */
    RandomBackgroundModel(int sites) {

        rand = new Random(System.currentTimeMillis());
        sites = (sites > 10000)? sites: 10000;
        createSites(sites);

    }

    /**
     * Creates random sites.
     *
     * @param sites count of sites to be created.
     */
    private void createSites(int sites) {

        long genomeSize = ChromosomSizes.getInstance().getGenomeSize();

        for(long i = 0 ; i < sites; i++) {
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
}
