package de.thm.spring.command;

import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 4/2/16.
 */
public class CovariantCommand {
    private List<String> covariants;
    private String filepath;
    private int positionCount;
    private String originalFilename;
    private Sites userData;

    public CovariantCommand() {
        covariants = new ArrayList<>();
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

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
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

    public Sites getUserData() {
        return userData;
    }

    public void setUserData(Sites userData) {
        this.userData = userData;
    }
}
