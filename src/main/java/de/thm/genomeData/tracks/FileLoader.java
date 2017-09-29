package de.thm.genomeData.tracks;

import de.thm.genomeData.sql.DBConnector;
import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.PositionPreprocessor;
import de.thm.run.BackendController;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Precision;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * File loader class that loads annotation files and creates track objects
 *
 * Created by menzel on 10/13/16.
 */
final class FileLoader implements Runnable {
    private final List<Track> tracks; //reference to the syncronized list created in the FileLoader

    private final Path path;
    private final GenomeFactory.Assembly assembly;
    private final String cellline;
    private final String name;
    private final String desc;
    private final TrackFactory.Type type;
    private final int linecount;

    public FileLoader(DBConnector.TrackEntry entry, List<Track> tracks) {


        Path basePath;
        if (System.getenv("HOME").contains("menzel")) {
            basePath = new File("/home/menzel/Desktop/THM/lfba/enhort/dat/").toPath();
        } else {
            basePath = new File("/home/mmnz21/dat/").toPath();
        }

        this.tracks = tracks;
        this.path = basePath.resolve(new File(entry.getFilepath()).toPath());
        this.assembly = GenomeFactory.Assembly.valueOf(entry.getAssembly());
        this.cellline = (entry.getCellline() == null || entry.getCellline().equals("")) ? "Unknown" : entry.getCellline();
        this.name =entry.getName();
        this.type = TrackFactory.Type.valueOf(entry.getType());
        this.desc = entry.getDescription();
        this.linecount = entry.getFilesize();

    }

    @Override
    public void run() {

        Optional<Track> track = initTrackfromFile(path.toFile());
        track.ifPresent(tracks::add);
    }


