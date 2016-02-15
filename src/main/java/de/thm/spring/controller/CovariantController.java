package de.thm.spring.controller;

import de.thm.exception.TooManyCovariantsException;
import de.thm.genomeData.Interval;
import de.thm.positionData.UserData;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.spring.command.CovariantCommand;
import de.thm.spring.helper.AnalysisHelper;
import de.thm.stat.ResultCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.nio.file.Path;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@Controller
public class CovariantController {

    @RequestMapping(value="/covariant", method= RequestMethod.POST)
    public String covariant(@ModelAttribute CovariantCommand command, Model model, HttpSession httpSession){

        Sessions sessionsControll = Sessions.getInstance();

        StatisticsCollector.getInstance().addAnaylseC();


        Path file = sessionsControll.getFile(httpSession.getId());
        UserData data = new UserData(file);

        ResultCollector collector = null;
        try {
            collector = AnalysisHelper.runAnalysis(data,command.getCovariants());

            model.addAttribute("results_inout", collector.getResultsByType(Interval.Type.inout));
            model.addAttribute("results_score", collector.getResultsByType(Interval.Type.score));
            model.addAttribute("results_named", collector.getResultsByType(Interval.Type.named));


            model.addAttribute("covariants", collector.getCovariants());

            command.setPositionCount(data.getPositionCount());
            model.addAttribute("covariantCommand", command);
            model.addAttribute("bgHash", collector.getBgModelHash());
            model.addAttribute("bgCount", collector.getResults().get(0).getExpectedIn() + collector.getResults().get(0).getExpectedOut());

        } catch (TooManyCovariantsException e) {
            model.addAttribute("errorMessage", "Too many covariants, a max of 7 covariants is allowed.");
        }

        return "result";
    }
}
