// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.genomeData.tracks;

import de.thm.exception.NoTracksLeftException;
import de.thm.genomeData.sql.DBConnector;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
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
    private final Set<String> packNames = new HashSet<>();
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
     * Public method to load a single track by TrackEntry and adds them to the global visible track list
     */
    public void loadTrack(TrackEntry entry) {
        FileLoader loader = new FileLoader(entry);

        try {
            loader.call().ifPresent(this.tracks::add);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Public method to load a list of tracks by TrackEntry and adds them to the global visible track list
     */
    public void loadTracks(List<TrackEntry> entries) {
        List<Track> newTracks = loadByEntries(entries);

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

            allTracks = connector.getAllTracks("WHERE name like '%ontigs'");
            allTracks = connector.getAllTracks(" WHERE directory like '%genetic%' and genome_assembly = 'hg19' ORDER BY lines ASC ");
            /*
            allTracks.addAll(connector.getAllTracks("WHERE genome_assembly = 'hg19' and directory like '%encode%HeLa%'"));
            allTracks.addAll(connector.getAllTracks("WHERE genome_assembly = 'hg19' and directory like '%custom%'"));
            //allTracks = connector.getAllTracks("WHERE bed_filename = 'SRX062365.05.bed'");
            //allTracks = connector.getAllTracks(" WHERE genome_assembly = 'hg19' ORDER BY lines ASC LIMIT 3000");
            */
        } else {

            //allTracks = connector.getAllTracks("WHERE (cell_line NOT like '%GM%' or cell_line like '%GM12878' or name like '%POLR%')");
            allTracks = connector.getAllTracks("WHERE (name not like '%POLR%' or name not like '%expression%')");
            /*
            allTracks = connector.getAllTracks(" WHERE directory like '%genetic%' and genome_assembly = 'hg19' ORDER BY lines ASC ");
            allTracks.addAll(connector.getAllTracks("WHERE cell_line like 'HeLa%'"));
            allTracks.addAll(connector.getAllTracks("WHERE cell_line like 'K-562'"));
            //allTracks.addAll(connector.getAllTracks("WHERE cell_line like '%PS%'"));
            allTracks.addAll(connector.getAllTracks("WHERE cell_line like '%CD%'"));
            allTracks.addAll(connector.getAllTracks("WHERE cell_line like '%GM12878%'"));
            allTracks.addAll(connector.getAllTracks("WHERE cell_line like '%Hep-G2%'"));
            allTracks.addAll(connector.getAllTracks("WHERE cell_line like '%A-549%'"));
            */
        }

        logger.info("Trying to load " + allTracks.size() + " tracks");
        this.tracks.addAll(loadByEntries(allTracks));

        List<TrackEntry> customTracks = new ArrayList<>();

        // Add custom tracks
        if (System.getenv("HOME").contains("menzel")) {
            try {

                Files.walk(Paths.get("/home/menzel/Desktop/THM/promotion/enhort/dat/stefan/custom"))
               .filter(Files::isRegularFile)
               .filter(f -> f.getFileName().toString().endsWith(".bed"))
               .forEach(f -> customTracks.add(new TrackEntry(f.getFileName().toString(),"Custom track: " + f.getFileName(),
                        "/home/menzel/Desktop/THM/promotion/enhort/dat/stefan/custom/" + f.getFileName().toString(), "inout", Genome.Assembly.GRCh38.toString(), "Unknown", 14003120, "Genetic", 1412123+f.getFileName().hashCode(), "nosource", "nourl")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("Trying to load " + customTracks.size() + " custom tracks");
        customTracks.forEach(e -> trackEntries.put(e.getName(), e));
        this.tracks.addAll(loadByEntries(customTracks));

        try {
            tracks.add(Tracks.createDistFromInOut(getTrackById(1386)));
            tracks.add(Tracks.createDistFromInOut(getTrackById(1376)));
        } catch (RuntimeException e) { //if running locally the track might not be found, tell and ignore
            logger.error("TSS and CpG tracks are not available. Skipping distance track build.");
        }

        //TODO use DB:
        List<String> trackPackagesNames = new ArrayList<>();

        for(Track track: tracks){

            packNames.add(track.getPack());

            if (trackPackagesNames.contains(track.getCellLine() + "_" + track.getAssembly())) {
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


    }

    /**
     * Loads all tracks given by TrackEntry objects.
     * <p>
     * Stops after a timeout of a few minutes.
     * <p>
     * Returns a list of Track objects, which can either be a stub (without data) or with data.
     *
     * @param allTracks - tracks to load
     * @return list of tracks
     */
    private List<Track> loadByEntries(List<TrackEntry> allTracks) {

        // filter doubled tracks
        // allTracks = allTracks.stream().filter(DBConnector.distinctByKey(TrackEntry::getName)).collect(Collectors.toList());

        int nThreads = (System.getenv("HOME").contains("menzel")) ? 8 : 32;
        ExecutorService exe = Executors.newFixedThreadPool(nThreads);
        CompletionService<Track> completionService = new ExecutorCompletionService<>(exe);

        List<Future<Optional<Track>>> futures = new ArrayList<>();

        for (TrackEntry entry : allTracks) {

            FileLoader loader = new FileLoader(entry);
            Future<Optional<Track>> t = exe.submit(loader);
            futures.add(t);
        }

        exe.shutdown();

        try {
            int timeout = (System.getenv("HOME").contains("menzel")) ? 60 : 60 * 2;
            completionService.poll(timeout, TimeUnit.SECONDS);

            logger.warn("Still loading track files. Stopping now");

        } catch (Exception e) {
            logger.warn("Some threads were interrupted loading the annotations. Loaded " + tracks.size() + " of " + allTracks.size());
        }

        List<Track> local = new ArrayList<>();

        futures.stream().filter(Future::isDone).forEach(f -> {
            try {
                f.get().ifPresent(local::add);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        /*
        new Thread(() -> futures.forEach(f -> {
            try{
                f.get().ifPresent(local::add);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        })).start();
        */

        return local;
    }

    /**
     * Returns all tracks by assembly
     *
     * @param assembly - number of assembly (hg19, hg38)
     * @return all tracks with this assembly number
     */
    public List<Track> getTracks(Genome.Assembly assembly) {

        return this.tracks.stream()
                .filter(track -> track.getAssembly().equals(assembly))
                .collect(Collectors.toList());
    }


    /**
     * Returns a list of tracks that belong to the given package.
     *
     * @param  cellline -  of the package
     * @return list of tracks with package
     */
    public List<Track> getTracksByCellline(String cellline, Genome.Assembly assembly) {

        for (TrackPackage pack : trackPackages) {
            if (pack.getCellLine().compareToIgnoreCase(cellline) == 0 && pack.getAssembly() == assembly)
                return pack.getTrackList();
        }
        throw new RuntimeException("No TrackPackage with that  (" + cellline + ") and assembly (" + assembly + ")");
    }


    public Collection<Track> getTracksByPackageAndCellline(List<String> packages, String cellline, Genome.Assembly assembly) throws NoTracksLeftException {

        List<Track> returnTracks = new ArrayList<>();

        for (Track track : tracks) {
            if (track.getAssembly().equals(assembly) && track.getCellLine().equals(cellline) && packages.stream().anyMatch(p -> p.equals(track.getPack())))
                returnTracks.add(track);
        }

        if (!returnTracks.isEmpty())
            return returnTracks;

        throw new NoTracksLeftException("No tracks found for : " + cellline + " in " + Arrays.toString(packages.toArray()));
    }


    /**
     * Returns all known track packages.
     *
     * @return list of all packages names
     */
    public List<String> getTrackPackageNames(Genome.Assembly assembly) {
        return this.trackPackages.stream().filter(i -> i.getAssembly() == assembly).map(TrackPackage::getName).collect(Collectors.toList());
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
     * Returns track by given database id
     *
     * @param dbid - database id of track to return
     * @return track with id
     */
    public Track getTrackByDbId(int dbid) {

        for (Track track : tracks) {
            if (track.getDbid() == dbid) {
                return track;
            }
        }

        throw new RuntimeException("Track not found by id: " + dbid);
    }


    /**
     * Returns track by given local id
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

        String message = "Could not find track " + name + ". Some parts might not be working correct. " +
                "Please check the track file and name";

        logger.error(message);
        throw new RuntimeException(message);
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

    public Set<String> getPackNames(Genome.Assembly assembly) {
        return packNames;
    }

    public List<Track> getTracksByPackage(List<String> packages, Genome.Assembly assembly) {
        List<Track> returnTracks = new ArrayList<>();

        for (Track track : tracks) {
            if (track.getAssembly().equals(assembly) && packages.stream().anyMatch(p -> p.equals(track.getPack())))
                returnTracks.add(track);
        }

        return returnTracks;

        //throw new RuntimeException("Could not find tracks for packages" + Arrays.toString(packages.toArray()) + ". Some parts might not be working correct. " +
        //        "Please check the names");
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
    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, String source, String sourceURL, Genome.Assembly assembly, String cellLine) {
        return new ScoredTrack(starts, ends, names, scores, new TrackEntry(name, description, assembly.toString(), cellLine, "", source, sourceURL));
    }

    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description) {
        return new ScoredTrack(starts, ends, names, scores, new TrackEntry(name, description, "Unknown", "", ""));
    }


    public ScoredTrack createScoredTrack(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new ScoredTrack(starts, ends, names, scores, new TrackEntry(name, description, assembly.toString(), cellLine, ""));
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
        return new InOutTrack(starts, ends, new TrackEntry(name, description, assembly.toString(), "None", ""));
    }

    public InOutTrack createInOutTrack(List<Long> starts, List<Long> ends, String name, String description, Genome.Assembly assembly, String cellLine) {
        return new InOutTrack(starts, ends, new TrackEntry(name, description, assembly.toString(), cellLine, ""));
    }

    public Track createInOutTrack(long[] starts, long[] ends, String ex, String description, Genome.Assembly assembly) {
        return new InOutTrack(starts, ends, ex, description, assembly, "");
    }

    public InOutTrack createEmptyTrack(Genome.Assembly assembly) {
        return new InOutTrack(new long[]{0}, new long[]{ChromosomSizes.getInstance().getGenomeSize(assembly)}, "", "", assembly, "");
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



    enum Type {inout, Named, distance, strand, scored}

}
