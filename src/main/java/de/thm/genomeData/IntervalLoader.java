package de.thm.genomeData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 18/12/15.
 */
public class IntervalLoader {

    private Path basePath = new File("/home/menzel/Desktop/THM/lfba/projekphase/dat/").toPath();
    private Map<String, Interval> intervals;
    private IntervalDumper intervalDumper;

    /**
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
     *
     * @param path
     * @param type
     * @throws IOException
     */
    private void getIntervals(Path path, Interval.Type type) throws IOException {


        Files.walk(Paths.get(path.toString())).filter(Files::isRegularFile).forEach(filePath -> {
            String filename = filePath.getFileName().toString();
            Interval interval = loadInterval(filePath.toFile(), type);

            intervals.put(filename, interval);
        });
    }

    /**
     *
     * @param file
     * @param type
     * @return
     */
    private Interval loadInterval(File file, Interval.Type type) {

        if(intervalDumper.exists(file.getName())){
            return intervalDumper.getInterval(new File(file.getName()));

        } else{
            Interval interval = new Interval(file, type);
            intervalDumper.dumpInterval(interval, type.toString()+ "/" + file.getName());
            return  interval;
        }
    }

    public Map<String, Interval> getAllIntervals() {
        return intervals;
    }
}
