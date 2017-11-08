package de.thm.spring.controller;

import de.thm.command.ExpressionCommand;
import de.thm.command.InterfaceCommand;
import de.thm.logo.Logo;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.spring.cache.RCodeExport;
import de.thm.stat.TestResult;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.util.Precision;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Collectors;

public class ControllerHelper {

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

        String rcode = RCodeExport.barplot(collector);

        model.addAttribute("rcode", rcode);

        // r export page

        // packs
        Map<String, List<TestResult>> results = new HashMap<>();
        Map<String, List<Double>> percentiles = new HashMap<>();

        inout.forEach(r -> results.put(r.getTrack().getPack(), new ArrayList<>()));
        inout.forEach(r -> results.get(r.getTrack().getPack()).add(r));

        // use copy of the results map for display, because scored and named tracks are appended here (and those are send to the interface separately)
        model.addAttribute("results", new HashMap<>(results));

        double maxOverall = collector.getResults(cmd.isShowall()).stream().map(TestResult::getEffectSize).max(Double::compareTo).get();

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

        long genomeSize = ChromosomSizes.getInstance().getGenomeSize(collector.getAssembly());
        String[] chrnames = new String[]{"chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21", "chr22", "chrX", "chrY"};

        List<Double> chrsizes = Arrays.stream(chrnames)
                .map(chr -> ChromosomSizes.getInstance().getChrSize(collector.getAssembly(), chr))
                .map(l -> (double) l)
                .map(len -> (len / genomeSize) * 100)
                .collect(Collectors.toList());

        model.addAttribute("chrnames", chrnames);
        model.addAttribute("chrsizes", chrsizes);

        // hotspots page


        // barplot page
        org.apache.commons.math3.util.Pair<List<String>, List<Double>> data = collector.getBarplotdata();
        model.addAttribute("bardata", data);

        // barplot page

        List<TestResult> score = collector.getScoredResults(cmd.isShowall());
        score.removeAll(covariants);
        model.addAttribute("results_score", score);

        List<TestResult> name = collector.getNamedResults(cmd.isShowall());
        name.removeAll(covariants);
        model.addAttribute("results_named", name);

        model.addAttribute("insig_results", collector.getInsignificantResults());

        cmd.setHotspots(collector.getHotspots());
        cmd.setAssembly(cmd.getAssembly() == null? "hg19": cmd.getAssembly()); //set assembly nr if there was none set in the previous run
        model.addAttribute("interfaceCommand", cmd);

        //cmd.setMinBg(collector.getBgCount());
        cmd.setMinBg(10000);
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

        //cut off filenames longer than 18 chars:
        if(filename != null)
            filename = filename.length() > 18 ? filename.substring(0, 15) + ".." : filename;
        command.setOriginalFilename(filename);

        //command.setMinBg(collector.getBgCount());

        setModel(model, collector, command, new ArrayList<>());


        ExpressionCommand exCommand = new ExpressionCommand();
        model.addAttribute("expressionCommand", exCommand);
    }
}
