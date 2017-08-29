package de.thm.spring.command;

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
    private List<String> celllines; // list of celllines used
    private List<String> tracks;
    private int positionCount; //count of user data positons
    private int minBg; //minimum of expected background positions
    private String originalFilename; // filename of the file the user uploaded
    private Sites sites;
    private Double influence; //influence of positions on prob interval
    private String assembly;
    private boolean logoCovariate;
    private boolean createLogo;
    private ScoredTrack hotspots;
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

    public double getInfluence() {
        if(influence == null)
            influence = 1d;
        return influence;
    }

    public void setInfluence(double influence) {
        this.influence = influence;
    }

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

    public boolean getCreateLogo() {
        return createLogo;
    }

    public void setCreateLogo(boolean createLogo) {
        this.createLogo = createLogo;
    }

    public List<Integer> getHotspots() {
        if(hotspots != null) {
            List<Double> hs = Arrays.stream(hotspots.getIntervalScore()).boxed().collect(Collectors.toList());

            double factor = 50 / Collections.max(hs);
            // change score by calc relative score to 50 (where 50 is the max), add 50 to have values ranging from 50 to 100. Then invert values to have highest values as 50% and lowest values as 100%
            // The calculated score is used as 'x' in hsl(100,100,x). Where the third param is the lightness
            return hs.stream().map(i -> i * factor + 50).map(i -> (100 - i) + 50).map(Double::intValue).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public void setHotspots(ScoredTrack hotspots) {
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

    public List<String> getCelllines() {
        return (celllines == null)? new ArrayList<>(): celllines;
    }

    public void setCelllines(List<String> celllines) {
        this.celllines = celllines;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks) {
        this.tracks = tracks;
    }
}
