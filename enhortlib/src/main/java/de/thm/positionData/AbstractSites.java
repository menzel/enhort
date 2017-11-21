package de.thm.positionData;


import de.thm.misc.Genome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Skeletal implementation for the Sites interface.
 * <p>
 * Created by Michael Menzel on 23/2/16.
 */
public abstract class AbstractSites implements Sites {

    public List<Long> positions;
    public Genome.Assembly assembly;
    public List<Character> strands = new ArrayList<>();
    public String cellline;

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

    @Override
    public List<Character> getStrands() {
        return this.strands;
    }

    @Override
    public Genome.Assembly getAssembly() {
        return this.assembly;
    }

    @Override
    public String getCellline() {
        if (cellline == null)
            return "Unknown";
        return this.cellline;
    }

}

