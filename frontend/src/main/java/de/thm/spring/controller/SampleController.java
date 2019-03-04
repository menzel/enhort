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

import de.thm.command.BackendCommand;
import de.thm.command.Command;
import de.thm.exception.CovariatesException;
import de.thm.exception.NoTracksLeftException;
import de.thm.misc.Genome;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.util.ArrayList;

import static de.thm.spring.controller.ControllerHelper.setModel;

@Controller
public class SampleController {


    @RequestMapping(value = "/sample", method = RequestMethod.GET)
    public String sampledata(Model model, HttpSession httpSession){

        String samplefilepath;

        if(System.getenv("HOME").contains("menzel")) {
            String path = "/home/menzel/Desktop/THM/lfba/enhort";
            samplefilepath = path + "/sites.hiv.all.wu_03.tab";
        } else {
            String path = "/home/mmnz21/enhort";
            samplefilepath = path + "/sites.hiv.all.wu_03.tab";
        }

        if (!new File(samplefilepath).exists()) {
            model.addAttribute("errorMessage", "The sample file is not available on your system. Please upload any .bed file using the quick start button on the main page.");
            return "error";
        }

        Sessions sessionControll = Sessions.getInstance();
        Session currentSession = sessionControll.getSession(httpSession.getId());
        StatisticsCollector stats = StatisticsCollector.getInstance();

        Path file = new File(samplefilepath).toPath();

        UserData userSites = new UserData(Genome.Assembly.hg19, file, "Unknown");
        currentSession.setSites(userSites);
        currentSession.setOriginalFilename("HIV");

        BackendCommand backendCommand = //new BackendCommand(userSites, Command.Task.ANALZYE_SINGLE);
                new BackendCommand.Builder(Command.Task.ANALZYE_SINGLE, userSites.getAssembly()).sites(userSites).minBg(userSites.getPositionCount()).build();

        try {
            /////////// Run analysis ////////////
            ResultCollector collector = (ResultCollector) currentSession.getConnector().runAnalysis(backendCommand);
            /////////////////////////////////////

            if(collector != null) {

                currentSession.setCollector(collector);

                setModel(model, collector, userSites, "HIV");
                model.addAttribute("covariants", new ArrayList<>());
                model.addAttribute("covariantCount", 0);
                model.addAttribute("customTracks", currentSession.getCustomTracks());
                model.addAttribute("bgfilename", currentSession.getBgname());
                model.addAttribute("customTracks", currentSession.getCustomTracks());

                stats.addAnaylseC();
                stats.addFileC();

                return "result";

            } else {
                model.addAttribute("errorMessage", "No results from backend server. Maybe the server is down right now. Try again in a few minutes or contact an admin.");
                return "error";
            }

        } catch (CovariatesException | SocketTimeoutException | NoTracksLeftException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
