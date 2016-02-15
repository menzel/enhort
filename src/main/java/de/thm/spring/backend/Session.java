package de.thm.spring.backend;

import de.thm.stat.ResultCollector;

import java.nio.file.Path;
import java.util.Date;

/**
 * Created by Michael Menzel on 10/2/16.
 */
public class Session {

    private Path file;
    private ResultCollector collector;
    private final String key;
    private final Date date;

    public Session(Path file, String key, Date date) {
        this.file = file;
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

    public void delete() {
        //TODO delte file

    }

    public ResultCollector getCollector() {
        return collector;
    }

    public void setCollector(ResultCollector collector) {
        this.collector = collector;
    }
}
