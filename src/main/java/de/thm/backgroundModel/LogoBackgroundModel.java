package de.thm.backgroundModel;

import de.thm.misc.Logo;
import de.thm.positionData.Sites;

import java.util.Collection;
import java.util.List;

/**
 * Created by menzel on 11/24/16.
 */
public class LogoBackgroundModel implements Sites {

    private List<Long> positions;

    public LogoBackgroundModel(Logo logo, int count){

        positions = generatePositions(logo, count);

    }

    private List<Long> generatePositions(Logo logo, int count) {
        return null;
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
