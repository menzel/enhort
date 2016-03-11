package de.thm.run;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.IntersectMultithread;
import de.thm.exception.CovariantsException;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.genomeData.TrackPackage;
import de.thm.positionData.Sites;
import de.thm.spring.command.RunCommand;
import de.thm.spring.serverStatistics.StatisticsCollector;
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
        StatisticsCollector.getInstance().addAnaylseC();

        IntersectMultithread multi = new IntersectMultithread();

        return multi.execute(TrackFactory.getInstance().getIntervalsByPackage(TrackPackage.PackageName.Basic), input, bg);
    }

    /**
     * Run analysis with covariants.
     *
     * @param sites          - sites to match background model against.
     * @param cmd - covariant command object
     * @return Collection of Results inside a ResultCollector object
     * @throws CovariantsException - if too many covariants are supplied or an impossible combination
     */
    public static ResultCollector runAnalysis(Sites sites, RunCommand cmd) throws CovariantsException {
        List<String> covariantNames = cmd.getCovariants();
        List<Track> covariants = getCovariants(covariantNames);
        List<Track> runTracks;
        TrackFactory trackFactory = TrackFactory.getInstance();

        StatisticsCollector.getInstance().addAnaylseC();

        int minSites = cmd.getMinBg();

        Sites bg = BackgroundModelFactory.createBackgroundModel(covariants, sites, minSites);

        if(cmd.getPackageNames().isEmpty()) {
            runTracks = trackFactory.getIntervalsByPackage(TrackPackage.PackageName.Basic);
        } else {
            runTracks =  new ArrayList<>();

            for(String packName: cmd.getPackageNames()){
                runTracks.addAll(trackFactory.getIntervalsByPackage(packName));
            }
        }


        IntersectMultithread multi = new IntersectMultithread();
        return multi.execute(runTracks, sites, bg);

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

        try {
            for (Track track : knownTracks) {
                if (covariantNames.contains(Integer.toString(track.getUid()))) {
                    selectedTracks.add(track);
                }
            }
        } catch (NullPointerException e){//TODO check in what case this is happening
            e.printStackTrace();
        }

        return selectedTracks;
    }

    public static ResultCollector runAnalysis(RunCommand command) throws CovariantsException {
        if(command.getCovariants().isEmpty())
            return runAnalysis(command.getSites());
        else
            return runAnalysis(command.getSites(), command);
    }

}
