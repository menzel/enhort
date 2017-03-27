package de.thm.spring.command;

import de.thm.genomeData.Track;
import de.thm.genomeData.TrackPackage;
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
    private final List<String> packageNames; // list of packages that will be used in the intersect run
    private final int minBg; //minimum of expected background positions
    private final List<Track> customTracks;
    private final Sites sites;
    private final Sites sitesBg;
    private final double influence;
    private final GenomeFactory.Assembly assembly;
    private final boolean logoCovariate;
    private final boolean createLogo;


    public BackendCommand(Sites sites) {
        this.covariants = new ArrayList<>();
        this.packageNames = new ArrayList<>();
        this.packageNames.add(TrackPackage.PackageName.Basic.toString());
        this.sites = sites;
        this.minBg = sites.getPositionCount();
        this.customTracks = new ArrayList<>();
        this.influence = 1;
        this.assembly = sites.getAssembly();
        this.logoCovariate = false;
        this.createLogo = false;
        this.sitesBg = null;
    }

    public BackendCommand(Sites sites, Sites sitesBg) {
        this.sitesBg = sitesBg;
        this.sites = sites;
        this.covariants = new ArrayList<>();
        this.packageNames = new ArrayList<>();
        this.packageNames.add(TrackPackage.PackageName.Basic.toString());
        this.minBg = sites.getPositionCount();
        this.customTracks = new ArrayList<>();
        this.influence = 1;
        this.assembly = sites.getAssembly();
        this.logoCovariate = false;
        this.createLogo = false;
    }

    public BackendCommand(InterfaceCommand command) {
        this.covariants = command.getCovariants();
        this.packageNames = command.getPackageNames();
        this.minBg = command.getMinBg();
        this.sites = command.getSites();
        this.influence = command.getInfluence();
        this.customTracks = new ArrayList<>();
        this.assembly = GenomeFactory.Assembly.valueOf(command.getAssembly());
        this.logoCovariate = command.getLogoCovariate();
        this.createLogo  = command.getCreateLogo();
        this.sitesBg = command.getSitesBg();
    }

    public void addCustomTrack(List<Track> track){
        this.customTracks.addAll(track);
    }

    public List<Track> getCustomTracks() { return customTracks; }

    public List<String> getCovariants() { return covariants; }

    public List<String> getPackageNames() { return packageNames; }

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
}
