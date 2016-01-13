package de.thm.backgroundModel;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class AdvancedBackgroundModel extends BackgroundModel {

    private Random rand;
    private final int factor = 10;
    private AppearanceTable appearanceTable;

    /**
     *
     * @param intervals
     */
    public AdvancedBackgroundModel(List<Interval> intervals, Sites inputPositions) {

        appearanceTable = new AppearanceTable();

        appearanceTable.fillTable(intervals, inputPositions);


        //positions.addAll(randPositions(appearanceTable));
    }

    private Collection<? extends Long> randPositions(AppearanceTable appearanceTable) {

        return null;
    }

    public AppearanceTable getAppearanceTable() {
        return appearanceTable;
    }
}
