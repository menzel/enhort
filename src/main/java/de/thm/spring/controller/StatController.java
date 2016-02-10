package de.thm.spring.controller;

import de.thm.spring.backend.StatisticsCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Michael Menzel on 10/2/16.
 */
@Controller
public class StatController {

    @RequestMapping(value = "/statistics")
    public String basicStats(Model model){

        StatisticsCollector stats = StatisticsCollector.getInstance();

        model.addAttribute("foo", stats.toString() );

        return "stat";
    }
}
