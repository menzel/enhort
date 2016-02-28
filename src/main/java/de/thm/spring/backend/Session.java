package de.thm.spring.backend;

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
public class Session {

    private final String key;
    private final Date date;
    private Path file;
    private ResultCollector collector;
    private String originalFilename;
    private List<TestResult> covariants;

    Session(Path file, String key, Date date) {
        this.file = file;
        this.key = key;
        this.date = date;
    }

    Session(String key, Date date) {
        this.key = key;
        this.date = date;
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
            Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
