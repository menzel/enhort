package de.thm.spring.command;

import de.thm.genomeData.tracks.Track;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backend command object to send data from interface to backend. Is immutable.
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendCommand implements Serializable, Command{
    private final List<String> covariants; //list of ids of tracks that are used as covariant
    private final int minBg; //minimum of expected background positions
    private final List<Track> customTracks;
    private final Sites sites;
    private final Sites sitesBg;
    private final double influence;
    private final GenomeFactory.Assembly assembly;
    private final boolean logoCovariate;
    private final boolean createLogo;
    private final List<String> celllines;
    private List<String> tracks;


    public BackendCommand(Sites sites) {
        this.covariants = new ArrayList<>();
        this.sites = sites;
        this.minBg = sites.getPositionCount();
        this.customTracks = new ArrayList<>();
        this.influence = 1;
        this.assembly = sites.getAssembly();
        this.logoCovariate = false;
        this.createLogo = false;
        this.sitesBg = null;
        this.celllines = new ArrayList<>();
    }

    public BackendCommand(Sites sites, Sites sitesBg) {
        this.sitesBg = sitesBg;
        this.sites = sites;
        this.covariants = new ArrayList<>();
        this.minBg = sites.getPositionCount();
        this.customTracks = new ArrayList<>();
        this.influence = 1;
        this.assembly = sites.getAssembly();
        this.logoCovariate = false;
        this.createLogo = false;
        this.celllines = new ArrayList<>();
    }

    public BackendCommand(InterfaceCommand command) {
        this.covariants = command.getCovariants();
        this.minBg = command.getMinBg();
        this.sites = command.getSites();
        this.influence = command.getInfluence();
        this.customTracks = new ArrayList<>();
        this.assembly = GenomeFactory.Assembly.valueOf(command.getAssembly());
        this.logoCovariate = command.getLogoCovariate();
        this.createLogo  = command.getCreateLogo();
        this.sitesBg = command.getSitesBg();
        this.celllines = command.getCelllines();
    }

    /**
     * Constructor to get all tracks from bg.
     * Used by the data table view
     *
     * @param assembly - assembly number to get
     */
    public BackendCommand(GenomeFactory.Assembly assembly) {
        this.assembly = assembly;

        this.covariants = new ArrayList<>();
        this.sites = null;
        this.minBg =  0;
        this.customTracks = new ArrayList<>();
        this.influence = 1;
        this.logoCovariate = false;
        this.createLogo = false;
        this.sitesBg = null;
        this.celllines = new ArrayList<>();
    }

    public void addCustomTrack(List<Track> track){
        this.customTracks.addAll(track);
    }

    public List<Track> getCustomTracks() { return customTracks; }

    public List<String> getCovariants() { return covariants; }

    public Sites getSites() { return sites; }

    public int getMinBg() { return minBg; }

    public double getInfluence() { return influence; }

    public GenomeFactory.Assembly getAssembly() { return assembly; }

    public boolean isLogoCovariate() {
        return logoCovariate;
    }

    public boolean isCreateLogo() {
        return createLogo;
    }

    public Sites getSitesBg() {
        return sitesBg;
    }

    public List<String> getCelllines() {
        return celllines;
    }

    public List<String> getTracks() {
        return tracks;
    }
}
