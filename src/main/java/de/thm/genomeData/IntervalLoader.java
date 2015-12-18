package de.thm.genomeData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 18/12/15.
 */
public class IntervalLoader {

    private String basePath = "/home/menzel/Desktop/THM/lfba/projekphase/dat/";
    private Map<String, Interval> intervals;
    private IntervalDumper intervalDumper;

    public IntervalLoader() {
        intervalDumper = new IntervalDumper(new File(basePath).toPath());
        intervals = new HashMap<>();

        try {
            getIntervals(basePath + "inout", Interval.Type.inout);
            getIntervals(basePath + "named", Interval.Type.named);
            getIntervals(basePath + "score", Interval.Type.score);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getIntervals(String path, Interval.Type type) throws IOException {

        //TODO check if a binary exists, call intervalDumper to get this instead of regular file

        Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(filePath -> {
            intervals.put(filePath.getFileName().toString(), new IntervalNamed(filePath.toFile(), type));
        });
    }

    public Map<String, Interval> getAllIntervals() {
        return intervals;
    }
}
