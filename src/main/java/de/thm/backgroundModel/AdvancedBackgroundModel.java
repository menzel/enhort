package de.thm.backgroundModel;

import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class AdvancedBackgroundModel extends BackgroundModel {

    private AppearanceTable appearanceTable;

    /**
     *
     * @param intervals
     */
    public AdvancedBackgroundModel(List<Interval> intervals, Sites inputPositions) {

        appearanceTable = new AppearanceTable();
        appearanceTable.fillTable(intervals, inputPositions);
        positions.addAll(randPositions(appearanceTable, intervals));
    }

    protected AdvancedBackgroundModel() { }


    protected Collection<? extends Long> randPositions(AppearanceTable appearanceTable, List<Interval> intervals){

        List<Long> sites = new ArrayList<>();
        BetterBackgroundModel better = new BetterBackgroundModel();

        for(String app: appearanceTable.getKeySet()){
            if(app.compareTo("[]") == 0){
                continue;
            }

            int count = appearanceTable.getAppearance(app);
            List<Interval> currentIntervals = appearanceTable.translate(app, intervals);
            List<Interval> negativeIntervals = appearanceTable.translateNegative(intervals, app);

            currentIntervals.addAll(negativeIntervals.stream().map(Interval::invert).collect(Collectors.toList()));

            Interval interval = Intervals.intersect(currentIntervals);

            sites.addAll(better.randPositions(count,interval ,"in"));
        }

        int count = appearanceTable.getAppearance("[]");
        Interval outs = Intervals.sum(intervals).invert();
        sites.addAll(better.randPositions(count, outs ,"in"));

        return sites;
    }

    public AppearanceTable getAppearanceTable() {
        return appearanceTable;
    }
}
