package de.thm.positionData;

import java.util.ArrayList;
import java.util.Collection;
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

    public void addPositions(Collection<Long> values) {
        positions.addAll(values);
    }

    public List<Long> getPositions() {
        return positions;
    }

    public void setPositions(List<Long> positions) {
        this.positions = positions;
    }

    public int getPositionCount() {
        return positions.size();
    }


}
