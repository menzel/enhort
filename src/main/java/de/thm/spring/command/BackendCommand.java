package de.thm.spring.command;

import de.thm.genomeData.TrackPackage;
import de.thm.positionData.Sites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backend command object to send data from interface to backend. Is immutable.
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendCommand implements Serializable{
    private final List<String> covariants; //list of ids of tracks that are used as covariant
    private final List<String> packageNames; // list of packages that will be used in the intersect run
    private final int minBg; //minimum of expected background positions
    private final Sites sites;
    private double influence;


    public BackendCommand(Sites sites) {
        covariants = new ArrayList<>();
        packageNames = new ArrayList<>();
        packageNames.add(TrackPackage.PackageName.Basic.toString());
        this.sites = sites;
        this.minBg = sites.getPositionCount();
    }

    public BackendCommand(InterfaceCommand command) {
        this.covariants = command.getCovariants();
        this.packageNames = command.getPackageNames();
        this.minBg = command.getMinBg();
        this.sites = command.getSites();
        this.influence = command.getInfluence();

    }

    public List<String> getCovariants() { return covariants; }

    public List<String> getPackageNames() { return packageNames; }

    public Sites getSites() { return sites; }

    public int getMinBg() { return minBg; }

    public double getInfluence() { return influence; }
}
