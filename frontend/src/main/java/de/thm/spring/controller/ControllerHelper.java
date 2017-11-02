package de.thm.spring.controller;

import de.thm.command.ExpressionCommand;
import de.thm.command.InterfaceCommand;
import de.thm.logo.Logo;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.ui.Model;

import java.util.*;

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

        // packs
        Map<String, List<TestResult>> results = new HashMap<>();
        Map<String, Triple<Double, Double, Double>> combinedEffectSizes = new HashMap<>();

        inout.forEach(r -> results.put(r.getTrack().getPack(), new ArrayList<>()));
        inout.forEach(r -> results.get(r.getTrack().getPack()).add(r));

        double maxOverall = collector.getInOutResults(cmd.isShowall()).stream().map(TestResult::getEffectSize).max(Double::compareTo).get();

        results.keySet().forEach(key -> {
            double max = results.get(key).stream().map(TestResult::getEffectSize).max(Double::compareTo).get();
            double left = results.get(key).stream().map(TestResult::getEffectSize).min(Double::compareTo).get();
            double right = (max / maxOverall) * 145; // 145 for 150px column width in the result table
            left = (left / maxOverall) * 145; // 145 for 150px column width in the result table

            combinedEffectSizes.put(key, new ImmutableTriple<>(left, max, right));
        });

        model.addAttribute("results", results);
        model.addAttribute("efs", combinedEffectSizes);

        // packs

        // pca

        SortedMap<String, double[]> pca = collector.getPca();
        double[][] pca_values = new double[pca.size()][2];
        List<String> pca_names = new ArrayList<>(pca.keySet());

        for (int i = 0; i < pca_names.size(); i++) pca_values[i] = pca.get(pca_names.get(i));

        model.addAttribute("pca_names", pca_names);
        model.addAttribute("pca", pca_values);

        // pca

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
