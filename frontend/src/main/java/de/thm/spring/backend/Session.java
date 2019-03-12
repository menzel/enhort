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
package de.thm.spring.backend;

import de.thm.genomeData.tracks.SerializeableInOutTrack;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Holds a single session for a user. The session includes a key (http session key) and a date when it was created.
 * <p>
 * The session also can have references to results, a input file, covariants, and the original filename.
 * <p>
 * If a session is needed let the Session class create one.
 * <p>
 * Created by Michael Menzel on 10/2/16.
 */
public final class Session {

    private final String key;
    private final Date date;
    private ResultCollector collector;
    private List<ResultCollector> oldcollectors = new ArrayList<>();
    private String originalFilename;
    private List<TestResult> covariants;
    private List<SerializeableInOutTrack> customTracks = new ArrayList<>();
    private String bgname = "Background";
    private UserData sitesBg;
    private UserData sites;
    private final BackendConnector connector;

    Session(String key, Date date, List<SerializeableInOutTrack> customTracks) {
        this.key = key;
        this.date = date;
        this.customTracks = customTracks;

        StatisticsCollector.getInstance().addSessionC();
        connector = new BackendConnector(Settings.getBackendip());
    }

    Session(String key, Date date) {
        this.key = key;
        this.date = date;

        StatisticsCollector.getInstance().addSessionC();
        connector = new BackendConnector(Settings.getBackendip());
    }

    public String getKey() {
        return key;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Session{" +
                ", key='" + key + '\'' +
                ", date=" + date +
                '}';
    }

    /**
     * Close connection to backend
     */
    void delete() {
        connector.close();
    }

    public void addCustomTrack(SerializeableInOutTrack track) {
        customTracks.add(track);
    }

    public List<SerializeableInOutTrack> getCustomTracks() {
        return this.customTracks;
    }

    public ResultCollector getCollector() {
        return collector;
    }

    public void setCollector(ResultCollector collector) {
        if(this.oldcollectors != null)
            addOldcollectors(collector);
        this.collector = collector;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public List<TestResult> getCovariants() {
        if (covariants == null)
            covariants = new ArrayList<>();
        return covariants;
    }

    public void setCovariants(List<TestResult> covariants) {
        this.covariants = covariants;
    }

    public void setBgFilename(String bgname) {
        this.bgname = bgname;
    }

    public String getBgname() {
        return bgname;
    }

    public void setBgSites(UserData sitesBg) {
        this.sitesBg = sitesBg;
    }

    public UserData getSitesBg() {
        return sitesBg;
    }

    public UserData getSites() {
        return sites;
    }

    public void setSites(UserData sites) {
        this.sites = sites;
    }

    public BackendConnector getConnector() {
        return connector;
    }

    public List<ResultCollector> getOldcollectors() {
        return oldcollectors;
    }

    private void addOldcollectors(ResultCollector col) {

        if(oldcollectors.size() > 3)
            oldcollectors.remove(3);
        this.oldcollectors.add(0,col);
    }
}
