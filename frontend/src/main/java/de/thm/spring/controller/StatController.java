// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
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

        model.addAttribute("version", "0.1.09");

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
