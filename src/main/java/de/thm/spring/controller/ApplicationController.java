package de.thm.spring.controller;

import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Application Controller for default routing
 *
 * Created by Michael Menzel on 22/2/16.
 */
@Controller
public class ApplicationController {

    @RequestMapping(value = "/clear_session", method = RequestMethod.GET)
    public String deleteSession(HttpSession session) {

        Sessions sessionsControl = Sessions.getInstance();
        sessionsControl.clear(session.getId());

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

}
