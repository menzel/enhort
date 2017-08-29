package de.thm.genomeData.tracks;

import de.thm.logo.GenomeFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Handles the loading of all tracks. Implements factory methods for all three track types.
 * <p>
 * Created by Michael Menzel on 18/12/15.
 */
public final class TrackFactory {

    private static TrackFactory instance;
    private final Path basePath;
    private final List<Track> tracks;
    private List<TrackPackage> trackPackages;


    /**
     * Constructor. Parses the base dir and gets all tracks from files.
     * Expects three dirs with the names 'inout', 'named' and 'score' for types.
     */
    private TrackFactory() {

        if (System.getenv("HOME").contains("menzel")) {
            basePath = new File("/home/menzel/Desktop/THM/lfba/enhort/dat_small/").toPath();
        } else {
            basePath = new File("/home/mmnz21/dat/").toPath();
        }

        /*
        DBConnector connector = new DBConnector();
        connector.connect();
        System.out.println(connector.getAllTracks());
        */

        tracks = new ArrayList<>();
        trackPackages = new ArrayList<>();
    }


    public static TrackFactory getInstance() {
        if (instance == null)
            instance = new TrackFactory();
        return instance;
    }


    /**
     * Public method to load a single track by given path
     *
     * @param path to track
     * @throws IOException if something goes wrong while loading the track
     */
    public void loadTrack(Path path, GenomeFactory.Assembly assembly) throws IOException {
        Track track;
        List<Path> paths = new ArrayList<>();
        paths.add(path);
        track = loadTracks(paths, assembly).get(0);
        this.tracks.add(track);
    }

