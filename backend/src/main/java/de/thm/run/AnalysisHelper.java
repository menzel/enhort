// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
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
import de.thm.result.BatchResult;
import de.thm.result.DataViewResult;
import de.thm.result.Result;
import de.thm.result.ResultCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        Double smooth = 0d; //cmd.getInfluence(); //TODO use user defined value
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
                 bg[0] = BackgroundModelFactory.createBackgroundModel(sites.getAssembly(), Math.max(sites.getPositionCount(), cmd.getMinBg()));
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

    static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, String> seen = new ConcurrentHashMap<>();
        return t -> seen.put(keyExtractor.apply(t), "") == null;
    }

    private ResultCollector runAnalysisWithBg(Sites sites, Sites sitesBg, List<Track> tracks, boolean createLogo) throws NoTracksLeftException {

        if(tracks.size() == 0){
            throw new NoTracksLeftException("There are no tracks loaded for analysis.");
        }

        CalcCaller multi = new CalcCaller();
        ResultCollector collector = multi.execute(tracks, sites, sitesBg, createLogo);

        if (collector.getResults().size() == 0) {
            throw new NoTracksLeftException("There are no tracks left as results.");
        }

        return collector;
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

        List<Track> runTracks = new ArrayList<>();
        TrackFactory trackFactory = TrackFactory.getInstance();

        if (cmd.getTracks().isEmpty()) { // when using /sample on frontend
            try {
                runTracks = trackFactory.getTracksByCellline("Unknown", cmd.getAssembly());
                runTracks = runTracks.stream().filter(t -> !t.getName().contains("Repeat")).collect(Collectors.toList());
                //runTracks.addAll(trackFactory.getTracksByCellline("", cmd.getAssembly())); // TODO get some cell line specific tracks for sample run
            } catch (RuntimeException e) {
                logger.warn("Error getting tracks from database. Ignore if you are only using custom tracks.", e);
            }

        } else { // if there is a list of track ids given by command

            runTracks = trackFactory.getTracksById(cmd.getTracks());

            if(runTracks.isEmpty()){
                logger.warn("TrackFactory did not provide any tracks for given packages (" + Arrays.toString(cmd.getTracks().toArray()) + ") in AnalysisHelper");
                throw new NoTracksLeftException("There are no tracks available for this genome version and cell line" );
            }
        }

        // always add custom tracks to run
        runTracks.addAll(cmd.getCustomTracks());
        System.out.println("cstom trks: "  + runTracks.size());
        return runAnalysisWithBg(sites, sitesBg, runTracks, cmd.isCreateLogo());

    }

    /**
     * Runs a batch analysis (multiple user sites against the same background)
     *
     * @param command    - command parameters
     * @return BatchResult - list of ResultCollectors for each site object from batch sites in the same order
     */
    private BatchResult batchAnalysis(BackendCommand command) {

        List<Sites> batchSites = command.getBatchSites();
        Sites bg;

        int bgsitecount = Math.max(command.getMinBg(),batchSites.stream().map(s -> s.getPositions().size()).max(Integer::compareTo).get());

        if (command.getSitesBg() == null)
            bg = BackgroundModelFactory.createBackgroundModel(command.getAssembly(),bgsitecount);
        else
            bg = command.getSitesBg();

        BatchResult results = new BatchResult();

        // get tracks by given packages and cellline
        List<String> packages = TrackFactory.getInstance().getPackNames(command.getAssembly()).stream()
                .filter(p -> command.getPackages().stream()
                        .anyMatch(p::contains))
                .collect(Collectors.toList());

        // filter track packages for packages that are not cell line specific
        List<String> without_cellline = Arrays.asList("Genetic", "Restr", "Other");
        List<Track> tracks = new ArrayList<>(TrackFactory.getInstance().getTracksByPackage(packages.stream()
                .filter(pack -> without_cellline.stream()
                        .anyMatch(pack::equals))
                .collect(Collectors.toList()), command.getAssembly()));

        try {
            tracks.addAll(TrackFactory.getInstance().getTracksByPackageAndCellline(packages, command.getCellline(), command.getAssembly()));

        } catch (NoTracksLeftException e) {
            logger.warn(e.toString());
        }

        /*
        //TODO Use to call create distance tracks and call for batch result histogram
        // TODO build to work with hg38 -> change id's
        if(packages.contains("Genetic") && command.getAssembly().equals(Genome.Assembly.hg19)){

            Track cpg_distance = Tracks.createDistFromInOut((InOutTrack) TrackFactory.getInstance().getTrackByDbId(1376));
            Track tss = Tracks.createDistFromInOut((InOutTrack) TrackFactory.getInstance().getTrackByDbId(1386));

            tracks.add(tss);
            tracks.add(cpg_distance);
        }
        */

        tracks = tracks.stream().filter(distinctByKey(Track::getName)).collect(Collectors.toList());

        for (Sites sites : batchSites) {

            try {
                results.addResult(runAnalysisWithBg(sites, bg, tracks, false));
            } catch (NoTracksLeftException e) {
                e.printStackTrace();
            }
        }

        return results;
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
                return batchAnalysis(command);

            case ANALZYE_SINGLE:
                if (command.getSitesBg() != null)
                    return runAnalysisWithBg(command.getSites(), command.getSitesBg(), command);
                return runAnalysis(command.getSites(), command);

            default:
                throw new RuntimeException("Task not runnable " + command.getTask());
        }
    }

}
