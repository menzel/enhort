package de.thm.genomeData;

import de.thm.logo.GenomeFactory;

import java.util.List;

/**
 * Created by menzel on 2/21/17.
 */
public class StrandTrack extends AbstractTrack {

    private final List<Character> strands;

    StrandTrack(List<Long> starts, List<Long> ends, String name, String description, List<Character> strands) {
        super(starts, ends, name, description);
        this.strands = strands;
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

    public List<Character> getStrands() {
        return strands;
    }
}
