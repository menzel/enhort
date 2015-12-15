package de.thm.positionData;

import de.thm.misc.ChromosomSizes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class SimpleBackgroundModel extends BackgroundModel{

    private Random rand;

    public SimpleBackgroundModel(int sites) {

        initMap();
        rand = new Random(System.currentTimeMillis());
        createSites(sites);

    }

    private void createSites(int sites) {

        int perChr = sites/positions.keySet().size();

        for (Map.Entry<String, ArrayList<Long>> entry: positions.entrySet()) {

            for(int i = 0 ; i < perChr; i++) {
                long rand = randomPosition(entry.getKey());

                entry.getValue().add(rand);
                positionCount++;
            }

            Collections.sort(entry.getValue());

        }
    }

    private Long randomPosition(String chr) {

        return (long)(rand.nextDouble() * ChromosomSizes.getChrSize(chr));
    }

}
