package de.thm.spring.serverStatistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Collects statistics about the usage of the web interface
 * <p>
 * Created by Michael Menzel on 10/2/16.
 */
public final class StatisticsCollector {

    private static final StatisticsCollector instance = new StatisticsCollector(new File("/tmp/log").toPath());
    private final Path logPath;
    /**
     * Statistic countings:
     */
    private int fileCount;
    private int analyseCount;
    private int sessionCount;
    private int errorCount;
    private int downloadCount;

    /**
     * Creates the collector. The known log file is parsed for values
     *
     * @param logPath - path to log file
     */
    private StatisticsCollector(Path logPath) {
        this.logPath = logPath;

        if (!logPath.toFile().exists()) {
            System.err.println("Log File does not exists");

        } else {

            try (Stream<String> lines = Files.lines(logPath)) {

                String[] values = lines.toArray(String[]::new);

                fileCount = Integer.parseInt(values[0]);
                analyseCount = Integer.parseInt(values[1]);
                sessionCount = Integer.parseInt(values[2]);
                errorCount = Integer.parseInt(values[3]);
                errorCount = Integer.parseInt(values[4]);

                lines.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static StatisticsCollector getInstance() {
        return instance;
    }

    public void addFileC() {
        fileCount++;
    }

    public void addAnaylseC() {
        analyseCount++;
    }

    public void addSessionC() {
        sessionCount++;
    }

    public void addErrorC() {
        errorCount++;
    }

    public void addDownloadC() {
        downloadCount++;
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int getFileCount() {
        return fileCount;
    }

    public int getAnalyseCount() {
        return analyseCount;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }
}
