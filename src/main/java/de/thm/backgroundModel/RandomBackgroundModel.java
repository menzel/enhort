package de.thm.backgroundModel;

import de.thm.misc.ChromosomSizes;

import java.util.Collections;
import java.util.Random;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class RandomBackgroundModel extends BackgroundModel{

    private Random rand;

    /**
     * Constructor
     *
     * @param sites count of sites to be generated
     */
    public RandomBackgroundModel(int sites) {

        rand = new Random(System.currentTimeMillis());
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
}
