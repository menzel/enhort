package de.thm.genomeData;

import de.thm.genomeData.Interval.Type;
import de.thm.misc.ChromosomSizes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Handles the loading of all intervals
 *
 * Created by Michael Menzel on 18/12/15.
 */
public class IntervalFactory {

    private static IntervalFactory instance;
    private final Path basePath = new File("/home/menzel/Desktop/THM/lfba/projekphase/dat/").toPath();
    private final Map<String, Interval> intervals;
    private final IntervalDumper intervalDumper;
    private final List<IntervalPackage> packageList;

    /**
     * Constructor. Parses the base dir and gets all intervals from files.
     * Expects three dirs with the names 'inout', 'named' and 'score' for types.
     *
     */
    private IntervalFactory() {
        intervalDumper = new IntervalDumper(basePath);
        intervals = new HashMap<>();

        packageList = new ArrayList<>();

        try {
            getIntervals(basePath.resolve("inout"), Type.inout);
            //getIntervals(basePath.resolve("broadHistone"), Interval.Type.inout);
            getIntervals(basePath.resolve("named"), Type.named);
            getIntervals(basePath.resolve("score"), Type.score);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static IntervalFactory getInstance(){
        if(instance == null)
            instance = new IntervalFactory();
        return  instance;
    }

    /**
     * Gets all intervals from a single type
     *
     * @param path - path to the dir with files
     * @param type - Interval.Type. Type based upon dir name
     * @throws IOException on file problems
     */
    private void getIntervals(Path path, Type type) throws IOException {


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
    private Interval loadInterval(File file, Type type) {

        if(intervalDumper.exists(file.getName())){
            return intervalDumper.getInterval(new File(file.getName()));

        } else{
            Interval interval =  initIntervalfromFile(file, type);
            intervalDumper.dumpInterval(interval, file.getName());
            return  interval;
        }
    }


    /**
     * Loads interval data from a bed file. Calls handleParts to handle each line
     *
     * @param file - file to parse
     * @param type - type of interval
     */
    private Interval initIntervalfromFile(File file, Type type){

        String name = "";
        String description = "";
        int length = 0;
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        try {
            length = new Long(Files.lines(file.toPath()).count()).intValue();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Long> starts = new ArrayList<>(length);
        List<Long> ends = new ArrayList<>(length);
        List<String> names = new ArrayList<>(length);
        List<Double> scores = new ArrayList<>(length);

        try(Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)){
            Iterator<String> it = lines.iterator();

            Pattern header = Pattern.compile("track fullname=.(.*). description=.(.*).."); //TODO . are "
            Pattern entry = Pattern.compile("chr(\\d{1,2}|X|Y)\\s(\\d*)\\s(\\d*).*");

            while(it.hasNext()){
                String line = it.next();
                Matcher header_matcher = header.matcher(line);
                Matcher line_matcher = entry.matcher(line);

                if(header_matcher.matches()){
                    name = header_matcher.group(1);
                    description = header_matcher.group(2);

                } else if (line_matcher.matches()){
                    String[] parts = line.split("\t");

                    long offset = chrSizes.offset(parts[0]); //handle null pointer exc if chromosome name is not in list

                    starts.add(Long.parseLong(parts[1]) + offset);
                    ends.add(Long.parseLong(parts[2])+ offset);

                    names.add(parts[3].intern());

                    if(parts.length > 4 && parts[4] != null)
                        scores.add(Double.parseDouble(parts[4]));
                    else
                        scores.add(.0);
                }
            }

            lines.close();

            return new ImmutableInterval(starts, ends, names, scores, name, file.getName(), description, type);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Map<String, Interval> getAllIntervals() {
        return intervals;
    }

    public List<Interval> getIntervalsByPackage(IntervalPackage.PackageName name){
        for(IntervalPackage pack: packageList){
            if(pack.getName() == name)
                return pack.getIntervalList();
        }
        return null;
    }

    public Interval getIntervalById(int id) {

        for(String keys: intervals.keySet()){
            if(intervals.get(keys).getUid() == id){
                return intervals.get(keys);
            }
        }

        return null;
    }
}
