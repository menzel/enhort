package de.thm.positionData;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of positions on a genome. Using offset for different chromosomes.
 *
 * Created by Michael Menzel on 8/12/15.
 */
public abstract class Sites {

    protected List<Long> positions = new ArrayList<>();

    /*
    Getter and Setter
     */

    public List<Long> getPositions() {
        return positions;
    }

}
