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
public class LogoBackgroundModel implements Sites {

    private List<Long> positions;

    /**
     * Constructor for the background model
     *
     * @param logo - sequence logo to fit the positions to
     * @param count - count of positions
     */
    public LogoBackgroundModel(Logo logo, int count){

        positions = generatePositions(logo, count);

    }

    private List<Long> generatePositions(Logo logo, int count) {
        String motif = logo.getConsensus();

        //TODO take user set Assembly #
        return GenomeFactory.getInstance().getPositions(GenomeFactory.Assembly.hg19, motif, count);
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
}
