package de.thm.genomeData;

import de.thm.logo.GenomeFactory;

import java.util.List;

/**
 * Created by menzel on 2/21/17.
 */
public class StrandTrack extends AbstractTrack {

    StrandTrack(List<Long> starts, List<Long> ends, String name, String description) {
        super(starts, ends, name, description);
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return null;
    }

    @Override
    public CellLine getCellLine() {
        return null;
    }

    @Override
    public Track clone() {
        return null;
    }
}
