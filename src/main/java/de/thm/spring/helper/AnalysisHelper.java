package de.thm.spring.helper;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.IntersectMultithread;
import de.thm.exception.CovariantsException;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.Sites;
import de.thm.run.Server;
import de.thm.stat.ResultCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for the spring gui to call the different intersects and background models.
 * <p>
 * Created by Michael Menzel on 4/2/16.
 */
public class AnalysisHelper {

    /**
     * Run analysis with a random distributed background with the same size as given sites
     *
     * @param input - sites to get count from
     * @return ResultCollection of the run
     */
    public static ResultCollector runAnalysis(Sites input) {
        Sites bg = BackgroundModelFactory.createBackgroundModel(input.getPositionCount());

        IntersectMultithread multi;
        multi = new IntersectMultithread();
        return multi.execute(Server.getIntervals(), input, bg);
    }

    /**
     * Run analysis with covariants.
     *
     * @param sites          - sites to match background model against.
     * @param covariantNames - covariants as list of names
     * @return Collection of Results inside a ResultCollector object
     * @throws CovariantsException - if too many covariants are supplied or an impossible combination
     */
    public static ResultCollector runAnalysis(Sites sites, List<String> covariantNames) throws CovariantsException {

        List<Track> covariants = getCovariants(covariantNames);

        Sites bg = BackgroundModelFactory.createBackgroundModel(covariants, sites);

        IntersectMultithread multi = new IntersectMultithread();
        return multi.execute(Server.getIntervals(), sites, bg);

    }

    /**
     * Converts a list of covariant names from the webinterface to a list of intervals for analysis.
     *
     * @param covariantNames - list of interval names
     * @return list of intervals with the same as given by input names
     */
    private static List<Track> getCovariants(List<String> covariantNames) {
        List<Track> selectedTracks = new ArrayList<>();
        TrackFactory loader = TrackFactory.getInstance();

        List<Track> knownTracks = loader.getAllIntervals();

        for (Track track : knownTracks) {
            if (covariantNames.contains(Integer.toString(track.getUid()))) {
                selectedTracks.add(track);
            }
        }

        return selectedTracks;
    }

}
