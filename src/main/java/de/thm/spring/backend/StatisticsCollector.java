package de.thm.spring.backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Created by Michael Menzel on 10/2/16.
 */
public class StatisticsCollector {

    private static StatisticsCollector instance = new StatisticsCollector(new File("/tmp/log").toPath());


    /**
     * Statistic countings:
     */
    private int fileCount;
    private int analyseCount;
    private int sessionCount;
    private int errorCount;
    private int downloadCount;

    private Path logPath;

    public static StatisticsCollector getInstance(){
        return instance;
    }

    private StatisticsCollector(Path logPath) {
        this.logPath = logPath;

        if(!logPath.toFile().exists()){
            System.err.println("Log File does not exists");

        } else {

            try(Stream<String> lines = Files.lines(logPath)){

                String[] values = lines.toArray(String[]::new);

                fileCount = Integer.parseInt(values[0]);
                analyseCount = Integer.parseInt(values[1]);
                sessionCount = Integer.parseInt(values[2]);
                errorCount= Integer.parseInt(values[3]);
                errorCount= Integer.parseInt(values[4]);

                lines.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void addFileC(){fileCount++;}
    public void addAnaylseC(){analyseCount++;}
    public void addSessionC(){sessionCount++;}
    public void addErrorC(){errorCount++;}
    public void addDownloadC(){downloadCount++;}


    @Override
    public String toString() {
        return "StatisticsCollector{" +
                "fileCount=" + fileCount +
                ", analyseCount=" + analyseCount +
                ", sessionCount=" + sessionCount +
                ", errorCount=" + errorCount +
                ", downloadCount=" + downloadCount+
                '}';
    }

    public void saveStats() {

        try (BufferedWriter writer = Files.newBufferedWriter(logPath)) {
            writer.write(fileCount + "\n");
            writer.write(analyseCount+ "\n");
            writer.write(sessionCount+ "\n");
            writer.write(errorCount + "\n");
            writer.write(downloadCount+ "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
