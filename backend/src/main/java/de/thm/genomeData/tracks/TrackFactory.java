package de.thm.genomeData.tracks;

import de.thm.genomeData.sql.DBConnector;
import de.thm.misc.Genome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Handles the loading of all tracks. Implements factory methods for all three track types.
 * <p>
 * Created by Michael Menzel on 18/12/15.
 */
public final class TrackFactory {

    private static TrackFactory instance;
    private final List<Track> tracks;
    private List<TrackPackage> trackPackages;
    private final Logger logger = LoggerFactory.getLogger(TrackFactory.class);


    /**
     * Constructor. Parses the base dir and gets all tracks from files.
     * Expects three dirs with the names 'inout', 'named' and 'score' for types.
     */
    private TrackFactory() {

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
    public void loadTrack(DBConnector.TrackEntry entry) {
        FileLoader loader = new FileLoader(entry, tracks);
        loader.run();
    }

    /**
     * Loads tracks from basePath dir.
     * Call once at start. Fills this.tracks with all tracks from the dirs.
     */
    public void loadAllTracks() {

        final List<Track> tracks = Collections.synchronizedList(new ArrayList<>());

        DBConnector connector  = new DBConnector();
        connector.connect();

        //List<DBConnector.TrackEntry> allTracks = connector.getAllTracks("WHERE (cellline != 'Unknown' OR filesize < 100000) and file like '%inout%' ORDER BY filesize ASC ");
        //List<DBConnector.TrackEntry> allTracks = connector.getAllTracks("WHERE (cellline != 'Unknown' OR filesize < 100000) ORDER BY filesize ASC ");
        List<DBConnector.TrackEntry> allTracks = connector.getAllTracks("WHERE file like '%inout%' OR file like '%score%' ORDER BY filesize ASC ");

        int nThreads = 32;
        if (System.getenv("HOME").contains("menzel")) nThreads = 4;
        ExecutorService exe = Executors.newFixedThreadPool(nThreads);

        for(DBConnector.TrackEntry entry: allTracks){

            FileLoader loader = new FileLoader(entry, tracks);
            exe.execute(loader);
        }

        exe.shutdown();

        try {
            if (!exe.awaitTermination(2, TimeUnit.MINUTES)) {
                logger.warn("Still loading track files. Stopping now");
                exe.shutdownNow();
            }

        } catch (Exception e) {
            logger.warn("Some threads were interrupted loading the annotations. Loaded "  + tracks.size() + " of " + allTracks.size());
        }

        exe.shutdownNow();


        //TODO use DB:
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

        logger.info("There are " + trackPackages.stream().filter(t -> t.getName() == "").count() + " packages without a name");

        this.tracks.addAll(tracks);
    }

    /**
     * Returns all tracks by assembly
     *
     * @param assembly - number of assembly (hg19, hg38)
     * @return all tracks with this assembly number
     */
    public List<Track> getTracks(Genome.Assembly assembly) {

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
    public List<Track> getTracksByPackage(String name, Genome.Assembly assembly) {
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
    public List<String> getTrackPackageNames(Genome.Assembly assembly) {
        return null; //TODO FIX this.trackPackages.stream().filter(i -> i.getAssembly() == assembly).map(TrackPackage::getName).map(Enum::toString).collect(Collectors.toList());
    }

    public List<TrackPackage> getTrackPackages(Genome.Assembly assembly) {
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
     * Returns a list of tracks by given list of Ids (as list of Strings).
     *
     * If any given Id does not exists no error is thrown.
     *
     * @param trackIds - list of ids (as String)
     * @param assembly - assembly number
     * @return
     */
    public List<Track> getTracksById(List<String> trackIds) {

        List<Integer> ids = trackIds.stream().map(n -> Integer.parseInt(n)).collect(Collectors.toList());
        return tracks.parallelStream().filter(track -> ids.contains(track.getUid())).collect(Collectors.toList());
    }



    /**
     * Gets a track by name (case insensitive)
     *
     * @param name     - name of the track
     * @param assembly - assembly of the track
     * @return track with the give name and assembly or null if no such track exists
     */
    public Track getTrackByName(String name, Genome.Assembly assembly) {

        for (Track track : tracks) {
            if (track.getAssembly().equals(assembly) && track.getName().toLowerCase().equals(name.toLowerCase()))
                return track;
        }

        throw new RuntimeException("Could not find track " + name + ". Some parts might not be working correct. " +
                "Please check the track file and name");
    }



    public List<Track> getTracksByName(List<String> trackNames, Genome.Assembly assembly){
        List<Track> returnTracks = new ArrayList<>();

        for (Track track : tracks) {
            if (track.getAssembly().equals(assembly) && trackNames.contains(track.getName()))
                returnTracks.add(track);
        }

        if(!returnTracks.isEmpty())
            return returnTracks;

        throw new RuntimeException("Could not find tracks " + Arrays.toString(trackNames.toArray()) + ". Some parts might not be working correct. " +
                "Please check the track file and name");
    }



    public List<Track> getTracksByCompilation(String name, Genome.Assembly assembly) {
        DBConnector connector = new DBConnector();
        connector.connect();

        return getTracksByName(connector.getCompilationByName(name, assembly), assembly);
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
        return new ScoredTrack(starts, ends, names, scores, name, description, Genome.Assembly.Unknown, "");
    }

    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new ScoredTrack(starts, ends, names, scores, name, description, assembly, cellLine);
    }

    public ScoredTrack createScoredTrack(long[] starts, long[] ends, String[] names, double[] scores, String name, String description, Genome.Assembly assembly) {
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
    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, Genome.Assembly assembly) {
        return new InOutTrack(starts, ends, name, description, assembly, "");
    }

    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new InOutTrack(starts, ends, name, description, assembly, cellLine);
    }

    public Track createInOutTrack(long[] starts, long[] ends, String ex, String description, Genome.Assembly assembly) {
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
    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, Genome.Assembly assembly) {
        return new DistanceTrack(starts, name, description, assembly, "");
    }

    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new DistanceTrack(starts, name, description, assembly, cellLine);
    }

    public NamedTrack createNamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new NamedTrack(starts, ends, names, name, description, assembly, cellLine);
    }

    public StrandTrack createStrandTrack(List<Long> start, List<Long> end, List<Character> strands, String name, String desc, Genome.Assembly assembly, String cellLine) {
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
