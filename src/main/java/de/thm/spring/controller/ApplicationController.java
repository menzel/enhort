package de.thm.spring.controller;

import de.thm.exception.CovariantsException;
import de.thm.exception.NoTracksLeftException;
import de.thm.logo.GenomeFactory;
import de.thm.result.DataViewResult;
import de.thm.spring.backend.BackendConnector;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.ExpressionCommand;
import de.thm.spring.command.InterfaceCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.SocketTimeoutException;

/**
 * Application Controller for default routing
 *
 * Created by Michael Menzel on 22/2/16.
 */
@Controller
public class ApplicationController {

    @RequestMapping(value = "/clear_session", method = RequestMethod.GET)
    public String deleteSession(Model model, HttpSession session) {

        Sessions sessionsControl = Sessions.getInstance();
        sessionsControl.clear(session.getId());

        InterfaceCommand command = new InterfaceCommand();
        command.setOriginalFilename("");
        command.setMinBg(10000);

        model.addAttribute("interfaceCommand", command);
        model.addAttribute("expressionCommand", new ExpressionCommand());

        return "result";
    }


    // Error page
    @RequestMapping("/error.html")
    public String error(HttpServletRequest request, Model model) {
        model.addAttribute("errorCode", request.getAttribute("javax.servlet.error.status_code"));
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String errorMessage = null;

        StatisticsCollector.getInstance().addErrorC();

        if (throwable != null) {
            errorMessage = throwable.getMessage();
        }

        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    @RequestMapping("/faq")
    public String faq(){

        return "faq";
    }
    @RequestMapping("/contact")
    public String contact(){

        return "contact";
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public String dataview(Model model){
        //TODO send dummy request to background to get list of all tracks and cell lines

        GenomeFactory.Assembly assembly = GenomeFactory.Assembly.hg19;
        BackendCommand command = new BackendCommand(assembly);

        try {
            /////////// Run analysis ////////////
            DataViewResult collector = (DataViewResult) BackendConnector.getInstance().runAnalysis(command);
            /////////////////////////////////////

            if(collector != null){

                model.addAttribute("tracks", collector.getTracks());
                model.addAttribute("assembly", collector.getAssembly());
                model.addAttribute("celllines", collector.getCellLines());
            }

        } catch (CovariantsException | SocketTimeoutException | NoTracksLeftException e) {
            e.printStackTrace();
        }

        return "data";
    }
}
