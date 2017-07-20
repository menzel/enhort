package de.thm.genomeData;

import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.PositionPreprocessor;
import de.thm.run.BackendController;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Precision;

import java.io.*;
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
    private final Path path;
    private final List<Track> tracks; //reference to the syncronized list created in the FileLoader
    private final TrackFactory.Type type;
    private final GenomeFactory.Assembly assembly;
    private final CellLine cellLine;

    FileLoader(Path path, List<Track> tracks, TrackFactory.Type type, GenomeFactory.Assembly assembly) {

        this.path = path;
        this.tracks = tracks;
        this.type = type;
        this.assembly = assembly;
        cellLine = CellLine.getInstance();
    }

    @Override
    public void run() {

        Optional<Track> track = initTrackfromFile(path.toFile(), type);
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
     * @param type - type of interval
     */
    private Optional<Track> initTrackfromFile(File file, TrackFactory.Type type) {

        String name = "";
        String description = "";
        String cellline = "none";
        int length = -1;
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        long time = System.currentTimeMillis();

        try {
            length = countBedLines(file.toPath());

            BufferedReader brTest = new BufferedReader(new FileReader(file));

            // decrease length by 1 if there is a header:
            if (brTest.readLine().contains("fullname=")) length--;

        } catch (IOException | NullPointerException e) {
            System.err.println("in file: " + file.getName());
            e.printStackTrace();
        }

        List<Long> starts = new ArrayList<>(length);
        List<Long> ends = new ArrayList<>(length);
        List<String> names = new ArrayList<>(length);
        List<Double> scores = new ArrayList<>(length);
        List<Character> strands = new ArrayList<>(length);

        try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            Iterator<String> it = lines.iterator();

            Pattern header = Pattern.compile("track fullname=\"([\\w\\s]+)\"\\s+description=\"([\\w\\s]+)\"(\\s+cellline=.(.*).)?");
            Pattern entry = Pattern.compile("chr(\\d{1,2}|X|Y)\\s(\\d*)\\s(\\d*).*");

            String lastChr = "";
            long offset = 0; //remember offset

            //if (!file.getName().contains("iPS")) { return null; }

            while (it.hasNext()) {

                if (Thread.currentThread().isInterrupted()) {

                    long diff = System.currentTimeMillis() - time;
                    System.err.println("loaded " + Precision.round(((double) length / diff), 2) + "\t" + file.getName() + " of lines " + length + " in " + diff);

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
                } else {

                    Matcher header_matcher = header.matcher(line);

                    if (header_matcher.matches()) {
                        name = header_matcher.group(1);
                        description = header_matcher.group(2);

                        if (header_matcher.group(4) != null)
                            cellline = header_matcher.group(4);
                    }
                }
            }

            lines.close();

            if (name.equals("")) {
                if (file.getName().contains("."))
                    name = file.getName().substring(0, file.getName().indexOf("."));
                else
                    name = file.getName();

                if (name.startsWith("wgEncodeBroadHistone")) {
                    name = name.substring("wgEncodeBroadHistone".length());
                }
            }

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
                            System.err.println("Looks like this track is not sorted (yet) " + file.getName());
                        }

                    for (int i = 0; i < starts.size() - 1; i++)
                        if (ends.get(i) > ends.get(i + 1)) {
                            System.err.println("Looks like this track is not sorted (yet) " + file.getName());
                        }

                    for (int i = 0; i < starts.size(); i++)
                        if (starts.get(i) > ends.get(i))
                            System.err.println("There is an interval with larger end than start in " + file.getName());

                }

            }

            // End check read files //


            //long diff = System.currentTimeMillis() - time;
            //System.err.println("loaded " + Precision.round(((double) length/diff),2) + "\t" + file.getName() + " of lines " + length + " in " +  diff);

            switch (type) {
                case strand:
                    return Optional.of(new StrandTrack(starts, ends, strands, name, description, assembly, cellLine.valueOf(cellline)));
                case inout:
                    //return PositionPreprocessor.preprocessData(new InOutTrack(starts, ends, name, description));
                    return Optional.of(new InOutTrack(starts, ends, name, description, assembly, cellLine.valueOf(cellline)));
                case scored:
                    return Optional.of(PositionPreprocessor.preprocessData(new ScoredTrack(starts, ends, names, scores, name, description, assembly, cellLine.valueOf(cellline))));
                case named:
                    return Optional.of(PositionPreprocessor.preprocessData(new NamedTrack(starts, ends, names, name, description, assembly, cellLine.valueOf(cellline))));
                case distance:
                    return Optional.of(new DistanceTrack(starts, "Distance from " + name, description, assembly, cellLine.valueOf(cellline)));
                default:
                    throw new Exception("Something is wrong with this track or file: " + file.getName());
            }

        } catch (Exception e) {

            System.err.println("For file " + file.getName());
            e.printStackTrace();
            return Optional.empty();
        }
    }


    private int countBedLines(Path path) throws IOException {

        try (InputStream is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        }
    }
}
