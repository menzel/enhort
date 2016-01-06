package de.thm.positionData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public abstract class Sites {
    /**
     *
     */
    protected List<Long> positions = new ArrayList<>();
    protected int positionCount;


    public Iterator<Long> getIterator(){

        return positions.iterator();
    }

    /*
    Getter and Setter
     */

    public List<Long> getPositions() {
        return positions;
    }

    public int getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(int positionCount) {
        this.positionCount = positionCount;
    }
}
