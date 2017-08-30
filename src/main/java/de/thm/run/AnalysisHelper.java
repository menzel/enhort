package de.thm.run;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.CalcCaller;
import de.thm.exception.CovariantsException;
import de.thm.exception.NoTracksLeftException;
import de.thm.exception.TrackTypeNotAllowedExcpetion;
import de.thm.genomeData.tracks.CellLine;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.TrackPackage;
import de.thm.logo.GenomeFactory;
import de.thm.logo.LogoCreator;
import de.thm.positionData.Sites;
import de.thm.precalc.SiteFactory;
import de.thm.precalc.SiteFactoryFactory;
import de.thm.result.DataViewResult;
import de.thm.result.Result;
import de.thm.result.ResultCollector;
import de.thm.spring.command.BackendCommand;

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
    private ResultCollector runAnalysis(Sites sites, BackendCommand cmd) throws CovariantsException, NoTracksLeftException {
        List<Track> covariants = getCovariants(cmd.getCovariants(), cmd.getAssembly());
        List<Track> runTracks;
        TrackFactory trackFactory = TrackFactory.getInstance();
        Sites bg = null;
        Double smooth = 10d; //cmd.getInfluence(); //TODO use user defined value
        int minSites = cmd.getMinBg();


        if (cmd.isLogoCovariate()) {
            //bg = BackgroundModelFactory.createBackgroundModel(sites.getAssembly(), LogoCreator.createLogo(sites), minSites);

            SiteFactory factory = SiteFactoryFactory.getInstance().get(sites.getAssembly());
            bg = factory.getByLogo(LogoCreator.createLogo(sites), minSites);
        } else if (covariants.isEmpty()){
            bg = BackgroundModelFactory.createBackgroundModel(sites.getAssembly(), sites.getPositionCount()); //check if minSites is larger
        } else {
            try {
                bg = BackgroundModelFactory.createBackgroundModel(covariants, sites, minSites, smooth);
            } catch (TrackTypeNotAllowedExcpetion trackTypeNotAllowedExcpetion) {
                trackTypeNotAllowedExcpetion.printStackTrace(); //TODO handle by inform user, and set some bg
            }
        }

        if(cmd.getTracks().isEmpty()) {
            System.err.println("No tracks given in AnalysisHelper");
            throw new NoTracksLeftException("There are no tracks here");

        } else {
            runTracks = trackFactory.getTracksById(cmd.getTracks(), cmd.getAssembly());

            //check and apply custom tracks
            runTracks.addAll(cmd.getCustomTracks());

            if(runTracks.isEmpty()) {
                if(BackendController.runlevel == BackendController.Runlevel.DEBUG)
                    System.err.println("TrackFactory did not provide any tracks for given packages (" + Arrays.toString(cmd.getTracks().toArray()) + ") in AnalysisHelper");
                throw new NoTracksLeftException("There are no tracks available for this genome version and cell line");
            }
        }

        CalcCaller multi = new CalcCaller();
        return multi.execute(runTracks, sites, bg, cmd.isCreateLogo());
    }


    private ResultCollector runAnalysis(Sites sites, Sites sitesBg, BackendCommand cmd) throws Exception{

        List<Track> runTracks;
        TrackFactory trackFactory = TrackFactory.getInstance();

        if(cmd.getTracks().isEmpty()) {
            //runTracks = TrackFactory.getInstance().getTracks(cmd.getAssembly());
            System.err.println("No tracks given in AnalysisHelper");
            throw new NoTracksLeftException("There are no tracks here");

        } else {
            runTracks = trackFactory.getTracksById(cmd.getTracks(), cmd.getAssembly());

            //check and apply custom tracks
            runTracks.addAll(cmd.getCustomTracks());

            if(runTracks.isEmpty()){
                System.err.println("TrackFactory did not provide any tracks for given packages (" + Arrays.toString(cmd.getTracks().toArray()) + ") in AnalysisHelper");
                throw new NoTracksLeftException("There are no tracks available for this genome version and cell line" );
            }
        }

        CalcCaller multi = new CalcCaller();
        return multi.execute(runTracks, sites, sitesBg, cmd.isCreateLogo());
    }

    private Result returnDataTableView(GenomeFactory.Assembly assembly) {
        List<TrackPackage> packages = TrackFactory.getInstance().getTrackPackages(assembly);

        return new DataViewResult(assembly, packages, CellLine.getInstance().getCelllines());
    }


    Result runAnalysis(BackendCommand command) throws Exception {
        if(command.getSites() == null){
            // return all tracks for data table overview
            return returnDataTableView(command.getAssembly());

        }else if(command.getSitesBg() != null){
            return runAnalysis(command.getSites(), command.getSitesBg(), command);
        }
        return runAnalysis(command.getSites(), command);
    }
}
