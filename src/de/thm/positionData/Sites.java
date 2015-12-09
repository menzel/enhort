package de.thm.positionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public abstract class Sites {
    /**
     *
     */
    protected  Map<String, ArrayList<Long>> positions = new HashMap<>();
    protected int positionCount;


    /**
     *
     */
    protected void initMap(){

        for(int i = 1; i <= 22; i++){
            positions.put("chr"+i, new ArrayList<>());
        }

        positions.put("chrX", new ArrayList<>());
        positions.put("chrY", new ArrayList<>());

    }

    public Iterator<Map.Entry<String, ArrayList<Long>>> getIterator(){

        return positions.entrySet().iterator();
    }

    /*
    Getter and Setter
     */

    public Map<String, ArrayList<Long>> getPositions() {
        return positions;
    }

    public int getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(int positionCount) {
        this.positionCount = positionCount;
    }
}
