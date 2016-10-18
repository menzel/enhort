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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private final List<Track> tracks;
    private TrackFactory.Type type;
    private Track.Assembly assembly;

    FileLoader(Path path, List<Track> tracks, TrackFactory.Type type, Track.Assembly assembly) {

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
     * @param path  - path to file to overwrite
     * @param type  - type of track, does not work for scored or named tracks.
     */
    public void saveTrack(Track track, Path path, TrackFactory.Type type) {
        String header = "";
        ChromosomSizes chr = ChromosomSizes.getInstance();

        if (type == TrackFactory.Type.inout) {
            List<Long> starts = track.getStarts();
            List<Long> ends = track.getEnds();

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                header = reader.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }


            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                if (header.contains("track"))
                    writer.write(header + "\n");

                for (int i = 0; i < starts.size(); i++) {
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
     *
     * @param file - file to parse
     * @param type - type of interval
     */
    private Track initTrackfromFile(File file, TrackFactory.Type type) {

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

                    if (header_matcher.group(3) != null)
                        cellline = header_matcher.group(3);

                } else if (line_matcher.matches()) {
                    String[] parts = line.split("\t");

                    long start = -1;
                    long end = -2;

                    try { //handle null pointer exc if chromosome name is not in list

                        long offset = chrSizes.offset(parts[0]);

                        start = Long.parseLong(parts[1]) + offset;
                        end = Long.parseLong(parts[2]) + offset;
                    } catch (NullPointerException e) {
                        System.err.println("File loader chrname " + parts[0] + " not found in file " + file.getName());
                    }


                    if (!(start < end)) //check if interval length is positive
                        continue;

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