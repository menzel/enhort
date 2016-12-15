package de.thm.run;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.IntersectMultithread;
import de.thm.exception.CovariantsException;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.genomeData.TrackPackage;
import de.thm.logo.GenomeFactory;
import de.thm.logo.LogoCreator;
import de.thm.positionData.Sites;
import de.thm.spring.command.BackendCommand;
import de.thm.stat.ResultCollector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for the spring gui to call the different intersects and background models.
 * <p>
 * Created by Michael Menzel on 4/2/16.
 */
class AnalysisHelper {

    /**
     * Converts a list of covariant names from the webinterface to a list of intervals for analysis.
     *
     * @param covariantNames - list of interval names
     * @param assembly - assembly name
     * @return list of intervals with the same as given by input names
     */
    private static List<Track> getCovariants(List<String> covariantNames, GenomeFactory.Assembly assembly) {
        List<Track> selectedTracks = new ArrayList<>();
        TrackFactory loader = TrackFactory.getInstance();

        List<Track> knownTracks = loader.getTracks(assembly);

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

    /**
     * Run analysis with covariants.
     *
     * @param sites - sites to match background model against.
     * @param cmd - covariant command object
     * @return Collection of Results inside a ResultCollector object
     * @throws CovariantsException - if too many covariants are supplied or an impossible combination
     */
    private ResultCollector runAnalysis(Sites sites, BackendCommand cmd) throws CovariantsException {
        List<Track> covariants = getCovariants(cmd.getCovariants(), cmd.getAssembly());
        List<Track> runTracks;
        TrackFactory trackFactory = TrackFactory.getInstance();
        Sites bg;
        Double influence = cmd.getInfluence();
        int minSites = cmd.getMinBg();

        if (cmd.isLogoCovariate()) {
            bg = BackgroundModelFactory.createBackgroundModel(sites.getAssembly(), LogoCreator.createLogo(sites), minSites);
        } else if (covariants.isEmpty()){
            bg = BackgroundModelFactory.createBackgroundModel(sites.getAssembly(), sites.getPositionCount()); //check if minSites is larger
        } else {
            bg = BackgroundModelFactory.createBackgroundModel(covariants, sites, minSites, influence);
        }


        if(cmd.getPackageNames().isEmpty()) {
            runTracks = trackFactory.getTracksByPackage(TrackPackage.PackageName.Basic, cmd.getAssembly());
        } else {
            runTracks =  new ArrayList<>();

            for(String packName: cmd.getPackageNames()){
                runTracks.addAll(trackFactory.getTracksByPackage(packName, cmd.getAssembly()));
            }

            //check and apply custom tracks
            runTracks.addAll(cmd.getCustomTracks());

            if(runTracks.isEmpty())
                System.err.println("TrackFactory did not provide any tracks for given packages (" + Arrays.toString(cmd.getPackageNames().toArray()) + ") in AnalysisHelper");
        }

        IntersectMultithread multi = new IntersectMultithread();
        return multi.execute(runTracks, sites, bg);

    }

    ResultCollector runAnalysis(BackendCommand command) throws CovariantsException {
        return runAnalysis(command.getSites(), command);
    }

}
