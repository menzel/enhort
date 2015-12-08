package de.thm.positionData;

import de.thm.misc.ChromosomSizes;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class SimpleBackgroundModel extends BackgroundModel{

    public SimpleBackgroundModel(int sites) {

        initMap();
        createSites(sites);

    }

    private void createSites(int sites) {

        int perChr = sites/24;

        for (Map.Entry<String, ArrayList<Long>> entry: positions.entrySet()) {

            for(int i = 0 ; i < perChr; i++) {
                entry.getValue().add(randomPosition(entry.getKey()));
            }

        }
    }

    private Long randomPosition(String chr) {
        Random rand =  new Random();

        return rand.nextLong() * ChromosomSizes.getChrSize(chr);
    }

}
