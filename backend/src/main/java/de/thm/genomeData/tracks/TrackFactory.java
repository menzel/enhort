package de.thm.genomeData.tracks;

import de.thm.genomeData.sql.DBConnector;
import de.thm.misc.Genome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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

    private volatile static TrackFactory instance;
    private final List<Track> tracks;
    private final List<TrackPackage> trackPackages;
    private final Logger logger = LoggerFactory.getLogger(TrackFactory.class);
    private Map<String, TrackEntry> trackEntries = new HashMap<>();


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
     */
    public void loadTrack(TrackEntry entry) {
        FileLoader loader = new FileLoader(entry, tracks);
        loader.run();
    }


    /**
     * Public method to load a single track by given path
     */
    public void loadTracks(List<TrackEntry> entries) {
        List<Track> newTracks = loadByEntries(entries, Integer.MAX_VALUE);

        int n = 0; // index over known tracks

        for (Track a : this.tracks) {
            for (Track b : newTracks) {
                if (a.getUid() == b.getUid()) {
                    this.tracks.set(n, b);
                }
            }
            n++;
        }

        //TODO check if tra
    }

    /**
     * Loads tracks from basePath dir.
     * Call once at start. Fills this.tracks with all tracks from the dirs.
     */
    public void loadAllTracks() {

        List<TrackEntry> allTracks;

        DBConnector connector  = new DBConnector();
        connector.connect();

        if (System.getenv("HOME").contains("menzel")) {
            allTracks = connector.getAllTracks("WHERE file like '%inout%' ORDER BY filesize ASC LIMIT 20");
            allTracks.addAll(connector.getAllTracks("WHERE name like '%tf%' LIMIT 10"));
            allTracks.addAll(connector.getAllTracks("WHERE file like '%repeat%' LIMIT 10"));
            allTracks.addAll(connector.getAllTracks("WHERE file like '%broad%' LIMIT 10"));
            allTracks.addAll(connector.getAllTracks("WHERE file like '%rest%' LIMIT 5"));
            allTracks.addAll(connector.getAllTracks("WHERE name like '%contigs%'"));
        } else {
            allTracks = connector.getAllTracks("WHERE cellline != 'Unknown' OR file like '%inout%' ORDER BY filesize ASC ");
            allTracks.addAll(connector.getAllTracks("WHERE name like '%tf%'"));
            allTracks.addAll(connector.getAllTracks("WHERE file like '%repeat%'"));
            allTracks.addAll(connector.getAllTracks("WHERE file like '%broad%'"));
            allTracks.addAll(connector.getAllTracks("WHERE file like '%rest%'"));
            allTracks.addAll(connector.getAllTracks("WHERE name like '%contigs%'"));
            allTracks.addAll(connector.getAllTracks("WHERE type = 'scored'"));
            allTracks.addAll(connector.getAllTracks("WHERE type = 'distance'"));
            allTracks.addAll(connector.getAllTracks("WHERE type = 'named'"));
        }

        this.tracks.addAll(loadByEntries(allTracks, 20));

        //TODO use DB:
        List<String> trackPackagesNames = new ArrayList<>();

        for(Track track: tracks){
            if(trackPackagesNames.contains(track.getCellLine())){
                //if the package exist add the track
                trackPackages.get(trackPackagesNames.indexOf(track.getCellLine() + "_" + track.getAssembly())).add(track);

            } else {
                //if the package for this cell line does not exist create a new package and then add the track
                TrackPackage trackPackage = new TrackPackage(track.getCellLine() + "_" + track.getAssembly(), track.getAssembly(), track.getCellLine());
                this.trackPackages.add(trackPackage);
                trackPackagesNames.add(trackPackage.getName());

                trackPackage.add(track);
            }
        }

        allTracks.forEach(e -> trackEntries.put(e.getName(), e));
    }

    private List<Track> loadByEntries(List<TrackEntry> allTracks, int preloadLimit) {

        final List<Track> tracks = Collections.synchronizedList(new ArrayList<>());

        // filter doubled tracks
        allTracks = allTracks.stream().filter(DBConnector.distinctByKey(TrackEntry::getName)).collect(Collectors.toList());

        int nThreads = (System.getenv("HOME").contains("menzel")) ? 4 : 32;
        ExecutorService exe = Executors.newFixedThreadPool(nThreads);

        int i = 0;
        for (TrackEntry entry : allTracks) {

            if (i++ < preloadLimit) {
                FileLoader loader = new FileLoader(entry, tracks);
                exe.execute(loader);
            } else {
                List<Long> starts = Collections.emptyList();
                List<Long> ends = Collections.emptyList();

                switch (entry.getType()) {
                    case "inout":
                        tracks.add(new InOutTrack(starts, ends, entry));
                        break;
                    case "Named":
                        tracks.add(new NamedTrack(starts, ends, new ArrayList<>(), entry));
                        break;
                    case "Scored":
                        tracks.add(new ScoredTrack(starts, ends, new ArrayList<>(), new ArrayList<>(), entry));
                        break;
                    default:
                        logger.error("TrackEntry " + entry + " of type " + entry.getType() + " could not be used.");
                        break;
                }
            }
        }

        exe.shutdown();

        try {
            int timeout = (System.getenv("HOME").contains("menzel")) ? 1 : 5;

            if (!exe.awaitTermination(timeout, TimeUnit.MINUTES)) {
                logger.warn("Still loading track files. Stopping now");
                exe.shutdownNow();
            }

        } catch (Exception e) {
            logger.warn("Some threads were interrupted loading the annotations. Loaded " + tracks.size() + " of " + allTracks.size());
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
    public List<Track> getTracks(Genome.Assembly assembly) {

        // find unloaded tracks
        List<Track> todo = this.tracks.stream()
                .filter(track -> track.getStarts().length < 1) // filter those without data
                .filter(track -> track.getAssembly().equals(assembly))
                .collect(Collectors.toList());

        // Check if the tracks are all loaded, reload if not
        loadTracks(todo.parallelStream()
                .map(track -> trackEntries.get(track.getName()))  // get entry by track name
                .collect(Collectors.toList()));

        return this.tracks.stream()
                .filter(track -> track.getAssembly().equals(assembly))
                .collect(Collectors.toList());
    }


    /**
     * Returns a list of tracks that belong to the given package.
     *
     * @param name - name of the package
     * @return list of tracks with package name
     */
    public List<Track> getTracksByPackage(String name, Genome.Assembly assembly) {
        for (TrackPackage pack : trackPackages) {
            if (pack.getName().equals(name) && pack.getAssembly() == assembly)
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

    /**
     * Get all track packages by given assembly
     *
     * @param assembly - number of the assembly
     * @return a list of track packages that have the given assembly
     */
    public List<TrackPackage> getTrackPackages(Genome.Assembly assembly) {
        return this.trackPackages.stream().filter(i -> i.getAssembly() == assembly).collect(Collectors.toList());
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
     * If the track is not loaded yet the track FileLoader is invoked.
     *
     * If any given Id does not exists no error is thrown.
     *
     * @param trackIds - list of ids (as String)
     * @return list of tracks for the given ids
     */
    public List<Track> getTracksById(List<String> trackIds) {

        // Get list of ids
        List<Integer> ids = trackIds.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        // Get list of tracks from ids
        List<Track> tmpTracks = tracks.parallelStream()
                .filter(track -> ids.contains(track.getUid()))
                .collect(Collectors.toList());

        // Check if the tracks are all loaded, reload if not
        loadTracks(tmpTracks.parallelStream()
                .filter(track -> track.getStarts().length < 1) // filter those without data
                .map(track -> trackEntries.get(track.getName()))  // get entry by track name
                .collect(Collectors.toList()));

        // Get probably updated tracks.
        return tracks.parallelStream()
                .filter(track -> ids.contains(track.getUid()))
                .collect(Collectors.toList());
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
        return new ScoredTrack(starts, ends, names, scores, new TrackEntry(name, description, "Unknown", "", ""));
    }

    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new ScoredTrack(starts, ends, names, scores, new TrackEntry(name, description, assembly.toString(), cellLine, ""));
    }

    public ScoredTrack createScoredTrack(long[] starts, long[] ends, String[] names, double[] scores, String name, String description, Genome.Assembly assembly, int id) {
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
        return new InOutTrack(starts, ends, new TrackEntry(name, description, assembly.toString(), "None", ""));
    }

    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new InOutTrack(starts, ends, new TrackEntry(name, description, assembly.toString(), cellLine, ""));
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
     * @return new track with all given parameters
     */
    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, Genome.Assembly assembly) {
        return new DistanceTrack(starts, new TrackEntry(name, description, assembly.toString(), "None", ""));
    }

    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new DistanceTrack(starts, new TrackEntry(name, description, assembly.toString(), cellLine, ""));
    }

    public NamedTrack createNamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new NamedTrack(starts, ends, names, new TrackEntry(name, description, assembly.toString(), cellLine, ""));
    }

    public StrandTrack createStrandTrack(List<Long> start, List<Long> end, List<Character> strands, String name, String desc, Genome.Assembly assembly, String cellLine) {
        return new StrandTrack(start, end, strands, new TrackEntry(name, desc, assembly.toString(), cellLine, ""));
    }

    public int getTrackCount() {
        return tracks.size();
    }


    public void addTrack(Track track) {
        this.tracks.add(track);
    }

    public Map<String, TrackEntry> getTrackEntries() {
        return trackEntries;
    }



    enum Type {inout, named, distance, strand, scored}

}
