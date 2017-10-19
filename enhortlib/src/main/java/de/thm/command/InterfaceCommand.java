package de.thm.command;

import de.thm.genomeData.tracks.ScoredTrack;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command object to send from controller to view.
 *
 * Created by Michael Menzel on 4/2/16.
 */
public final class InterfaceCommand {
    private List<String> covariants; //list of ids of tracks that are used as covariant
    private List<String> tracks;
    private int positionCount; //count of user data positons
    private int minBg; //minimum of expected background positions
    private String originalFilename; // filename of the file the user uploaded
    private Sites sites;
    private String assembly;
    private boolean logoCovariate;
    private boolean logo;
    private List<Integer> hotspots;
    private Sites sitesBg;
    private boolean showall;

    public InterfaceCommand() {
        covariants = new ArrayList<>();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterfaceCommand that = (InterfaceCommand) o;

        return covariants == that.covariants;

    }

    @Override
    public int hashCode() {
        return (covariants.hashCode());
    }

    public int getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(int positionCount) {
        this.positionCount = positionCount;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public int getMinBg() {
        return minBg;
    }

    public void setMinBg(int minBg) {
        this.minBg = minBg;
    }

    public List<String> getCovariants() {
        return covariants;
    }

    public void setCovariants(List<String> covariants) {
        this.covariants = covariants;
    }

    public Sites getSites() {
        return sites;
    }

    public void setSites(Sites sites) { this.sites = sites; }

    public String getAssembly() {
        return this.assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public boolean getLogoCovariate() {
        return logoCovariate;
    }

    public void setLogoCovariate(boolean logoCovariate) {
        this.logoCovariate = logoCovariate;
    }

    public boolean getLogo() {
        return logo;
    }

    public void setLogo(boolean createLogo) {
        this.logo= createLogo;
    }

    public List<Integer> getHotspots() {
        return hotspots;
    }

    public void setHotspots(List<Integer> hotspots) {
        this.hotspots = hotspots;
    }

    public Sites getSitesBg() {
        return this.sitesBg;
    }

    public void setSitesBg(Sites sitesBg) {
        this.sitesBg = sitesBg;
    }

    public boolean isShowall() {
        return showall;
    }

    public void setShowall(boolean showall) {
        this.showall = showall;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks) {
        this.tracks = tracks;
    }
}
