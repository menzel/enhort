package de.thm.genomeData;

import de.thm.misc.ChromosomSizes;
import de.thm.misc.PositionPreprocessor;
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
 * Handles the loading of all tracks. Implements factory methods for all three track types.
 * <p>
 * Created by Michael Menzel on 18/12/15.
 */
public final class TrackFactory {

    private static TrackFactory instance;
    private final Path basePath;

    private final List<TrackPackage> trackPackages;
    private List<Track> tracks;



    /**
     * Constructor. Parses the base dir and gets all tracks from files.
     * Expects three dirs with the names 'inout', 'named' and 'score' for types.
     */
    private TrackFactory() {

        if(System.getenv("HOME").contains("menzel")){
            basePath = new File("/home/menzel/Desktop/THM/lfba/enhort/dat/").toPath();
        } else {
            basePath = new File("/home/mmnz21/dat/").toPath();
        }


        tracks = new ArrayList<>();
        trackPackages = new ArrayList<>();
    }



    public static TrackFactory getInstance() {
        if (instance == null)
            instance = new TrackFactory();
        return instance;
    }

    /**
     * Loads tracks from basePath dir.
     * Call once at start. Fills this.tracks with all tracks from the dirs.
     *
     */
    public void loadTracks() {

        List<Track> tmp;

        try {


            //////////// hg19  ///////////////
            Path basePath = this.basePath.resolve("hg19"); //convert basePath to a local variable and set to hg19 dir

            tmp = getTracks(basePath.resolve("inout"), Type.inout, Track.Assembly.hg19);
            this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Basic, "Basic tracks.", Track.Assembly.hg19));
            this.tracks.addAll(tmp);


            //////////// hg38  ///////////////
            basePath = this.basePath.resolve("hg38"); //convert basePath to a local variable and set to hg38 dir

