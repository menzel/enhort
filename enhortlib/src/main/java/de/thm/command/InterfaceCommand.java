// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.command;

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
    private String cellline;

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

    public String getCellline() {
        return cellline;
    }

    public void setCellline(String cellline) {
        this.cellline = cellline;
    }
}
