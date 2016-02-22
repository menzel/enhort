package de.thm.spring.backend;

import de.thm.stat.ResultCollector;
import de.thm.stat.TestResult;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Michael Menzel on 10/2/16.
 */
public class Session {

    private final String key;
    private final Date date;
    private Path file;
    private ResultCollector collector;
    private String originalFilename;
    private List<TestResult> covariants;

    public Session(Path file, String key, Date date) {
        this.file = file;
        this.key = key;
        this.date = date;
    }

    public Session(String key, Date date) {
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

    void delete() {
        //TODO delte file

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
        if(covariants == null)
            covariants = new ArrayList<>();
        return covariants;
    }

    public void setCovariants(List<TestResult> covariants) {
        this.covariants = covariants;
    }
}
