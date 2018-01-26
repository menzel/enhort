package de.thm.run;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.CalcCaller;
import de.thm.command.BackendCommand;
import de.thm.exception.CovariatesException;
import de.thm.exception.NoTracksLeftException;
import de.thm.genomeData.tracks.CellLine;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.TrackPackage;
import de.thm.logo.LogoCreator;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import de.thm.precalc.SiteFactory;
import de.thm.precalc.SiteFactoryFactory;
import de.thm.result.DataViewResult;
import de.thm.result.Result;
import de.thm.result.ResultCollector;
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
    private static List<Track> getCovariants(List<String> covariantNames, Genome.Assembly assembly) {

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
     * @throws CovariatesException - if too many covariants are supplied or an impossible combination
     */
    private ResultCollector runAnalysis(Sites sites, BackendCommand cmd) throws CovariatesException, NoTracksLeftException {
        List<Track> covariants = getCovariants(cmd.getCovariants(), cmd.getAssembly());
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
            f.get(60, TimeUnit.SECONDS);

        } catch (TimeoutException e) {
            logger.debug("Timeout while creating the background model", e);
            f.cancel(true);
            return null;
        } catch (InterruptedException | ExecutionException e) {
            logger.debug("Error creating the background model", e);
        }

        return runAnalysisWithBg(sites, bg[0], cmd);
    }

    /**
     * Run analysis with based on a backend command. If no tracks are supplied a base set of tracks is used
     *
     * @param sites - sites to match background model against.
     * @param cmd - command object
     *
     * @return Collection of Results inside a ResultCollector object
     * @throws NoTracksLeftException - if there are no tracks using this cmd
     */
    private ResultCollector runAnalysisWithBg(Sites sites, Sites sitesBg, BackendCommand cmd) throws NoTracksLeftException {

        List<Track> runTracks;
        TrackFactory trackFactory = TrackFactory.getInstance();

        if (cmd.getTracks().isEmpty() || !sites.getCellline().equals("Unknown")) {
            try {
                runTracks = trackFactory.getTracksByCellline(sites.getCellline(), sites.getAssembly());
            } catch (RuntimeException e) {
                try {
                    runTracks = trackFactory.getTracksByName(Arrays.asList("Known genes", "CpG Islands", "Exons", "Introns"), Genome.Assembly.hg19);
                    logger.warn("Error getting tracks {}", e);
                } catch (RuntimeException e1) {
                    runTracks = trackFactory.getTracks(Genome.Assembly.GRCh38);
                }
            }

        } else { // if there is a list of track ids given by command

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

    /**
     * Returns the data table view for known annotations for a given assembly
     *
     * @param assembly - assembly number
     * @return - Result which contains information for the data table view
     */
    private Result returnDataTableView(Genome.Assembly assembly) {
        List<TrackPackage> packages = TrackFactory.getInstance().getTrackPackages(assembly);

        return new DataViewResult(assembly, packages, CellLine.getInstance().getCelllines());
    }


    /**
     * Base method for running any analysis given a backend command.
     *
     * Based on the backend command the run method is selected and the result,
     * either a DataViewResult or ResultCollector is returned
     *
     * @param command - command which sets the params and contains any information for the run
     * @return Result of the computation
     * @throws NoTracksLeftException if there are no tracks for the given filters
     * @throws CovariatesException if the number or combination of covariates is impossible
     */
    Result runAnalysis(BackendCommand command) throws NoTracksLeftException, CovariatesException {
        switch (command.getTask()) {
            case GET_TRACKS:
                return returnDataTableView(command.getAssembly());

            case ANALYZE_BATCH:
                //TODO
                return null;

            case ANALZYE_SINGLE:
                if (command.getSitesBg() != null) {
                    return runAnalysisWithBg(command.getSites(), command.getSitesBg(), command);
                }
                return runAnalysis(command.getSites(), command);

            default:
                throw new RuntimeException("Task not runnable " + command.getTask());
        }
    }
}
