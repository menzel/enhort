package de.thm.positionData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Michael Menzel on 23/2/16.
 */
public class AbstractSites implements Sites{

    private List<Long> positions = new ArrayList<>();

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
        return positions.size();
    }
}
