package de.thm.precalc;

import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;

import java.util.Collection;
import java.util.List;

/**
 * BackgroundModel sites object for precalc sites
 *
 * Created by menzel on 2/13/17.
 */
final class PrecalcBackgroundModel implements Sites {

    private final GenomeFactory.Assembly assembly;
    private List<Long> positions;

    PrecalcBackgroundModel(GenomeFactory.Assembly assembly, List<Long> positions) {
        this.assembly = assembly;
        this.positions = positions;
    }

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
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }
}