    /**
     * Overwrites the bed file with new (preprocessed) positions
     *
     * @param track - track to write
     * @param path  - path to file to overwrite
     * @param type  - type of track, does not work for scored or named tracks.
     */
    public void saveTrack(Track track, Path path, TrackFactory.Type type) {
        String header = "";
        ChromosomSizes chr = ChromosomSizes.getInstance();

        if (type == TrackFactory.Type.inout) {
            long[] starts = track.getStarts();
            long[] ends = track.getEnds();

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                header = reader.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }


            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                if (header.contains("track"))
                    writer.write(header + "\n");

                for (int i = 0; i < starts.length; i++) {
                    Pair<String, Long> start = chr.mapToChr(assembly, starts[i]);
                    Pair<String, Long> end = chr.mapToChr(assembly, ends[i]);

                    writer.write(start.getKey() + "\t" + start.getValue() + "\t" + end.getValue() + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Loads track data from a bed file. Calls handleParts to handle each line
     *
     * @param file - file to parse
     */
    private Optional<Track> initTrackfromFile(File file) {

        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        long time = System.currentTimeMillis();

        List<Long> starts = new ArrayList<>(linecount);
        List<Long> ends = new ArrayList<>(linecount);
        List<String> names = new ArrayList<>(linecount);
        List<Double> scores = new ArrayList<>(linecount);
        List<Character> strands = new ArrayList<>(linecount);

        try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            Iterator<String> it = lines.iterator();

            Pattern entry = Pattern.compile("chr(\\d{1,2}|X|Y)\\s(\\d*)\\s(\\d*).*");

            String lastChr = "";
            long offset = 0; //remember offset

            while (it.hasNext()) {

                if (Thread.currentThread().isInterrupted()) {

                    long diff = System.currentTimeMillis() - time;
                    System.err.println("loaded " + Precision.round(((double) linecount / diff), 2) + "\t" + file.getName() + " of lines " + linecount + " in " + diff);

                    System.err.println("Interrupted loading of " + file.getName());
                    return Optional.empty();
                }

                String line = it.next();
                Matcher line_matcher = entry.matcher(line);

                if (line_matcher.matches()) {
                    String[] parts = line.split("\t");

                    long start;
                    long end;

                    try { //handle null pointer exc if chromosome name is not in list

                        if (!lastChr.equals(parts[0])) {  //only calc new offset if the chr changes
                            offset = chrSizes.offset(assembly, parts[0]);
                            lastChr = parts[0];
                        }

                        if(BackendController.runlevel == BackendController.Runlevel.DEBUG){
                            //check if start and end are on the chromosome
                            if(chrSizes.getChrSize(assembly,parts[0]) < Long.parseLong(parts[1]) ||
                                (chrSizes.getChrSize(assembly,parts[0]) < Long.parseLong(parts[2]))){
                                System.err.println("Start or End out of chromosome bounds (" + file.getName() + "): " + line);

                            }

                        }

                        start = Long.parseLong(parts[1]) + offset;
                        end = Long.parseLong(parts[2]) + offset;


                    } catch (NullPointerException e) {
                        System.err.println("File loader chrname " + parts[0] + " not found in file " + file.getName());
                        continue;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        System.err.println("Could not parse " + Arrays.toString(parts) + " from file " + file.getName());
                        continue;
                    }

                    starts.add(start);
                    ends.add(end);

                    if (BackendController.runlevel == BackendController.Runlevel.DEBUG) {
                        if(starts.size() > 2 && start < starts.get(starts.size()-2)){
                            System.err.println("Illegal position at " + line + " file " + file.getName());
                        }
                    }
                    if (type == TrackFactory.Type.inout)
                        continue;

                    if (type == TrackFactory.Type.named)
                        names.add(parts[3].intern());

                    if (type == TrackFactory.Type.scored) {
                        names.add(parts[3].intern());

                        if (parts.length > 4 && parts[4] != null)
                            scores.add(Double.parseDouble(parts[4]));
                        else
                            scores.add(.0);
                    }

                    if(type == TrackFactory.Type.strand) {
                        if (parts.length > 5 && parts[5] != null && parts[5].matches("[+-]"))
                            strands.add(parts[5].charAt(0));
                        else strands.add('o');
                    }
                }
            }

            lines.close();

           // Check read files //

            if (BackendController.runlevel == BackendController.Runlevel.DEBUG) {

                if (starts.size() == 0 || starts.size() != ends.size()) {
                    System.err.println("File has no positions or different start and end lengths: " + file.getAbsolutePath());
                    throw new Exception("Something is wrong with this track or file: " + file.getName());
                }

                if (starts.stream().filter(Objects::isNull).count() > 0)
                    System.err.println("List of starts is missing something for " + file.getName());

                if (ends.stream().filter(Objects::isNull).count() > 0)
                    System.err.println("List of ends is missing something for " + file.getName());

                if ((type == TrackFactory.Type.named || type == TrackFactory.Type.scored) && names.stream().filter(Objects::isNull).count() > 0)
                    System.err.println("List of names is missing something for " + file.getName());

                if (type == TrackFactory.Type.scored && scores.stream().filter(Objects::isNull).count() > 0)
                    System.err.println("List of scores is missing something for " + file.getName());

                if(type != TrackFactory.Type.scored && type != TrackFactory.Type.named) {

                    for (int i = 0; i < starts.size() - 1; i++)
                        if (starts.get(i) > starts.get(i + 1)) {
                            Pair<String, Long> pos = chrSizes.mapToChr(assembly, starts.get(i));
                            System.err.println("Looks like this track is not sorted " + file.getName() + "(" + file.getAbsolutePath() + ")" + pos.getLeft() + " " + pos.getRight());
                        }

                    for (int i = 0; i < starts.size() - 1; i++)
                        if (ends.get(i) > ends.get(i + 1)) {
                            Pair<String, Long> pos = chrSizes.mapToChr(assembly, ends.get(i));
                            System.err.println("Looks like this track is not sorted " + file.getName() + "(" + file.getAbsolutePath() + ")" + pos.getLeft() + " " + pos.getRight());
                        }

                    for (int i = 0; i < starts.size(); i++)
                        if (starts.get(i) > ends.get(i))
                            System.err.println("There is an interval with larger end than start in " + file.getName() + "(" + file.getAbsolutePath() + ")");

                }

            }

            // End check read files //


            //long diff = System.currentTimeMillis() - time;
            //System.err.println("loaded " + Precision.round(((double) length/diff),2) + "\t" + file.getName() + " of lines " + length + " in " +  diff);

            switch (type) {
                case strand:
                    return Optional.of(new StrandTrack(starts, ends, strands, name, desc, assembly, cellline));
                case inout:
                    //return PositionPreprocessor.preprocessData(new InOutTrack(starts, ends, name, description));
                    return Optional.of(new InOutTrack(starts, ends, name, desc, assembly, cellline));
                case scored:
                    return Optional.of(PositionPreprocessor.preprocessData(new ScoredTrack(starts, ends, names, scores, name, desc, assembly, cellline)));
                case named:
                    return Optional.of(PositionPreprocessor.preprocessData(new NamedTrack(starts, ends, names, name, desc, assembly, cellline)));
                case distance:
                    return Optional.of(new DistanceTrack(starts, "Distance from " + name, desc, assembly, cellline));
                default:
                    throw new Exception("Something is wrong with this track or file: " + file.getName());
            }

        } catch (Exception e) {

            System.err.println("For file " + file.getName());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
