package de.thm.genomeData;

import de.thm.logo.GenomeFactory;

import java.util.List;

/**
 * Created by menzel on 2/21/17.
 */
public class StrandTrack extends AbstractTrack {

    private final List<Character> strands;

    StrandTrack(List<Long> starts, List<Long> ends, List<Character> strands, String name, String description, GenomeFactory.Assembly assembly, CellLine cellLine) {
        super(starts, ends, name, description,assembly, cellLine);
        this.strands = strands;
    }

    @Override
    public Track clone() {
        return new StrandTrack(this.getStarts(), this.getEnds(), this.getStrands(), this.getName(), this.getDescription(), this.getAssembly(), this.getCellLine());
    }

    public List<Character> getStrands() {
        return strands;
    }

}
