package de.thm.spring.command;

import de.thm.genomeData.TrackPackage;
import de.thm.positionData.Sites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 11/3/16.
 */
public final class RunCommand implements Serializable{
    private List<String> covariants; //list of ids of tracks that are used as covariant
    private List<String> packageNames; // list of packages that will be used in the intersect run
    private int minBg; //minimum of expected background positions
    private Sites sites;


    public RunCommand() {
        covariants = new ArrayList<>();
        packageNames = new ArrayList<>();
        packageNames.add(TrackPackage.PackageName.Basic.toString());
    }

    public RunCommand(CovariantCommand command) {
        this.covariants = command.getCovariants();
        this.packageNames = command.getPackageNames();
        this.minBg = command.getMinBg();
        this.sites = command.getSites();

    }

    public List<String> getCovariants() {
        return covariants;
    }

    public void setCovariants(List<String> covariants) {
        this.covariants = covariants;
    }

    public List<String> getPackageNames() {
        return packageNames;
    }

    public void setPackageNames(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    public Sites getSites() {
        return sites;
    }

    public void setSites(Sites sites) {
        this.sites = sites;
    }

    public int getMinBg() {
        return minBg;
    }

    public void setMinBg(int minBg) {
        this.minBg = minBg;
    }

}
