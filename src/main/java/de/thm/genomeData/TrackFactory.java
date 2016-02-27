package de.thm.genomeData;

import de.thm.misc.ChromosomSizes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Handles the loading of all intervals
 *
 * Created by Michael Menzel on 18/12/15.
 */
public class TrackFactory {

    private static TrackFactory instance;
    private final Path basePath = new File("/home/menzel/Desktop/THM/lfba/projekphase/dat/").toPath();
    private final TrackDumper trackDumper;
    private final List<TrackPackage> packageList;
    private List<Track> intervals;

/**
     * Constructor. Parses the base dir and gets all intervals from files.
     * Expects three dirs with the names 'inout', 'named' and 'score' for types.
     *
     */
    private TrackFactory() {
        trackDumper = new TrackDumper(basePath);
        intervals = new ArrayList<>();

        packageList = new ArrayList<>();

        try {
            getIntervals(basePath.resolve("inout"), Type.inout);
            //getIntervals(basePath.resolve("broadHistone"), Type.inout);
            getIntervals(basePath.resolve("named"), Type.named);
            //getIntervals(basePath.resolve("score"), Type.scored);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

            public static TrackFactory getInstance(){
        if(instance == null)
            instance = new TrackFactory();
        return  instance;
    };

    /**
     * Gets all intervals from a single type
     *
     * @param path - path to the dir with files
     * @param type - Interval.Type. Type based upon dir name
     * @throws IOException on file problems
     */
    private void getIntervals(Path path, Type type) throws IOException {

        List<Path> files = new ArrayList<>();
        final List<Track> intervals = Collections.synchronizedList(new ArrayList<>());

        Files.walk(Paths.get(path.toString())).filter(Files::isRegularFile).forEach(files::add);

        ExecutorService exe = Executors.newFixedThreadPool(8);

        for(Path file: files){
            FileLoader loader = new FileLoader(file, intervals, type);
            exe.execute(loader);
        }

        exe.shutdown();

        try {
            exe.awaitTermination(30, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.intervals.addAll(intervals);
    }

    public List<Track> getAllIntervals() {
        return intervals;
    }

    public List<Track> getIntervalsByPackage(TrackPackage.PackageName name){
        for(TrackPackage pack: packageList){
            if(pack.getName() == name)
                return pack.getTrackList();
        }
        return null;
    }

    public Track getIntervalById(int id) {

        for(Track track: intervals){
            if(track.getUid() == id){
                return track;
            }
        }

        return null;
    }

    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description) {
        return new ScoredTrack(starts,ends,names,scores,name, description);
    }

    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description) {
        return new InOutTrack(starts,ends,name, description);
    }

    private enum Type {inout, named, scored}

    private final class FileLoader implements Runnable {
        private final Path path;
        private final List<Track> intervals;
        private Type type;

        public FileLoader(Path path, List<Track> intervals, Type type) {

            this.path = path;
            this.intervals = intervals;
            this.type = type;
        }

        @Override
        public void run() {

            Track track =  initIntervalfromFile(path.toFile(), type);
            intervals.add(track);
        }


        /**
         * Loads interval data from a bed file. Calls handleParts to handle each line
         *
         * @param file - file to parse
         * @param type - type of interval
         */
        private Track initIntervalfromFile(File file, Type type){

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

                if(name.equals(""))
                    name = file.getName();

                switch (type){
                    case inout:
                        return new InOutTrack(starts, ends, name, description);
                    case scored:
                        return new ScoredTrack(starts, ends, names, scores, name, description);
                    case named:
                        return new NamedTrack(starts, ends, names, name, description);
                    default:
                        return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }



    }
}
