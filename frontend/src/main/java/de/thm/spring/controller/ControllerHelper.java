package de.thm.spring.controller;

import de.thm.command.ExpressionCommand;
import de.thm.command.InterfaceCommand;
import de.thm.logo.Logo;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

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
        model.addAttribute("results_inout", inout);

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
        model.addAttribute("bgCount", 42);
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
