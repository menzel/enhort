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
        connector = new BackendConnector();
    }

    Session(String key, Date date) {
        this.key = key;
        this.date = date;

        StatisticsCollector.getInstance().addSessionC();
        connector = new BackendConnector();
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
}
