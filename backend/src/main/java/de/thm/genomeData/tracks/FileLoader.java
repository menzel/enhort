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

import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.misc.PositionPreprocessor;
import de.thm.run.BackendServer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * File loader class that loads annotation files and creates track objects
 *
 * Created by menzel on 10/13/16.
 */
class FileLoader implements Callable<Optional<Track>> {

    private final Path path;
    private final Genome.Assembly assembly;
    private final TrackFactory.Type type;
    private final int linecount;

    private final Logger logger = LoggerFactory.getLogger(FileLoader.class);
    private final TrackEntry trackEntry;

    public FileLoader(TrackEntry entry) {

        Path basePath = BackendServer.basePath;

        this.path = basePath.resolve(new File(entry.getFilepath()).toPath());
        this.assembly = Genome.Assembly.valueOf(entry.getAssembly());
        this.type = TrackFactory.Type.valueOf(entry.getType());
        this.linecount = entry.getFilesize();
        this.trackEntry = entry;
    }


    /**
     * Invoked when the Task is executed, the call method must be overridden and
     * implemented by subclasses. The call method actually performs the
     * background thread logic. Only the updateProgress, updateMessage, updateValue and
     * updateTitle methods of Task may be called from code within this method.
     * Any other interaction with the Task from the background thread will result
     * in runtime exceptions.
     *
     * @return The result of the background work, if any.
     * @throws Exception an unhandled exception which occurred during the
     *                   background operation
     */
    @Override
    public Optional<Track> call() throws Exception {

        return readBedFile(path.toFile());
    }