    /**
     * Loads tracks from basePath dir.
     * Call once at start. Fills this.tracks with all tracks from the dirs.
     */
    public void loadAllTracks() {

        try {

            //////////// hg19  ///////////////
            Path hg19path = this.basePath.resolve("hg19");

            List<Path> dirs19 = new ArrayList<>();
            Files.walk(Paths.get(hg19path.toString())).filter(Files::isRegularFile).forEach(dirs19::add);

            this.tracks.addAll(loadTracks(dirs19, GenomeFactory.Assembly.hg19));

            /*

            //////////// hg18  ///////////////
            Path hg18path = this.basePath.resolve("hg18");

            List<Path> dirs18 = new ArrayList<>();
            Files.walk(Paths.get(hg18path.toString())).filter(Files::isRegularFile).forEach(dirs18::add);

            this.tracks.addAll(loadTracks(dirs18, GenomeFactory.Assembly.hg18));


            //////////// hg38  ///////////////
            Path hg38path = this.basePath.resolve("hg38");

            List<Path> dirs38 = new ArrayList<>();
            Files.walk(Paths.get(hg18path.toString())).filter(Files::isRegularFile).forEach(dirs38::add);

            this.tracks.addAll(loadTracks(dirs38, GenomeFactory.Assembly.hg38));
            */

            List<String> trackPackagesNames = new ArrayList<>();

            for(Track track: tracks){
                if(trackPackagesNames.contains(track.getCellLine())){
                    //if the package exist add the track
                    trackPackages.get(trackPackagesNames.indexOf(track.getCellLine())).add(track);

                } else {
                    //if the package for this cell line does not exist create a new package and then add the track
                    TrackPackage trackPackage = new TrackPackage(track.getCellLine(), track.getAssembly(), track.getCellLine());
                    this.trackPackages.add(trackPackage);
                    trackPackagesNames.add(track.getCellLine());

                    trackPackage.add(track);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets all tracks from a single type
     *
     * @param hg19
     * @param type - Interval.Type. Type based upon dir name
     * @param path - path to the dir with files
     * @throws IOException on file problems
     */
    List<Track> loadTracks(List<Path> files, GenomeFactory.Assembly assembly) throws IOException {

        final List<Track> tracks = Collections.synchronizedList(new ArrayList<>());

        int nThreads = 8;
        if (System.getenv("HOME").contains("menzel"))
            nThreads = 4;

        ExecutorService exe = Executors.newFixedThreadPool(nThreads);

        for (Path file : files) {
            if(!file.toFile().getName().equals("chrSizes")) { //only load bed files, omit the chromosome sizes files
                FileLoader loader = new FileLoader(file, tracks, assembly);
                exe.execute(loader);
            }
        }

        exe.shutdown();

        try {
            if (!exe.awaitTermination(2, TimeUnit.MINUTES)) {
                System.err.println("Still loading track files. Stopping now");
                exe.shutdownNow();
            }

        } catch (Exception e) {
            System.err.println("Some threads were interrupted");
        }

        exe.shutdownNow();

        return tracks;
    }

    /**
     * Returns all tracks by assembly
     *
     * @param assembly - number of assembly (hg19, hg38)
     * @return all tracks with this assembly number
     */
    public List<Track> getTracks(GenomeFactory.Assembly assembly) {

        List<Track> tracks = new ArrayList<>();

        for (Track track : this.tracks) {
            if (track.getAssembly() == null || (track.getAssembly() != null && track.getAssembly().equals(assembly))) {
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
    public List<Track> getTracksByPackage(String name, GenomeFactory.Assembly assembly) {
        for (TrackPackage pack : trackPackages) {
            if (pack.getName() == name && pack.getAssembly() == assembly)
                return pack.getTrackList();
        }
        throw new RuntimeException("No TrackPackage with that name (" + name + ") and assembly (" + assembly + ")");
    }


    /**
     * Returns all known track packages.
     *
     * @return list of all packages names
     */
    public List<String> getTrackPackageNames(GenomeFactory.Assembly assembly) {
        return null; //TODO FIX this.trackPackages.stream().filter(i -> i.getAssembly() == assembly).map(TrackPackage::getName).map(Enum::toString).collect(Collectors.toList());
    }

    public List<TrackPackage> getTrackPackages(GenomeFactory.Assembly assembly) {
        return this.trackPackages;
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

        throw new RuntimeException("Track not found by id: " + id);
    }


    /**
     * Gets a track by name (case insensitive)
     *
     * @param name     - name of the track
     * @param assembly - assembly of the track
     * @return track with the give name and assembly or null if no such track exists
     */
    public Track getTrackByName(String name, GenomeFactory.Assembly assembly) {

        for (Track track : tracks) {
            if (track.getAssembly().equals(assembly) && track.getName().toLowerCase().equals(name.toLowerCase()))
                return track;
        }

        throw new RuntimeException("Could not find track " + name + ". Some parts might not be working correct. " +
                "Please check the track file and name");
    }



    public List<Track> getTracksByName(List<String> trackNames, GenomeFactory.Assembly assembly){
        List<Track> returnTracks = new ArrayList<>();

        for (Track track : tracks) {
            if (track.getAssembly().equals(assembly) && trackNames.contains(track.getName()))
                returnTracks.add(track);
        }

        if(!returnTracks.isEmpty())
            return returnTracks;

        throw new RuntimeException("Could not find tracks " + trackNames.toArray().toString() + ". Some parts might not be working correct. " +
                "Please check the track file and name");
    }



    /**
     * Factory method for scored tracks. Creates a new track based on input.
     *
     * @param starts      - list of start positions
     * @param ends        - list of end positions
     * @param names       - list of names
     * @param scores      - list of scores
     * @param name        - name of track
     * @param description - description of track
     * @return new track with all given parameters
     */
    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description) {
        return new ScoredTrack(starts, ends, names, scores, name, description, GenomeFactory.Assembly.Unknown, "");
    }

    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, GenomeFactory.Assembly assembly, String cellLine) {
        return new ScoredTrack(starts, ends, names, scores, name, description, assembly, cellLine);
    }

    public ScoredTrack createScoredTrack(long[] starts, long[] ends, String[] names, double[] scores, String name, String description, GenomeFactory.Assembly assembly) {
        return new ScoredTrack(starts, ends, names, scores, name, description, assembly, "");
    }


    /**
     * Factory method for inout tracks. Creates a new track based on input.
     *
     * @param starts      - list of start positions
     * @param ends        - list of end positions
     * @param name        - name of track
     * @param description - description of track
     * @return new track with all given parameters
     */
    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, GenomeFactory.Assembly assembly) {
        return new InOutTrack(starts, ends, name, description, assembly, "");
    }

    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, GenomeFactory.Assembly assembly, String cellLine) {
        return new InOutTrack(starts, ends, name, description, assembly, cellLine);
    }

    public Track createInOutTrack(long[] starts, long[] ends, String ex, String description, GenomeFactory.Assembly assembly) {
        return new InOutTrack(starts, ends, ex, description, assembly, "");
    }


    /**
     * Factory method for distance tracks. Creates a new track based on input.
     *
     * @param starts      - list of start positions
     * @param name        - name of track
     * @param description - description of track
     * @param hg19
     * @return new track with all given parameters
     */
    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, GenomeFactory.Assembly assembly) {
        return new DistanceTrack(starts, name, description, assembly, "");
    }

    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, GenomeFactory.Assembly assembly, String cellLine) {
        return new DistanceTrack(starts, name, description, assembly, cellLine);
    }

    public NamedTrack createNamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description, GenomeFactory.Assembly assembly, String cellLine) {
        return new NamedTrack(starts, ends, names, name, description, assembly, cellLine);
    }

    public StrandTrack createStrandTrack(List<Long> start, List<Long> end, List<Character> strands, String name, String desc, GenomeFactory.Assembly assembly, String cellLine) {
        return new StrandTrack(start, end, strands, name, desc, assembly, cellLine);
    }

    public int getTrackCount() {
        return tracks.size();
    }


    public void addTrack(Track track) {
        this.tracks.add(track);
    }


    enum Type {inout, named, distance, strand, scored}

}
