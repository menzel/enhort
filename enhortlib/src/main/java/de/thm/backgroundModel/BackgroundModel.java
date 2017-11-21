package de.thm.backgroundModel;

import de.thm.misc.Genome;
import de.thm.positionData.AbstractSites;

import java.util.List;

public class BackgroundModel extends AbstractSites {

    public BackgroundModel(List<Long> positions, List<Character> strands, Genome.Assembly assembly) {
        this.positions = positions;
        this.strands = strands;
        this.assembly = assembly;
    }

    public BackgroundModel(List<Long> positions, Genome.Assembly assembly) {
        this.positions = positions;
        this.assembly = assembly;
    }
}
