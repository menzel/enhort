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
class MultiTrackBackgroundModel implements Sites{

    private AppearanceTable appearanceTable;
    private List<Long> positions = new ArrayList<>();

    /**
     * Constructor. Creates a Bg Model with covariants according the given intervals and positions.
     *
     * @param intervals - covariants
     * @param inputPositions - positions to match against
     *
     */
    MultiTrackBackgroundModel(List<Interval> intervals, Sites inputPositions) {

        appearanceTable = new AppearanceTable();
        appearanceTable.fillTable(intervals, inputPositions);
        positions.addAll(randPositions(appearanceTable, intervals));
    }

    /**
     * Empty constructor
     */
    MultiTrackBackgroundModel() { }


    /**
     * Generates random positions for a given appearance table and the intervals.
     * The appearance table has to be made of the given intervals.
     *
     * @param appearanceTable - table of appearance counts
     * @param intervals - intervals to match against
     *
     * @return list of positions which are spread by the same appearance values
     */
    Collection<Long> randPositions(AppearanceTable appearanceTable, List<Interval> intervals){

        List<Long> sites = new ArrayList<>();
        SingleTrackBackgroundModel better = new SingleTrackBackgroundModel();

        for(String app: appearanceTable.getKeySet()){
            if(app.compareTo("[]") == 0){
                continue;
            }

            int count = appearanceTable.getAppearance(app);
            List<Interval> currentIntervals = appearanceTable.translate(app, intervals);
            List<Interval> negativeIntervals = appearanceTable.translateNegative(intervals, app);

            currentIntervals.addAll(negativeIntervals.stream().map(Intervals::invert).collect(Collectors.toList()));

            Interval interval = Intervals.intersect(currentIntervals);

            sites.addAll(better.randPositions(count,interval ,"in"));
        }

        int count = appearanceTable.getAppearance("[]");
        //Interval outs = Intervals.sum(intervals).invert();
        Interval outs = Intervals.intersect(intervals.stream().map(Intervals::invert).collect(Collectors.toList()));
        sites.addAll(better.randPositions(count, outs ,"in"));

        Collections.sort(sites);

        return sites;
    }

    AppearanceTable getAppearanceTable() {
        return appearanceTable;
    }

    @Override
    public void addPositions(Collection<Long> values) {
        this.positions.addAll(values);
    }

    @Override
    public List<Long> getPositions() {
        return this.positions;
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
