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
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackPackage;
import de.thm.misc.Genome;
import de.thm.result.DataViewResult;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DataController {


    private final Logger logger = LoggerFactory.getLogger(DataController.class);


    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public String datadefault() {

        return "redirect:/data/hg19";
    }


    @RequestMapping(value = "/data/{hg}", method = RequestMethod.GET)
    public String data(@PathVariable("hg") String assem, Model model, HttpSession httpSession) {


        Session currentSession;
        Genome.Assembly assembly;
        try {

            Sessions sessionsControll = Sessions.getInstance();
            currentSession = sessionsControll.getSessionOrError(httpSession.getId());

            assembly = Genome.Assembly.valueOf(assem);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "There are no tracks with the genome version " + assem + ". Try www.enhort.mni.thm.de/data/hg19");
            return "error";
        } catch (Exception e) {
            logger.error("Exception {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "The calculation could not be saved, please run it again");
            return "error";
        }

        BackendCommand command = //new BackendCommand(assembly, Command.Task.GET_TRACKS);
                new BackendCommand.Builder(Command.Task.GET_TRACKS, assembly).build();

        try {
            /////////// Run analysis ////////////
            DataViewResult collector = (DataViewResult) currentSession.getConnector().runAnalysis(command);
            /////////////////////////////////////

            if (collector != null) {

                List<Track> tracks = collector.getPackages().stream()
                        .map(TrackPackage::getTrackList)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                model.addAttribute("tracks", tracks);
            }

        } catch (CovariatesException | SocketTimeoutException | NoTracksLeftException | ClassCastException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

        model.addAttribute("assembly", assembly);

        return "datatable";
    }
}
