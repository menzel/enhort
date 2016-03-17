package de.thm.genomeData;

import de.thm.misc.ChromosomSizes;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles the loading of all intervals
 * <p>
 * Created by Michael Menzel on 18/12/15.
 */
public final class TrackFactory {

    private static TrackFactory instance;
    //private final Path basePath = new File("/home/mmnz21/dat/").toPath();
    private final Path basePath = new File("/home/menzel/Desktop/THM/lfba/projekphase/dat/").toPath();
    private final List<TrackPackage> trackPackages;
    private List<Track> intervals;

    /**
     * Constructor. Parses the base dir and gets all intervals from files.
     * Expects three dirs with the names 'inout', 'named' and 'score' for types.
     */
    private TrackFactory() {
        intervals = new ArrayList<>();
        trackPackages = new ArrayList<>();

        loadIntervals();
    }



    public static TrackFactory getInstance() {
        if (instance == null)
            instance = new TrackFactory();
        return instance;
    }

    private void loadIntervals() {

        List<Track> tmp;

        try {
            /*
            tmp = getIntervals(basePath.resolve("inout"), Type.inout);

            tmp.addAll(getIntervals(basePath.resolve("named"), Type.named));
            this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Basic, "Basic tracks."));
            this.intervals.addAll(tmp);

            tmp = getIntervals(basePath.resolve("repeats_by_name"), Type.inout);
            this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Repeats_by_name, "Repeats by name."));
            this.intervals.addAll(tmp);
            */



            tmp = getIntervals(basePath.resolve("score"), Type.scored);
            this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Expression, "Expression scores"));
            this.intervals.addAll(tmp);


            //TODO:
            //getIntervals(basePath.resolve("broadHistone"), Type.inout);
            //getIntervals(basePath.resolve("awgSegmentation"), Type.named);
            //getIntervals(basePath.resolve("tfbs_composite"), Type.inout);



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
    private List<Track> getIntervals(Path path, Type type) throws IOException {

        List<Path> files = new ArrayList<>();
        final List<Track> intervals = Collections.synchronizedList(new ArrayList<>());

        Files.walk(Paths.get(path.toString())).filter(Files::isRegularFile).forEach(files::add);

        ExecutorService exe = Executors.newFixedThreadPool(4);

        for (Path file : files) {
            FileLoader loader = new FileLoader(file, intervals, type);
            exe.execute(loader);
        }

        exe.shutdown();

        try {
            exe.awaitTermination(300, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return intervals;
    }

    public List<Track> getAllIntervals() {
        return intervals;
    }

    public List<Track> getIntervalsByPackage(TrackPackage.PackageName name) {
        for (TrackPackage pack : trackPackages) {
            if (pack.getName() == name)
                return pack.getTrackList();
        }
        return null;
    }

    public List<String> getTrackPackageNames(){
        return this.trackPackages.stream().map(TrackPackage::getName).map(Enum::toString).collect(Collectors.toList());
    }


    public Track getIntervalById(int id) {

        for (Track track : intervals) {
            if (track.getUid() == id) {
                return track;
            }
        }

        return null;
    }

    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description) {
        return new ScoredTrack(starts, ends, names, scores, name, description);
    }

    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description) {
        return new InOutTrack(starts, ends, name, description);
    }

    public List<Track> getIntervalsByPackage(String packName) throws IllegalArgumentException{
        List <Track> tracks = getIntervalsByPackage(TrackPackage.PackageName.valueOf(packName));

        if(tracks != null)
            return tracks;
        return new ArrayList<>();
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

            Track track = initIntervalfromFile(path.toFile(), type);
            //saveTrack(track, path, type);
            intervals.add(track);
        }


        public void saveTrack(Track track, Path path, Type type) {
            String header = "";
            ChromosomSizes chr = ChromosomSizes.getInstance();

            if(type == Type.inout){
                List<Long> starts = track.getIntervalsStart();
                List<Long> ends = track.getIntervalsEnd();

                try (BufferedReader reader= Files.newBufferedReader(path)) {
                    header = reader.readLine();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    if(header.contains("track"))
                        writer.write(header + "\n");

                    for(int i = 0; i < starts.size(); i++){
                        Pair<String, Long> start = chr.mapToChr(starts.get(i));
                        Pair<String, Long> end = chr.mapToChr(ends.get(i));

                        writer.write(start.getKey() + "\t" + start.getValue() + "\t" + end.getValue() + "\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        /**
         * Loads interval data from a bed file. Calls handleParts to handle each line
         *
         * @param file - file to parse
         * @param type - type of interval
         */
        private Track initIntervalfromFile(File file, Type type) {

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

            try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                Iterator<String> it = lines.iterator();

                Pattern header = Pattern.compile("track fullname=.(.*). description=.(.*).."); //TODO . are "
                Pattern entry = Pattern.compile("chr(\\d{1,2}|X|Y)\\s(\\d*)\\s(\\d*).*");

                while (it.hasNext()) {
                    String line = it.next();
                    Matcher header_matcher = header.matcher(line);
                    Matcher line_matcher = entry.matcher(line);

                    if (header_matcher.matches()) {
                        name = header_matcher.group(1);
                        description = header_matcher.group(2);

                    } else if (line_matcher.matches()) {
                        String[] parts = line.split("\t");

                        long offset = chrSizes.offset(parts[0]); //handle null pointer exc if chromosome name is not in list

                        long start = Long.parseLong(parts[1]) + offset;
                        long end  = Long.parseLong(parts[2]) + offset;

                        if(!(start < end)) //check if interval length is positive
                            continue;

                        starts.add(start);
                        ends.add(end);

                        if(type == Type.named)
                            names.add(parts[3].intern());

                        if(type == Type.scored) {
                            names.add(parts[3].intern());

                            if (parts.length > 4 && parts[4] != null)
                                scores.add(Double.parseDouble(parts[4]));
                            else
                                scores.add(.0);
                        }
                    }
                }

                lines.close();

                if (name.equals(""))
                    name = file.getName().substring(0,file.getName().indexOf(".")); //TODO check if a . is present in name

                switch (type) {
                    case inout:
                        //return PositionPreprocessor.preprocessData(new InOutTrack(starts, ends, name, description));
                        return new InOutTrack(starts, ends, name, description);
                    case scored:
                        return new ScoredTrack(starts, ends, names, scores, name, description);
                    case named:
                        return new NamedTrack(starts, ends, names, name, description);
                    default:
                        throw new Exception("Something is wrong with this track or file");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