    /**
     * Loads track data from a bed file. Calls handleParts to handle each line
     *
     * @param file - file to parse
     */
    private Optional<Track> readBedFile(File file) {

        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        long time = System.currentTimeMillis();

        List<Long> starts = new ArrayList<>(linecount);
        List<Long> ends = new ArrayList<>(linecount);
        List<String> names = new ArrayList<>(linecount);
        List<Double> scores = new ArrayList<>(linecount);
        List<Character> strands = new ArrayList<>(linecount);
        int counter = 0;

        try (Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            Iterator<String> it = lines.iterator();

            Pattern entry = Pattern.compile("chr(\\d{1,2}|X|Y)\\s(\\d*)\\s(\\S*).*");
            //Pattern entry = Pattern.compile("chr(\\d{1,2}|X|Y).*");

            String lastChr = "";
            long offset = 0; //remember offset

            while (it.hasNext()) {

                if (Thread.currentThread().isInterrupted()) {

                    long diff = System.currentTimeMillis() - time;
                    logger.warn("loaded " + Precision.round(((double) linecount / diff), 2) + "\t" + file.getName() + " of lines " + linecount + " in " + diff);

                    logger.warn("Interrupted loading of " + file.getName());
                    return createTrackStub();
                }

                String line = it.next();
                Matcher line_matcher = entry.matcher(line);

                if (line_matcher.matches()) {
                    String[] parts = line.split("\t");
                    counter++;

                    long start;
                    long end;

                    try { //handle null pointer exc if chromosome name is not in list

                        if (!lastChr.equals(parts[0])) {  //only calc new offset if the chr changes
                            offset = chrSizes.offset(assembly, parts[0]);
                            lastChr = parts[0];
                        }

                        if(logger.isDebugEnabled()){
                            //check if start and end are on the chromosome
                            if(chrSizes.getChrSize(assembly,parts[0]) < Long.parseLong(parts[1]) ||
                                (chrSizes.getChrSize(assembly,parts[0]) < Long.parseLong(parts[2]))){
                                logger.warn("Start or End out of chromosome bounds (" + file.getName() + "): " + line);

                            }
                        }

                        start = Long.parseLong(parts[1]) + offset;
                        end = Long.parseLong(parts[2]) + offset;


                    } catch (NullPointerException e) {
                        logger.warn("File loader chrname " + parts[0] + " not found in file " + file.getName() + " for assemb " + assembly);
                        continue;
                    } catch (NumberFormatException e) {
                        logger.error("Exception {}", e.getMessage(), e);
                        logger.warn("Could not parse " + Arrays.toString(parts) + " from file " + file.getName());
                        continue;
                    }

                    starts.add(start);
                    ends.add(end);

                    if (logger.isDebugEnabled() && type != TrackFactory.Type.scored) {
                        if (starts.size() > 2 && start < starts.get(starts.size() - 2)) {
                            logger.warn("Illegal position at " + line + " file " + file.getName());
                        }
                    }
                    if (type == TrackFactory.Type.inout)
                        continue;

                    if (type == TrackFactory.Type.Named)
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


            if (linecount != counter)
                logger.error("File " + file.getName() + " has " + counter + " lines, but db says it has " + linecount + " lines.");


           // Check read files //

            if (logger.isDebugEnabled()) {

                if (starts.size() == 0 || (type != TrackFactory.Type.distance && starts.size() != ends.size())) {
                    logger.warn("File has no positions or different start and end lengths: " + file.getAbsolutePath());
                    throw new Exception("Something is wrong with this track or file: " + file.getName());
                }

                if (starts.stream().anyMatch(Objects::isNull))
                    logger.warn("List of starts is missing something for " + file.getName());

                if (ends.stream().anyMatch(Objects::isNull))
                    logger.warn("List of ends is missing something for " + file.getName());

                if ((type == TrackFactory.Type.Named || type == TrackFactory.Type.scored) && names.stream().anyMatch(Objects::isNull))
                    logger.warn("List of names is missing something for " + file.getName());

                if (type == TrackFactory.Type.scored && scores.stream().anyMatch(Objects::isNull))
                    logger.warn("List of scores is missing something for " + file.getName());

                if(type != TrackFactory.Type.scored && type != TrackFactory.Type.Named) {

                    for (int i = 0; i < starts.size() - 1; i++)
                        if (starts.get(i) > starts.get(i + 1)) {
                            Pair<String, Long> pos = chrSizes.mapToChr(assembly, starts.get(i));
                            logger.warn("Looks like this track is not sorted " + file.getName() + "(" + file.getAbsolutePath() + ")" + pos.getLeft() + " " + pos.getRight());
                        }

                    for (int i = 0; i < starts.size() - 1; i++)
                        if (ends.get(i) > ends.get(i + 1)) {
                            Pair<String, Long> pos = chrSizes.mapToChr(assembly, ends.get(i));
                            logger.warn("Looks like this track is not sorted " + file.getName() + "(" + file.getAbsolutePath() + ")" + pos.getLeft() + " " + pos.getRight());
                        }

                    for (int i = 0; i < starts.size(); i++)
                        if (starts.get(i) > ends.get(i))
                            logger.warn("There is an interval with larger end than start in " + file.getName() + "(" + file.getAbsolutePath() + ")");

                }

            }

            // End check read files //


            //long diff = System.currentTimeMillis() - time;
            //logger.warn("loaded " + Precision.round(((double) length/diff),2) + "\t" + file.getName() + " of lines " + length + " in " +  diff);

            switch (type) {
                case strand:
                    return Optional.of(new StrandTrack(starts, ends, strands, trackEntry));
                case inout:
                    return Optional.of(new InOutTrack(starts, ends, trackEntry));
                case scored:
                    return Optional.of(PositionPreprocessor.preprocessData(new ScoredTrack(starts, ends, names, scores, trackEntry)));
                case Named:
                    return Optional.of(PositionPreprocessor.preprocessData(new NamedTrack(starts, ends, names, trackEntry)));
                case distance:
                    return Optional.of(new DistanceTrack(starts, trackEntry));
                default:
                    throw new Exception("Something is wrong with this track or file: " + file.getName());
            }

        } catch (Exception e) {

            logger.warn("For file " + file.getName());
            logger.error("Exception {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private Optional<Track> createTrackStub() {

        List<Long> starts = Collections.emptyList();
        List<Long> ends = Collections.emptyList();
        List<String> names = Collections.emptyList();
        List<Double> scores = Collections.emptyList();
        List<Character> strands = Collections.emptyList();

        switch (type) {
            case strand:
                return Optional.of(new StrandTrack(starts, ends, strands, trackEntry));
            case inout:
                return Optional.of(new InOutTrack(starts, ends, trackEntry));
            case scored:
                return Optional.of(PositionPreprocessor.preprocessData(new ScoredTrack(starts, ends, names, scores, trackEntry)));
            case Named:
                return Optional.of(PositionPreprocessor.preprocessData(new NamedTrack(starts, ends, names, trackEntry)));
            case distance:
                return Optional.of(new DistanceTrack(starts, trackEntry));
            default:
                return Optional.empty();
        }

    }

}
