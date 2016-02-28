package de.thm.spring.controller;

import de.thm.spring.backend.StatisticsCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 10/2/16.
 */
@Controller
public class StatController {

    @RequestMapping(value = "/statistics")
    public String basicStats(Model model) {

        StatisticsCollector stats = StatisticsCollector.getInstance();

        model.addAttribute("version", "0.0.2");

        model.addAttribute("fileCount", stats.getFileCount());
        model.addAttribute("analyseCount", stats.getAnalyseCount());
        model.addAttribute("sessionCount", stats.getSessionCount());
        model.addAttribute("errorCount", stats.getErrorCount());
        model.addAttribute("downloadCount", stats.getDownloadCount());


        model.addAttribute("memory_map", getMemoryUsage());

        return "stat";
    }

    private List<String> getMemoryUsage() {
        List<String> stats = new ArrayList<>();
        return stats;
    }


}
