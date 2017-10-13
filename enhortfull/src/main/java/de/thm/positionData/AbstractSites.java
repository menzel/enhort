package de.thm.positionData;

import de.thm.logo.GenomeFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Skeletal implementation for the Sites interface.
 * <p>
 * Created by Michael Menzel on 23/2/16.
 */
@SuppressWarnings("unused")
public abstract class AbstractSites implements Sites {

    private List<Long> positions = new ArrayList<>();
    private GenomeFactory.Assembly assembly;
    private List<Character> strand = new ArrayList<>();

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
        return this.strand;
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }
}
