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
package de.thm.spring.controller;

import de.thm.calc.TestTrackResult;
import de.thm.command.ExpressionCommand;
import de.thm.command.InterfaceCommand;
import de.thm.genomeData.tracks.Track;
import de.thm.logo.Logo;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.positionData.AssemblyGuesser;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.spring.cache.RCodeExport;
import de.thm.stat.TestResult;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.util.Precision;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerHelper {

    public static final Path basePath = new File("/tmp").toPath();

    /**
     * Set params for model with known interfaceCommand
     *
     * @param model - model to set params to
     * @param collector - result collector to get results from
     * @param cmd - interfaceCommand for user set params
     */
    static void setModel(Model model, ResultCollector collector, InterfaceCommand cmd, List<TestResult> covariants) {

        List<TestResult> inout = collector.getInOutResults(cmd.isShowall());
        inout.removeAll(covariants);

        // r export page

        String rcode = RCodeExport.barplot(collector, cmd.getOriginalFilename());

        model.addAttribute("rcode", rcode);

        // r export page

        // packs
        Map<String, List<TestResult>> results = new HashMap<>();
        Map<String, List<Double>> percentiles = new HashMap<>();

        inout.forEach(r -> results.put(r.getTrack().getPack(), new ArrayList<>()));
        inout.forEach(r -> results.get(r.getTrack().getPack()).add(r));

        // use copy of the results map for display, because scored and named tracks are appended here (and those are send to the interface separately)
        model.addAttribute("results", new HashMap<>(results));

        double maxOverall = collector.getResults(cmd.isShowall()).stream()
                .map(TestResult::getEffectSize)
                .max(Double::compareTo)
                .orElse(1d);

        results.put("Scored", collector.getScoredResults(cmd.isShowall()));
        results.put("Named", collector.getNamedResults(cmd.isShowall()));

        results.keySet().forEach(key -> { //iterate over the packages

            if (results.get(key).size() == 0) // set 0's if there aren't any values for this package
                percentiles.put(key, Arrays.asList(0d, 0d, 0d, 0d, 0d));

            else {
                Percentile p = new Percentile();

                p.setData(results.get(key).stream()
                        .map(TestResult::getEffectSize)
                        .mapToDouble(Double::doubleValue)
                        .toArray());

                List<Double> vals = new ArrayList<>();


                vals.add(p.evaluate(1));
                vals.add(p.evaluate(25));
                vals.add(Precision.round(p.evaluate(50), 2));
                vals.add(p.evaluate(75));
                vals.add(p.evaluate(100));

                percentiles.put(key, vals);
            }
        });

        model.addAttribute("perc", percentiles);
        model.addAttribute("maxES", maxOverall);

        // packs

        // pca

        SortedMap<String, double[]> pca = collector.getPca();
        double[][] pca_values = new double[pca.size()][2];
        List<String> pca_names = new ArrayList<>(pca.keySet());

        for (int i = 0; i < pca_names.size(); i++) pca_values[i] = pca.get(pca_names.get(i));

        model.addAttribute("pca_names", pca_names);
        model.addAttribute("pca", pca_values);

        // pca

        // hotspots page
        setHotspotBoundaries(model, collector.getAssembly());

        cmd.setHotspots(collector.getHotspots());

        // hotspots page


        // barplot page
        org.apache.commons.math3.util.Pair<List<String>, List<Double>> data = collector.getBarplotdata();
        model.addAttribute("bardata", data);

        // barplot page


        // circ plot

        List<List<Double>> circResults = collector.getPositionalResults().stream()
                .map(TestTrackResult::getResultScores)
                .collect(Collectors.toList());

        List<String> circResultNames = collector.getPositionalResults().stream()
                .map(r -> r.getUsedTrack().getName())
                .collect(Collectors.toList());

        Collections.reverse(circResults);
        Collections.reverse(circResultNames);


        model.addAttribute("circ_results", circResults);
        model.addAttribute("circ_result_names", circResultNames);

        // circ plot

        // Other values ...

        List<TestResult> score = collector.getScoredResults(cmd.isShowall());
        score.removeAll(covariants);
        model.addAttribute("results_score", score);

        List<TestResult> name = collector.getNamedResults(cmd.isShowall());
        name.removeAll(covariants);
        model.addAttribute("results_named", name);

        model.addAttribute("insig_results", collector.getInsignificantResults());

        model.addAttribute("interfaceCommand", cmd);

        // track counts overall

        model.addAttribute("total_track_count", collector.getTrackCount());
        if (cmd.isShowall())
            model.addAttribute("visible_track_count", collector.getTrackCount() - covariants.size());
        else
            model.addAttribute("visible_track_count", collector.getSignificantTrackCount() - covariants.size());

        // track counts overall

        //cmd.setMinBg(collector.getBgCount());
        cmd.setMinBg(10000);
        cmd.setAssembly(cmd.getAssembly() == null ? "hg19" : cmd.getAssembly()); //set assembly nr if there was none set in the previous run

        Set<String> celllines = collector.getResults().stream()
                .map(TestResult::getTrack)
                .map(Track::getCellLine)
                .collect(Collectors.toSet());

        celllines.add("Unknown");

        model.addAttribute("knownCellLines", celllines);

        model.addAttribute("bgCount", collector.getBackgroundSites().getPositionCount());
        model.addAttribute("sigTrackCount", inout.size() + score.size() + name.size());
        model.addAttribute("trackCount", collector.getTrackCount());

        model.addAttribute("ran", true);

        Logo logo1 = collector.getLogo();
        Logo logo2 = collector.getSecondLogo();

        if(logo1 != null && logo2 != null) {
            model.addAttribute("sequencelogo", logo1.getHeights().toString());
            model.addAttribute("sequencelogo2", logo2.getHeights().toString());

            model.addAttribute("sequencelogo_name", logo1.getName());
            model.addAttribute("sequencelogo2_name", logo2.getName());
        }

        model.addAttribute("sl_effect", collector.logoEffectSize());

        model.addAttribute("bgfilename", "Background");


    }

    /**
     * Sets the boundaries (sizes) for the chromosomes for the given assembly on the given model
     *
     * @param model    - model to set values
     * @param assembly - assembly number to get sizes from
     */
    public static void setHotspotBoundaries(Model model, Genome.Assembly assembly) {

        long genomeSize = ChromosomSizes.getInstance().getGenomeSize(assembly);
        String[] chrnames = new String[]{"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21", "chr22", "chrX", "chrY"};

        List<Double> chrsizes = Arrays.stream(chrnames)
                .map(chr -> ChromosomSizes.getInstance().getChrSize(assembly, chr))
                .map(l -> (double) l)
                .map(len -> (len / genomeSize) * 100)
                .collect(Collectors.toList());

        model.addAttribute("chrnames", chrnames);
        model.addAttribute("chrsizes", chrsizes);
    }


    /**
     * Set params for model
     *
     * @param model - model to set params to
     * @param collector - collector to get results from
     * @param data - user data to get size from
     * @param filename - name of uploaded file
     */
    static void setModel(Model model, ResultCollector collector, UserData data, String filename) {

        InterfaceCommand command = new InterfaceCommand();
        command.setPositionCount(data.getPositionCount());
        command.setAssembly(collector.getAssembly().toString());

        if (model.containsAttribute("showall") && model.asMap().get("showall").equals(true))
            command.setShowall(true);

        //cut off filenames longer than 18 chars:
        if(filename != null)
            filename = filename.length() > 18 ? filename.substring(0, 15) + ".." : filename;
        command.setOriginalFilename(filename);

        //command.setMinBg(collector.getBgCount());

        setModel(model, collector, command, new ArrayList<>());


        ExpressionCommand exCommand = new ExpressionCommand();
        model.addAttribute("expressionCommand", exCommand);
    }


    /**
     * Reads the user data for a given uploaded multipart file.
     * Returns the generated user sites
     *
     * @param file             - file from form submit
     * @return User sites with the given data
     * @throws IllegalArgumentException if the file was empty
     */
    public static UserData getUserData(MultipartFile file, Optional<Genome.Assembly> assembly) throws IllegalArgumentException {


        String name = file.getOriginalFilename();
        String filenameWithUUID = name + "-" + UUID.randomUUID();

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(ControllerHelper.basePath.resolve(filenameWithUUID).toFile()));
                stream.write(bytes);
                stream.close();

                Path inputFilepath = basePath.resolve(filenameWithUUID);

                UserData data = new UserData(assembly.orElseGet(() -> AssemblyGuesser.guessAssembly(inputFilepath)), inputFilepath, "Unknown");
                try {
                    Files.deleteIfExists(inputFilepath);
                } catch (IOException e) {
                    // do nothing here. File seems to be unreacheable
                }

                return data;

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error reading file " + file + " " + e);
            }

        } else throw new IllegalArgumentException("Upload failed. No file selected or the file was empty.");

    }
}
