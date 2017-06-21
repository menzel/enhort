package de.thm.spring.controller;

import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for statistics page.
 *
 *
 * Created by Michael Menzel on 10/2/16.
 */
@Controller
public class StatController {

    @RequestMapping(value = "/statistics")
    public String basicStats(Model model) {

        StatisticsCollector stats = StatisticsCollector.getInstance();

        model.addAttribute("version", "0.1.03");

        model.addAttribute("fileCount", stats.getFileCount());
        model.addAttribute("analyseCount", stats.getAnalyseCount());
        model.addAttribute("sessionCount", stats.getSessionCount());
        model.addAttribute("errorCount", stats.getErrorCount());
        model.addAttribute("downloadCount", stats.getDownloadCount());
        model.addAttribute("creationDate", stats.getCreationDate().toString());


        model.addAttribute("current_session_count", Sessions.getInstance().count());
        model.addAttribute("sessions", Sessions.getInstance().getSessions());


        return "stat";
    }
}
