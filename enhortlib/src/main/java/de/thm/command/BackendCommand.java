package de.thm.command;

import de.thm.genomeData.tracks.Track;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Backend command object to send data from interface to backend. Is immutable.
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendCommand implements Command {
    private final List<String> covariants; //list of ids of tracks that are used as covariant
    private final int minBg; //minimum of expected background positions
    private final List<Track> customTracks;
    private final Sites sites;
    private final Sites sitesBg;
    private final Genome.Assembly assembly;
    private final boolean logoCovariate;
    private final boolean createLogo;
    private final List<String> tracks;
    private final List<Sites> batchSites;
    private final Task task;

    public BackendCommand(List<Sites> sites, Task task) {
        this.covariants = new ArrayList<>();
        this.sites = null;
        this.minBg = sites.get(0).getPositionCount() < 10000 ? 10000 : sites.get(0).getPositionCount();
        this.customTracks = new ArrayList<>();
        this.assembly = sites.get(0).getAssembly();
        this.logoCovariate = false;
        this.createLogo = false;
        this.sitesBg = null;
        this.tracks = Collections.emptyList();
        this.task = task;
        batchSites = sites;
    }


    public BackendCommand(Sites sites, Task task) {
        this.covariants = new ArrayList<>();
        this.sites = sites;
        this.minBg = sites.getPositionCount() < 10000 ? 10000: sites.getPositionCount();
        this.customTracks = new ArrayList<>();
        this.assembly = sites.getAssembly();
        this.logoCovariate = false;
        this.createLogo = false;
        this.sitesBg = null;
        this.tracks = Collections.emptyList();
        this.task = task;
        batchSites = null;
    }

    public BackendCommand(Sites sites, Sites sitesBg, Task task) {
        this.sitesBg = sitesBg;
        this.sites = sites;
        this.covariants = new ArrayList<>();
        this.minBg = sites.getPositionCount();
        this.customTracks = new ArrayList<>();
        this.assembly = sites.getAssembly();
        this.logoCovariate = false;
        this.createLogo = false;
        this.tracks = Collections.emptyList();
        this.task = task;
        batchSites = null;
    }

    public BackendCommand(InterfaceCommand command, Task task) {
        this.covariants = command.getCovariants();
        this.minBg = command.getMinBg();
        this.sites = command.getSites();
        this.customTracks = new ArrayList<>();
        this.assembly = Genome.Assembly.valueOf(command.getAssembly());
        this.logoCovariate = command.getLogoCovariate();
        this.createLogo = command.getLogo() || this.logoCovariate;
        this.sitesBg = command.getSitesBg();
        this.tracks =  command.getTracks();
        this.task = task;
        batchSites = null;
    }

    /**
     * Constructor to get all tracks from bg.
     * Used by the data table view
     *
     * @param assembly - assembly number to get
     */
    public BackendCommand(Genome.Assembly assembly, Task task) {
        this.assembly = assembly;

        this.covariants = new ArrayList<>();
        this.sites = null;
        this.minBg =  0;
        this.customTracks = new ArrayList<>();
        this.logoCovariate = false;
        this.createLogo = false;
        this.sitesBg = null;
        this.tracks = Collections.emptyList();
        this.task = task;
        batchSites = null;
    }

    public void addCustomTrack(List<Track> track){
        this.customTracks.addAll(track);
    }

    public List<Track> getCustomTracks() { return customTracks; }

    public List<String> getCovariants() { return covariants; }

    public Sites getSites() { return sites; }

    public int getMinBg() { return minBg; }

    public Genome.Assembly getAssembly() { return assembly; }

    public boolean isLogoCovariate() {
        return logoCovariate;
    }

    public boolean isCreateLogo() {
        return createLogo;
    }

    public Sites getSitesBg() {
        return sitesBg;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public Task getTask() {
        return task;
    }

    public List<Sites> getBatchSites() {
        return batchSites;
    }
}
