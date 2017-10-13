package de.thm.spring.controller;


import de.thm.exception.CovariantsException;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.ExpressionCommand;
import de.thm.spring.command.InterfaceCommand;
import de.thm.stat.TestResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static de.thm.spring.controller.ControllerHelper.setModel;


@Controller
public class CovariateController {


    @RequestMapping(value = "/covariate", method = RequestMethod.GET)
    public String covariant_get(@ModelAttribute InterfaceCommand command, Model model) {
        //TODO: try to reset and show results

        model.addAttribute("errorMessage", "The calculation could not be saved, please run it again");
        return "error";
    }

    @RequestMapping(value = "/covariate", method = RequestMethod.POST)
    public String covariant(@ModelAttribute InterfaceCommand command, Model model, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();

        Session currentSession = sessionsControll.getSession(httpSession.getId());
        UserData data = currentSession.getSites();

        if(data == null) {
            model.addAttribute("errorMessage", "The calculation could not be saved, please run it again");
            return "error";
        }

        ResultCollector collector;
        List<TestResult> covariants = new ArrayList<>();
        StatisticsCollector stats = StatisticsCollector.getInstance();
        command.setSites(data);
        command.setAssembly(data.getAssembly().toString());

        //command.setCreateLogo(false);
        // remove uuid from filename for display and set it to the old InterfaceCommand, because it will be sent to the View again:
        String filename = data.getFilename(); //file.toFile().getName().substring(0, file.toFile().getName().length()-37);
        filename = filename.length() > 18 ? filename.substring(0, 15) + ".." : filename;
        command.setOriginalFilename(filename);

        command.setSitesBg(currentSession.getSitesBg()); // get sites from session, add to command
        if(currentSession.getSitesBg() != null) {
            command.setCovariants(new ArrayList<>()); // no covariates for uploaded bg
            model.addAttribute("bgfilename", currentSession.getBgname());
        }

        command.setTracks(currentSession.getCollector().getTracks()); // get tracks from last collector
        command.setAssembly(data.getAssembly().toString());

        try {

            BackendCommand backendCommand = new BackendCommand(command);
            backendCommand.addCustomTrack(currentSession.getCustomTracks());

            /////////// Run analysis ////////////
            collector = (ResultCollector) currentSession.getConnector().runAnalysis(backendCommand);
            /////////////////////////////////////
            if(collector != null) {

                covariants = collector.getCovariants(command.getCovariants());
                currentSession.setCovariants(covariants);

                model.addAttribute("covariants", covariants);
                model.addAttribute("covariantCount", covariants.size() + (command.getLogoCovariate() ? 1 : 0));
                model.addAttribute("customTracks", currentSession.getCustomTracks());
            } else {

                model.addAttribute("errorMessage", "No Results from backend server: The computation took too long. Try to select a smaller set of covariates");
                return "error";
            }


        } catch (CovariantsException e) {
            model.addAttribute("errorMessage", "Too many covariants, a max of " + "10 covariants is allowed.");
            collector = currentSession.getCollector();

            //TODO reset last known state: set command object and put to runAnalysis
            //covariants = currentSession.getCovariants();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage() + " " + e.getCause());
            return "error";
        }

        currentSession.setCollector(collector);
        setModel(model, collector, command, covariants);

        ExpressionCommand exCommand = new ExpressionCommand();
        model.addAttribute("expressionCommand", exCommand);

        command.setPositionCount(data.getPositionCount());

        stats.addAnaylseC();
        return "result";
    }


}
