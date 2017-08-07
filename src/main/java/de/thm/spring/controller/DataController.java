package de.thm.spring.controller;

import de.thm.exception.CovariantsException;
import de.thm.exception.NoTracksLeftException;
import de.thm.genomeData.Track;
import de.thm.logo.GenomeFactory;
import de.thm.result.DataViewResult;
import de.thm.spring.backend.BackendConnector;
import de.thm.spring.cache.CellLineCache;
import de.thm.spring.command.BackendCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.SocketTimeoutException;
import java.util.stream.Collectors;

@Controller
public class DataController {

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public String dataview(Model model){

        GenomeFactory.Assembly assembly = GenomeFactory.Assembly.hg19;
        BackendCommand command = new BackendCommand(assembly);

        try {
            /////////// Run analysis ////////////
            DataViewResult collector = (DataViewResult) BackendConnector.getInstance().runAnalysis(command);
            /////////////////////////////////////

            if(collector != null) {

                model.addAttribute("tracks", collector.getTracks());
                model.addAttribute("tracknames", collector.getTracks().stream().map(Track::getName).distinct().collect(Collectors.toList()));
                model.addAttribute("assembly", collector.getAssembly());
                model.addAttribute("celllines", CellLineCache.getInstance(collector).getCellLines());

            } else {
                System.err.println("ApplicationController: Collector for data is null");
            }

        } catch (CovariantsException | SocketTimeoutException | NoTracksLeftException e) {
            e.printStackTrace();
        }

        return "data";
    }
}
