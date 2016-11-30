package de.thm.positionData;

import de.thm.logo.GenomeFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Holds a list of positions on a genome. Using offset for different chromosomes.
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
public interface Sites extends Serializable {

    void addPositions(Collection<Long> values);

    List<Long> getPositions();

    void setPositions(List<Long> positions);

    int getPositionCount();

    GenomeFactory.Assembly getAssembly();
}
