package de.thm.run;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.CalcCaller;
import de.thm.exception.CovariantsException;
import de.thm.exception.NoTracksLeftException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Wrapper for the spring gui to call the different intersects and background models.
 * <p>
 * Created by Michael Menzel on 4/2/16.
 */
class AnalysisHelper {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisHelper.class);

    /**
     * Converts a list of covariant names from the webinterface to a list of intervals for analysis.
     *
     * @param covariantNames - list of interval names
     * @param assembly - assembly name
     * @return list of intervals with the same as given by input names
     */
    private static List<Track> getCovariants(List<String> covariantNames, GenomeFactory.Assembly assembly) {

        List<Track> selectedTracks = new ArrayList<>();

        if(covariantNames.size() == 0)
            return selectedTracks;

        TrackFactory loader = TrackFactory.getInstance();

        List<Track> knownTracks = loader.getTracks(assembly);

        try {
            for (Track track : knownTracks) {
                if (covariantNames.contains(Integer.toString(track.getUid()))) {
                    selectedTracks.add(track);
                }
            }
        } catch (NullPointerException e){//TODO check in what case this is happening
            logger.error("Exception {}", e.getMessage(), e);
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
        final Sites[] bg = new Sites[1];
        Double smooth = 10d; //cmd.getInfluence(); //TODO use user defined value
        int minSites = cmd.getMinBg();
        ExecutorService pool = Executors.newFixedThreadPool(1);


        // create background //
        logger.debug("Create background model");

        Runnable backgroundCreator =  () -> {
             if (cmd.isLogoCovariate()) {
                 //bg = BackgroundModelFactory.createBackgroundModel(sites.getAssembly(), LogoCreator.createLogo(sites), minSites);

                 SiteFactory factory = SiteFactoryFactory.getInstance().get(sites.getAssembly());
                 bg[0] = factory.getByLogo(LogoCreator.createLogo(sites), minSites);
             } else if (covariants.isEmpty()) {
                 bg[0] = BackgroundModelFactory.createBackgroundModel(sites.getAssembly(), sites.getPositionCount()); //check if minSites is larger
             } else {
                 try {
                     bg[0] = BackgroundModelFactory.createBackgroundModel(covariants, sites, minSites, smooth);
                 } catch (Exception e) {
                     logger.error("Error while creating the background model", e);
                     bg[0] = null;
                 }
             }
        };

        Future f = pool.submit(backgroundCreator);

        try {
            f.get(30, TimeUnit.SECONDS);

        } catch (TimeoutException e) {
            logger.debug("Timeout while creating the background model", e);
            f.cancel(true);
            return null;
        } catch (InterruptedException | ExecutionException e) {
            logger.debug("Error creating the background model", e);
        }


        // collect tracks for the run //
        logger.debug("collect tracks for the run");
        if(cmd.getTracks().isEmpty()) {

            try {
                runTracks = trackFactory.getTracksByCompilation("basic", cmd.getAssembly());
            } catch (RuntimeException e){
                runTracks = trackFactory.getTracksByName(Arrays.asList("Known genes", "CpG Islands" ,"Exons", "Introns"), GenomeFactory.Assembly.hg19);
            }


        } else {
            runTracks = trackFactory.getTracksById(cmd.getTracks());

            //check and apply custom tracks
            runTracks.addAll(cmd.getCustomTracks());

            if(runTracks.isEmpty()) {
                logger.warn("TrackFactory did not provide any tracks for given packages (" + Arrays.toString(cmd.getTracks().toArray()) + ") in AnalysisHelper");
                throw new NoTracksLeftException("There are no tracks available for this genome version and cell line");
            }
        }

        logger.debug("executing the calculations now");
        CalcCaller multi = new CalcCaller();
        return multi.execute(runTracks, sites, bg[0], cmd.isCreateLogo());
    }


    private ResultCollector runAnalysis(Sites sites, Sites sitesBg, BackendCommand cmd) throws Exception{

        List<Track> runTracks;
        TrackFactory trackFactory = TrackFactory.getInstance();

        if(cmd.getTracks().isEmpty()) {
            try {
                runTracks = trackFactory.getTracksByCompilation("basic", cmd.getAssembly());
            } catch (RuntimeException e){
                runTracks = trackFactory.getTracksByName(Arrays.asList("Known genes", "CpG Islands" ,"Exons", "Introns"), GenomeFactory.Assembly.hg19);
            }

        } else {
            runTracks = trackFactory.getTracksById(cmd.getTracks());

            //check and apply custom tracks
            runTracks.addAll(cmd.getCustomTracks());

            if(runTracks.isEmpty()){
                logger.warn("TrackFactory did not provide any tracks for given packages (" + Arrays.toString(cmd.getTracks().toArray()) + ") in AnalysisHelper");
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
