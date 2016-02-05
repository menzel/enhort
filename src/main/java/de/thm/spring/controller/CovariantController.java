package de.thm.spring.controller;

import de.thm.genomeData.Interval;
import de.thm.positionData.UserData;
import de.thm.spring.command.CovariantCommand;
import de.thm.spring.helper.AnalysisHelper;
import de.thm.stat.ResultCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@Controller
public class CovariantController {

    @RequestMapping(value="/covariant", method= RequestMethod.POST)
    public String covariant(@ModelAttribute CovariantCommand command, Model model){

        UserData data = new UserData(new File(command.getFilepath()).toPath());
        ResultCollector collector =AnalysisHelper.runAnalysis(data,command.getCovariants());

        model.addAttribute("results_inout", collector.getResultsByType(Interval.Type.inout));
        model.addAttribute("results_score", collector.getResultsByType(Interval.Type.score));
        model.addAttribute("results_named", collector.getResultsByType(Interval.Type.named));

        command.setPositionCount(data.getPositionCount()); //TODO count
        model.addAttribute("covariantCommand", command);

        return "result";
    }
}