            tmp = getTracks(basePath.resolve("inout"), Type.inout, Track.Assembly.hg38);
            this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Basic, "Basic tracks.", Track.Assembly.hg38));
            this.tracks.addAll(tmp);



            //only load all tracks when running on the big server
            if(!System.getenv("HOME").contains("menzel")) {


                tmp = getTracks(basePath.resolve("named"), Type.named, Track.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Basic, "Basic tracks.", Track.Assembly.hg19));
                this.tracks.addAll(tmp);

                tmp = getTracks(basePath.resolve("score"), Type.scored, Track.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Expression, "Expression scores", Track.Assembly.hg19));
                this.tracks.addAll(tmp);


                tmp = getTracks(basePath.resolve("distanced"), Type.distance, Track.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Distance, "Distances", Track.Assembly.hg19));
                this.tracks.addAll(tmp);



                tmp = getTracks(basePath.resolve("tf"), Type.inout, Track.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.TFBS, "Transcription factor binding sites", Track.Assembly.hg19));
                this.tracks.addAll(tmp);

                tmp = getTracks(basePath.resolve("restriction_sites"), Type.inout, Track.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Restriction_sites, "Restriction sites", Track.Assembly.hg19));
                this.tracks.addAll(tmp);

                tmp = getTracks(basePath.resolve("broadHistone"), Type.inout, Track.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Histone, "Histone modifications", Track.Assembly.hg19));
                this.tracks.addAll(tmp);

                tmp = getTracks(basePath.resolve("OpenChrom"), Type.inout, Track.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.OpenChrom, "Open Chromatin", Track.Assembly.hg19));
                this.tracks.addAll(tmp);


                tmp = getTracks(basePath.resolve("repeats_by_name"), Type.inout, Track.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Repeats_by_name, "Repeats by name", Track.Assembly.hg19));
                this.tracks.addAll(tmp);



            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets all tracks from a single type
     *
     * @param hg19
     * @param path - path to the dir with files
     * @param type - Interval.Type. Type based upon dir name
     * @throws IOException on file problems
     */
    private List<Track> getTracks(Path path, Type type, Track.Assembly assembly) throws IOException {

        List<Path> files = new ArrayList<>();
        final List<Track> tracks = Collections.synchronizedList(new ArrayList<>());

        Files.walk(Paths.get(path.toString())).filter(Files::isRegularFile).forEach(files::add);

        ExecutorService exe = Executors.newFixedThreadPool(4);

        for (Path file : files) {
            FileLoader loader = new FileLoader(file, tracks , type, assembly);
            exe.execute(loader);
        }

        exe.shutdown();

        try {
            if(!exe.awaitTermination(120, TimeUnit.SECONDS)){
                System.err.println("Still loading track files. Stopping now");
                exe.shutdownNow();
            }

        } catch (Exception e) {
            System.err.println("Some threads were interrupted");
        }

        //TODO Test:
        exe.shutdownNow();

        return tracks;
    }

    /**
     * Returns all tracks by assembly
     *
     * @param assembly - number of assembly (hg19, hg38)
     * @return all tracks with this assembly number
     */
    public List<Track> getTracks(Track.Assembly assembly) {

        List<Track> tracks = new ArrayList<>();

        for(Track track: this.tracks){
            if(track.getAssembly().equals(assembly)){
                tracks.add(track);
            }
        }

        return tracks;
    }


    /**
     * Returns a list of tracks that belong to the given package.
     *
     * @param name - name of the package
     * @return list of tracks with package name
     */
    public List<Track> getTracksByPackage(TrackPackage.PackageName name, Track.Assembly assembly) {
        for (TrackPackage pack : trackPackages) {
            if (pack.getName() == name && pack.getAssembly() == assembly)
                return pack.getTrackList();
        }
        return null;
    }




    /**
     * Returns tracks by package name as String
     *
     * @param packName - name as String
     * @return list of tracks within the package
     * @throws IllegalArgumentException - if name is not known as package name
     */
    public List<Track> getTracksByPackage(String packName, Track.Assembly assembly) throws IllegalArgumentException{
        List<Track> tracks = getTracksByPackage(TrackPackage.PackageName.valueOf(packName), assembly);

        if(tracks != null)
            return tracks;
        return new ArrayList<>();
    }


    /**
     * Returns all known track packages.
     *
     * @return list of all packages names
     */
    public List<String> getTrackPackageNames(Track.Assembly assembly){
        return this.trackPackages.stream().filter(i -> i.getAssembly() == assembly).map(TrackPackage::getName).map(Enum::toString).collect(Collectors.toList());
    }


    /**
     * Returns track by given id
     *
     * @param id - id of track to return
     * @return track with id
     */
    public Track getTrackById(int id) {

        for (Track track : tracks) {
            if (track.getUid() == id) {
                return track;
            }
        }

        return null;
    }

    /**
     * Factory method for scored tracks. Creates a new track based on input.
     *
     * @param starts - list of start positions
     * @param ends - list of end positions
     * @param names - list of names
     * @param scores - list of scores
     * @param name - name of track
     * @param description - description of track
     *
     * @return new track with all given parameters
     */
    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description) {
        return new ScoredTrack(starts, ends, names, scores, name, description, null, null);
    }
    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, Track.Assembly assembly, Track.CellLine cellLine) {
        return new ScoredTrack(starts, ends, names, scores, name, description, assembly, cellLine);
    }

    /**
     * Factory method for inout tracks. Creates a new track based on input.
     *
     * @param starts - list of start positions
     * @param ends - list of end positions
     * @param name - name of track
     * @param description - description of track
     *
     * @return new track with all given parameters
     */
    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description) {
        return new InOutTrack(starts, ends, name, description, null, null);
    }
    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, Track.Assembly assembly, Track.CellLine cellLine) {
        return new InOutTrack(starts, ends, name, description, assembly, cellLine);
    }


     /**
     * Factory method for distance tracks. Creates a new track based on input.
     *
     * @param starts - list of start positions
     * @param name - name of track
     * @param description - description of track
     *
     * @return new track with all given parameters
     */
    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description) {
        return new DistanceTrack(starts, name, description, null, null);
    }
    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, Track.Assembly assembly, Track.CellLine cellLine) {
        return new DistanceTrack(starts, name, description, assembly, cellLine);
    }

    public NamedTrack createNamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description, Track.Assembly assembly, Track.CellLine cellLine) {
        return new NamedTrack(starts,ends, names, name, description, assembly, cellLine);
    }

    public int getTrackCount() {
        return tracks.size();
    }


    public void addTrack(Track track){
        this.tracks.add(track);
    }


    private enum Type {inout, named, distance, scored}

    private final class FileLoader implements Runnable {
        private final Path path;
        private final List<Track> tracks;
        private Type type;
        private Track.Assembly assembly;

        public FileLoader(Path path, List<Track> tracks, Type type, Track.Assembly assembly) {

            this.path = path;
            this.tracks = tracks;
            this.type = type;
            this.assembly = assembly;
        }

        @Override
        public void run() {

            Track track = initTrackfromFile(path.toFile(), type);
            //if(path.toString().contains("conservation")) saveTrack(track, path, type);
            tracks.add(track);
        }


        /**
         * Overwrites the bed file with new (preprocessed) positions
         *
         * @param track - track to write
         * @param path - path to file to overwrite
         * @param type - type of track, does not work for scored or named tracks.
         */
        public void saveTrack(Track track, Path path, Type type) {
            String header = "";
            ChromosomSizes chr = ChromosomSizes.getInstance();

            if(type == Type.inout){
                List<Long> starts = track.getStarts();
                List<Long> ends = track.getEnds();

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
         * Loads track data from a bed file. Calls handleParts to handle each line
         *  @param file - file to parse
         * @param type - type of interval
         */
        private Track initTrackfromFile(File file, Type type) {

            String name = "";
            String description = "";
            String cellline = "none";
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

                Pattern header = Pattern.compile("track fullname=.(.*). description=.(.*).( cellline=.(.*).)?"); //TODO dots (.) are "
                Pattern entry = Pattern.compile("chr(\\d{1,2}|X|Y)\\s(\\d*)\\s(\\d*).*");

                while (it.hasNext()) {
                    String line = it.next();
                    Matcher header_matcher = header.matcher(line);
                    Matcher line_matcher = entry.matcher(line);

                    if (header_matcher.matches()) {
                        name = header_matcher.group(1);
                        description = header_matcher.group(2);

                        if(header_matcher.group(3) != null)
                            cellline = header_matcher.group(3);

                    } else if (line_matcher.matches()) {
                        String[] parts = line.split("\t");

                        long start = -1;
                        long end = -2;

                        try { //handle null pointer exc if chromosome name is not in list

                            long offset = chrSizes.offset(parts[0]);

                            start = Long.parseLong(parts[1]) + offset;
                            end = Long.parseLong(parts[2]) + offset;
                        } catch (NullPointerException e){
                            System.err.println("File loader chrname "  + parts[0] + " not found in file " + file.getName());
                        }


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

                if (name.equals("")) {
                    if(file.getName().contains("."))
                        name = file.getName().substring(0, file.getName().indexOf("."));
                    else
                        name = file.getName();

                    if(name.startsWith("wgEncodeBroadHistone")){
                        name = name.substring("wgEncodeBroadHistone".length());
                    }
                }



                switch (type) {
                    case inout:
                        //return PositionPreprocessor.preprocessData(new InOutTrack(starts, ends, name, description));
                        return new InOutTrack(starts, ends, name, description, assembly, Track.CellLine.valueOf(cellline));
                    case scored:
                        return PositionPreprocessor.preprocessData(new ScoredTrack(starts, ends, names, scores, name, description, assembly, Track.CellLine.valueOf(cellline)));
                    case named:
                        return PositionPreprocessor.preprocessData(new NamedTrack(starts, ends, names, name, description, assembly, Track.CellLine.valueOf(cellline)));
                    case distance:
                        return new DistanceTrack(starts, "Distance from " + name, description, assembly, Track.CellLine.valueOf(cellline));
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
