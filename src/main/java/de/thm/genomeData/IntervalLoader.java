package de.thm.genomeData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the loading of all intervals
 *
 * Created by Michael Menzel on 18/12/15.
 */
public class IntervalLoader {

    private final Path basePath = new File("/home/menzel/Desktop/THM/lfba/projekphase/dat/").toPath();
    private Map<String, Interval> intervals;
    private IntervalDumper intervalDumper;

    /**
     * Constructor. Parses the base dir and gets all intervals from files.
     * Expects three dirs with the names 'inout', 'named' and 'score' for types.
     *
     */
    public IntervalLoader() {
        intervalDumper = new IntervalDumper(basePath);
        intervals = new HashMap<>();

        try {
            getIntervals(basePath.resolve("inout"), Interval.Type.inout);
            getIntervals(basePath.resolve("named"), Interval.Type.named);
            getIntervals(basePath.resolve("score"), Interval.Type.score);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets all intervals from a single type
     *
     * @param path - path to the dir with files
     * @param type - Interval.Type. Type based upon dir name
     * @throws IOException on file problems
     */
    private void getIntervals(Path path, Interval.Type type) throws IOException {


        Files.walk(Paths.get(path.toString())).filter(Files::isRegularFile).forEach(filePath -> {
            String filename = filePath.getFileName().toString();
            Interval interval = loadInterval(filePath.toFile(), type);

            intervals.put(filename, interval);
        });
    }

    /**
     * Loads a single Interval from a file.
     * Checks if a binary files exists and calls intervalDumper to load this if possible
     *
     * @param file - file to load
     * @param type - Interval.Typ. type of the file (inout, named, score)
     *
     * @return interval, either from binary or bed file
     */
    private Interval loadInterval(File file, Interval.Type type) {

        if(intervalDumper.exists(file.getName())){
            return intervalDumper.getInterval(new File(file.getName()));

        } else{
            Interval interval = new Interval(file, type);
            intervalDumper.dumpInterval(interval, file.getName());
            return  interval;
        }
    }

    public Map<String, Interval> getAllIntervals() {
        return intervals;
    }
}
