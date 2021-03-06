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
import de.thm.command.ExpressionCommand;
import de.thm.command.InterfaceCommand;
import de.thm.exception.CovariatesException;
import de.thm.misc.Genome;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.stat.TestResult;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        //TODO remove redudnant infos from InterfaceCommand

        ResultCollector collector;
        List<TestResult> covariants = new ArrayList<>();
        StatisticsCollector stats = StatisticsCollector.getInstance();
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

        currentSession.setSites(data);
        command.setSites(data);

        try {

            BackendCommand backendCommand = //new BackendCommand(command, Command.Task.ANALZYE_SINGLE);
                    new BackendCommand.Builder(Command.Task.ANALZYE_SINGLE, Genome.Assembly.valueOf(command.getAssembly()))
                            .minBg(command.getMinBg())
                            .tracks(command.getTracks())
                            .covariants(command.getCovariants())
                            .sites(command.getSites())
                            .logoCovariate(command.getLogoCovariate())
                            .sitesBg(command.getSitesBg())
                            .createLogo(command.getLogo() || command.getLogoCovariate())
                            .customTracks(currentSession.getCustomTracks())
                            .build();


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


        } catch (CovariatesException e) {
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

        // barplot compare page

        List<Triple<List<String>, List<Double>, List<Double>>> oldbarplotdat = currentSession.getOldcollectors().stream().map(ResultCollector::getBarplotdata).collect(Collectors.toList());
        model.addAttribute("bardata", oldbarplotdat);

        // barplot compare page


        ExpressionCommand exCommand = new ExpressionCommand();
        model.addAttribute("expressionCommand", exCommand);

        command.setPositionCount(data.getPositionCount());

        stats.addAnaylseC();
        return "result";
    }


}
