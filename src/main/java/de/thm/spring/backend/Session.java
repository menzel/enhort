package de.thm.spring.backend;

import de.thm.genomeData.Track;
import de.thm.positionData.UserData;
import de.thm.stat.ResultCollector;
import de.thm.stat.TestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private Path file;
    private ResultCollector collector;
    private String originalFilename;
    private List<TestResult> covariants;
    private List<Track> customTracks = new ArrayList<>();
    private String bgname = "Background";
    private UserData sitesBg;
    private UserData sites;

    Session(Path file, String key, Date date, List<Track> customTracks) {
        this.file = file;
        this.key = key;
        this.date = date;
        this.customTracks = customTracks;

        StatisticsCollector.getInstance().addSessionC();
    }

    Session(String key, Date date) {
        this.key = key;
        this.date = date;

        StatisticsCollector.getInstance().addSessionC();
    }

    public Path getFile() {
        return file;
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
                "file=" + file +
                ", key='" + key + '\'' +
                ", date=" + date +
                '}';
    }

    /**
     * Deletes the known file
     */
    void delete() {
        try {
            if(file != null)
                Files.deleteIfExists(file);
        } catch (IOException e) {
            //System.err.println("File is not there. Could not delete");
            // do nothing here. File seems to be unreacheable
        }
    }

    public void addCustomTrack(Track track){
        customTracks.add(track);
    }

    public List<Track> getCustomTracks(){
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
}
