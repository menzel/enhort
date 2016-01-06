package de.thm.positionData;

import de.thm.misc.ChromosomSizes;

import java.util.Collections;
import java.util.Random;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class SimpleBackgroundModel extends BackgroundModel{

    private Random rand;

    public SimpleBackgroundModel(int sites) {

        rand = new Random(System.currentTimeMillis());
        createSites(sites);

    }

    private void createSites(int sites) {

        long genomeSize = ChromosomSizes.getInstance().getGenomeSize();

        for(long i = 0 ; i < sites; i++) {
            long r = Math.round(rand.nextDouble() * ((double) genomeSize));

            positions.add(r);
            positionCount++;
        }

        Collections.sort(positions);

    }
}
