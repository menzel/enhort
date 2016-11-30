package de.thm.genomeData;

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
import java.util.stream.Collectors;

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

            tmp = getTracks(basePath.resolve("inout"), Type.inout, GenomeFactory.Assembly.hg19);
            this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Basic, "Basic tracks.", GenomeFactory.Assembly.hg19));
            this.tracks.addAll(tmp);

            tmp = getTracks(basePath.resolve("distanced"), Type.distance, GenomeFactory.Assembly.hg19);
            this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Distance, "Distances", GenomeFactory.Assembly.hg19));
            this.tracks.addAll(tmp);


            //////////// hg38  ///////////////
            basePath = this.basePath.resolve("hg38"); //convert basePath to a local variable and set to hg38 dir

            tmp = getTracks(basePath.resolve("inout"), Type.inout, GenomeFactory.Assembly.hg38);
            this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Basic, "Basic tracks.", GenomeFactory.Assembly.hg38));
            this.tracks.addAll(tmp);



            //only load all tracks when running on the big server
            if(!System.getenv("HOME").contains("menzel")) {


                tmp = getTracks(basePath.resolve("named"), Type.named, GenomeFactory.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Basic, "Basic tracks.", GenomeFactory.Assembly.hg19));
                this.tracks.addAll(tmp);

                tmp = getTracks(basePath.resolve("score"), Type.scored, GenomeFactory.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Expression, "Expression scores", GenomeFactory.Assembly.hg19));
                this.tracks.addAll(tmp);




                tmp = getTracks(basePath.resolve("tf"), Type.inout, GenomeFactory.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.TFBS, "Transcription factor binding sites", GenomeFactory.Assembly.hg19));
                this.tracks.addAll(tmp);

                tmp = getTracks(basePath.resolve("restriction_sites"), Type.inout, GenomeFactory.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Restriction_sites, "Restriction sites", GenomeFactory.Assembly.hg19));
                this.tracks.addAll(tmp);

                tmp = getTracks(basePath.resolve("broadHistone"), Type.inout, GenomeFactory.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Histone, "Histone modifications", GenomeFactory.Assembly.hg19));
                this.tracks.addAll(tmp);

                tmp = getTracks(basePath.resolve("OpenChrom"), Type.inout, GenomeFactory.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.OpenChrom, "Open Chromatin", GenomeFactory.Assembly.hg19));
                this.tracks.addAll(tmp);


                tmp = getTracks(basePath.resolve("repeats_by_name"), Type.inout, GenomeFactory.Assembly.hg19);
                this.trackPackages.add(new TrackPackage(tmp, TrackPackage.PackageName.Repeats_by_name, "Repeats by name", GenomeFactory.Assembly.hg19));
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
    private List<Track> getTracks(Path path, Type type, GenomeFactory.Assembly assembly) throws IOException {

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
    public List<Track> getTracks(GenomeFactory.Assembly assembly) {

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
    public List<Track> getTracksByPackage(TrackPackage.PackageName name, GenomeFactory.Assembly assembly) {
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
    public List<Track> getTracksByPackage(String packName, GenomeFactory.Assembly assembly) throws IllegalArgumentException{
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
    public List<String> getTrackPackageNames(GenomeFactory.Assembly assembly){
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
    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, GenomeFactory.Assembly assembly, Track.CellLine cellLine) {
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
    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, GenomeFactory.Assembly assembly) {
        return new InOutTrack(starts, ends, name, description, assembly, null);
    }
    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, GenomeFactory.Assembly assembly, Track.CellLine cellLine) {
        return new InOutTrack(starts, ends, name, description, assembly, cellLine);
    }


     /**
     * Factory method for distance tracks. Creates a new track based on input.
     *
     * @param starts - list of start positions
     * @param name - name of track
     * @param description - description of track
     *
     * @param hg19
      * @return new track with all given parameters
     */
    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, GenomeFactory.Assembly assembly) {
        return new DistanceTrack(starts, name, description, assembly, null);
    }
    public DistanceTrack createDistanceTrack(List<Long> starts, String name, String description, GenomeFactory.Assembly assembly, Track.CellLine cellLine) {
        return new DistanceTrack(starts, name, description, assembly, cellLine);
    }

    public NamedTrack createNamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description, GenomeFactory.Assembly assembly, Track.CellLine cellLine) {
        return new NamedTrack(starts,ends, names, name, description, assembly, cellLine);
    }

    public int getTrackCount() {
        return tracks.size();
    }


    public void addTrack(Track track){
        this.tracks.add(track);
    }


    enum Type {inout, named, distance, scored}

}
