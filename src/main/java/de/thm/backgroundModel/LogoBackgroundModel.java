package de.thm.backgroundModel;

import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.positionData.Sites;

import java.util.Collection;
import java.util.List;

/**
 * Creates a background model based on a Sequencelogo @{Logo}
 *
 * Created by menzel on 11/24/16.
 */
class LogoBackgroundModel implements Sites {

    private final GenomeFactory.Assembly assembly;
    private List<Long> positions;

    /**
     * Constructor for the background model
     *
     * @param assembly - assembly number
     * @param logo - sequence logo to fit the positions to
     * @param count - count of positions
     */
    LogoBackgroundModel(GenomeFactory.Assembly assembly, Logo logo, int count){
        this.assembly = assembly;
        count = 12000/logo.getConsensus().length();

        positions = generatePositions(assembly, logo, count);
    }

    private List<Long> generatePositions(GenomeFactory.Assembly assembly, Logo logo, int count) {
        String motif = logo.getConsensus();

        //TODO take user set Assembly #
        return GenomeFactory.getInstance().getPositions(assembly, motif, count);
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
    public int getPositionCount() {
        return this.positions.size();
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }
}
