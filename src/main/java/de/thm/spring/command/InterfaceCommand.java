package de.thm.spring.command;

import de.thm.genomeData.TrackPackage;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;

/**
 * Command object to send from controller to view.
 *
 * Created by Michael Menzel on 4/2/16.
 */
public final class InterfaceCommand {
    private List<String> covariants; //list of ids of tracks that are used as covariant
    private List<String> packageNames; // list of packages that will be used in the intersect run
    private int positionCount; //count of user data positons
    private int minBg; //minimum of expected background positions
    private String originalFilename; // filename of the file the user uploaded
    private Sites sites;
    private Double influence; //influence of positions on prob interval
    private String assembly;

    public InterfaceCommand() {
        covariants = new ArrayList<>();
        packageNames = new ArrayList<>();
        packageNames.add(TrackPackage.PackageName.Basic.toString());
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

    public List<String> getPackageNames() { return packageNames; }


    public void setPackageNames(List<String> packageNames) { this.packageNames = packageNames; }


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
}
