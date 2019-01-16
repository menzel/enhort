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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Collects statistics about the usage of the web interface
 * <p>
 * Created by Michael Menzel on 10/2/16.
 */
public final class StatisticsCollector {

    private static final StatisticsCollector instance = new StatisticsCollector(Settings.getLogfile_path());
    private final Path logPath;
    private final Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);

    /**
     * TODO use hashmap with enum as key for generic stats
     * Statistic countings:
     */
    private AtomicInteger fileCount;
    private AtomicInteger analyseCount;
    private AtomicInteger sessionCount;
    private AtomicInteger errorCount;
    private AtomicInteger downloadCount;
    private Date date;


    /**
     * Creates the collector. The known log file is parsed for values
     *
     * @param logPath - path to log file
     */
    private StatisticsCollector(Path logPath) {
        this.logPath = logPath;

        if (!logPath.toFile().exists()) {
            logger.warn("Log File does not exists: " + logPath.toAbsolutePath());
            logger.warn("Creating new file");

            fileCount = new AtomicInteger(0);
            analyseCount = new AtomicInteger(0);
            sessionCount = new AtomicInteger(0);
            errorCount = new AtomicInteger(0);
            downloadCount = new AtomicInteger(0);
            date = new Date();

            saveStats();

            try {
                logPath.toFile().createNewFile();
            } catch (IOException e){
                logger.warn("Could not create new log file");
                return;
            }

            logger.warn("New log file created and saved");

        } else {

            try (Stream<String> lines = Files.lines(logPath)) {

                String[] values = lines.toArray(String[]::new);

                fileCount = new AtomicInteger(Integer.parseInt(values[0]));
                analyseCount = new AtomicInteger(Integer.parseInt(values[1]));
                sessionCount = new AtomicInteger(Integer.parseInt(values[2]));
                errorCount = new AtomicInteger(Integer.parseInt(values[3]));
                downloadCount = new AtomicInteger(Integer.parseInt(values[4]));
                date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(values[5]);

            } catch (ArrayIndexOutOfBoundsException e) {
                logPath.toFile().delete();
                logger.error("Exception {}", "Corrupt log file", "The logfile was corrupt and was deleted. Please restart the server to enable logging again");

            } catch (IOException | ParseException e) {
                logger.error("Exception {}", e.getMessage(), e);
            }

        }
    }

    public static StatisticsCollector getInstance() {
        return instance;
    }

    public void addFileC() {
        fileCount.getAndIncrement();
    }

    public void addAnaylseC() {
        analyseCount.getAndIncrement();
    }

    public void addSessionC() {
        sessionCount.getAndIncrement();
    }

    public void addErrorC() {
        errorCount.getAndIncrement();
    }

    public void addDownloadC() {
        downloadCount.getAndIncrement();
    }


    @Override
    public String toString() {
        return "StatisticsCollector{" +
                "fileCount=" + fileCount +
                ", analyseCount=" + analyseCount +
                ", sessionCount=" + sessionCount +
                ", errorCount=" + errorCount +
                ", downloadCount=" + downloadCount +
                '}';
    }

    /**
     * Writes statistics to known file. Is called from a shutdown hook in the run.server file
     */
    public void saveStats() {

        try (BufferedWriter writer = Files.newBufferedWriter(logPath)) {
            writer.write(fileCount + "\n");
            writer.write(analyseCount + "\n");
            writer.write(sessionCount + "\n");
            writer.write(errorCount + "\n");
            writer.write(downloadCount + "\n");
            writer.write(date + "\n");

        } catch (IOException e) {
            logger.warn("Could not write log file");
        }
    }


    public int getFileCount() {
        return fileCount.intValue();
    }

    public int getAnalyseCount() {
        return analyseCount.intValue();
    }

    public int getSessionCount() {
        return sessionCount.intValue();
    }

    public int getErrorCount() {
        return errorCount.intValue();
    }

    public int getDownloadCount() {
        return downloadCount.intValue();
    }

    public Date getCreationDate(){return this.date;}
}
