package de.thm.backgroundModel;

import de.thm.misc.Genome;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BackgroundModel implements Sites {
    List<Long> positions = new ArrayList<>();
    List<Character> strands = new ArrayList<>();
    Genome.Assembly assembly = Genome.Assembly.Unknown;

    public BackgroundModel(List<Long> positions, List<Character> strands, Genome.Assembly assembly) {
        this.positions = positions;
        this.strands = strands;
        this.assembly = assembly;
    }

    public BackgroundModel(List<Long> positions, Genome.Assembly assembly) {
        this.positions = positions;
        this.assembly = assembly;
    }


    @Override
    public void addPositions(Collection<Long> values) {
        positions.addAll(values);
    }

    @Override
    public List<Long> getPositions() {
        return positions;
    }

    @Override
    public void setPositions(List<Long> positions) {
        this.positions = positions;
    }

    @Override
    public List<Character> getStrands() {
        return strands;
    }

    @Override
    public int getPositionCount() {
        return positions.size();
    }

    @Override
    public Genome.Assembly getAssembly() {
        return assembly;
    }
}
