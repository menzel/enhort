package de.thm.backgroundModel;

import de.thm.genomeData.Track;
import de.thm.genomeData.Tracks;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a background model which can use many covariants.
 * <p>
 * Created by Michael Menzel on 13/1/16.
 */
class MultiTrackBackgroundModel implements Sites {

    private AppearanceTable appearanceTable;
    private List<Long> positions = new ArrayList<>();

    /**
     * Constructor. Creates a Bg Model with covariants according the given intervals and positions.
     *
     * @param tracks         - covariants
     * @param inputPositions - positions to match against
     */
    MultiTrackBackgroundModel(List<Track> tracks, Sites inputPositions, int minSites) {

        appearanceTable = new AppearanceTable(minSites);
        appearanceTable.fillTable(tracks, inputPositions);
        positions.addAll(randPositions(appearanceTable, tracks));
    }

    /**
     * Empty constructor
     */
    MultiTrackBackgroundModel() {
    }


    /**
     * Generates random positions for a given appearance table and the intervals.
     * The appearance table has to be made of the given intervals.
     *
     * @param appearanceTable - table of appearance counts
     * @param tracks          - intervals to match against
     * @return list of positions which are spread by the same appearance values
     */
    Collection<Long> randPositions(AppearanceTable appearanceTable, List<Track> tracks) {

        List<Long> sites = new ArrayList<>();
        SingleTrackBackgroundModel better = new SingleTrackBackgroundModel();

        for (String app : appearanceTable.getKeySet()) {
            if (app.compareTo("[]") == 0) {
                continue;
            }

            int count = appearanceTable.getAppearance(app);
            List<Track> currentTracks = appearanceTable.translate(app, tracks);
            List<Track> negativeTracks = appearanceTable.translateNegative(tracks, app);

            currentTracks.addAll(negativeTracks.stream().map(Tracks::invert).collect(Collectors.toList()));

            Track track = Tracks.intersect(currentTracks);
            if(Tracks.sumOfIntervals(track, "in") < 1000){
                //TODO add some pseudocount
            } else {
                sites.addAll(better.randPositions(count, track, "in"));
            }
        }

        int count = appearanceTable.getAppearance("[]");
        //Interval outs = Intervals.sum(intervals).invert();
        Track outs = Tracks.intersect(tracks.stream().map(Tracks::invert).collect(Collectors.toList()));
        sites.addAll(better.randPositions(count, outs, "in"));

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
