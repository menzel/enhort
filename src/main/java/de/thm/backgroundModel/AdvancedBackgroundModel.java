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

    /**
     *
     * @param intervals
     */
    public AdvancedBackgroundModel(List<Interval> intervals, Sites inputPositions) {

        AppearanceTable appearanceTable = new AppearanceTable();

        appearanceTable.fillTable(intervals, inputPositions);

        System.out.println(appearanceTable.getAppearance(intervals));
        System.out.println(appearanceTable.getAppearance(intervals.subList(0,1)));
        System.out.println(appearanceTable.getAppearance(intervals.subList(1,2)));
        System.out.println(appearanceTable.getAppearance(intervals.subList(2,3)));
        System.out.println(appearanceTable.getAppearance(intervals.subList(1,3)));


        //positions.addAll(randPositions(appearanceTable));
    }

    private Collection<? extends Long> randPositions(AppearanceTable appearanceTable) {

        return null;
    }
}
