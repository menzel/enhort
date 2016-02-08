package de.thm.backgroundModel;

import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a background model which can use many covariants.
 *
 * Created by Michael Menzel on 13/1/16.
 */
public class MultiTrackBackgroundModel extends BackgroundModel {

    private AppearanceTable appearanceTable;

    /**
     * Constructor. Creates a Bg Model with covariants according the given intervals and positions.
     *
     * @param intervals - covariants
     * @param inputPositions - positions to match against
     *
     */
    public MultiTrackBackgroundModel(List<Interval> intervals, Sites inputPositions) throws Exception {

        //check if there is an interval with the type score
        if(intervals.stream().filter(p -> p.getType() == Interval.Type.score).count() > 0){
            throw new Exception("Scored intervals not allowed");
        }

        appearanceTable = new AppearanceTable();
        appearanceTable.fillTable(intervals, inputPositions);
        positions.addAll(randPositions(appearanceTable, intervals));

        this.hash = positions.hashCode();
    }

    /**
     * Empty constructor
     */
    protected MultiTrackBackgroundModel() { }


    /**
     * Generates random positions for a given appearance table and the intervals.
     * The appearance table has to be made of the given intervals.
     *
     * @param appearanceTable - table of appearance counts
     * @param intervals - intervals to match against
     *
     * @return list of positions which are spread by the same appearance values
     */
    protected Collection<Long> randPositions(AppearanceTable appearanceTable, List<Interval> intervals){

        List<Long> sites = new ArrayList<>();
        SingleTrackBackgroundModel better = new SingleTrackBackgroundModel();

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
        //Interval outs = Intervals.sum(intervals).invert();
        Interval outs = Intervals.intersect(intervals.stream().map(Interval::invert).collect(Collectors.toList()));
        sites.addAll(better.randPositions(count, outs ,"in"));

        Collections.sort(sites);

        return sites;
    }

    public AppearanceTable getAppearanceTable() {
        return appearanceTable;
    }
}
