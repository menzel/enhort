package de.thm.spring.command;

import de.thm.genomeData.TrackPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 4/2/16.
 */
public class CovariantCommand {
    private List<String> covariants;
    private List<String> packageNames;
    private int positionCount;
    private int minBg;
    private String originalFilename;

    public CovariantCommand() {
        covariants = new ArrayList<>();
        packageNames = new ArrayList<>();
        packageNames.add(TrackPackage.PackageName.Basic.toString());
    }

    public List<String> getCovariants() {
        return covariants;
    }

    public void setCovariants(List<String> covariants) {
        this.covariants = covariants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CovariantCommand that = (CovariantCommand) o;

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
}
