package de.thm.backgroundModel;

import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalIntersect;
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

    private Collection<? extends Long> randPositions(AppearanceTable appearanceTable, List<Interval> intervals){

        List<Long> sites = new ArrayList<>();
        BetterBackgroundModel better = new BetterBackgroundModel();

        for(String app: appearanceTable.getKeySet()){
            if(app.compareTo("[]") == 0){
                continue;
            }

            int count = appearanceTable.getAppearance(app);
            List<Interval> currentIntervals = appearanceTable.translate(app);
            List<Interval> negativeIntevals = appearanceTable.translateNegative(intervals, app);

            currentIntervals.addAll(negativeIntevals.stream().map(Interval::invert).collect(Collectors.toList()));

            Interval interval = IntervalIntersect.intersect(currentIntervals);

            sites.addAll(better.randPositions(count,interval ,"in"));
        }

        int count = appearanceTable.getAppearance("[]");

        Interval outs = IntervalIntersect.intersectNone(intervals);
        sites.addAll(better.randPositions(count, outs ,"out"));

        return sites;
    }

    public AppearanceTable getAppearanceTable() {
        return appearanceTable;
    }
}
