package de.thm.spring.controller;

import de.thm.exception.CovariantsException;
import de.thm.exception.NoTracksLeftException;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackPackage;
import de.thm.logo.GenomeFactory;
import de.thm.result.DataViewResult;
import de.thm.spring.backend.BackendConnector;
import de.thm.spring.cache.DataTableCache;
import de.thm.spring.command.BackendCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

                //TODO cache:
                List<String> trackNames = collector.getPackages().stream()
                        .flatMap(trackPackage -> trackPackage.getTrackList().stream())
                        .map(Track::getName)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                Map<String, List<Integer>> ids = new TreeMap<>();

                for(TrackPackage pack: collector.getPackages()){

                    List<Integer> linkIds = new ArrayList<>();
                    List<String> packNames = pack.getTrackList().stream().map(Track::getName).collect(Collectors.toList());

                    for(String name: trackNames){
                        if(packNames.contains(name))
                            linkIds.add(pack.getTrackList().get(packNames.indexOf(name)).getUid());
                        else
                            linkIds.add(-1);
                    }

                    ids.put(pack.getCellLine(), linkIds);
                }

                model.addAttribute("ids", ids);

                model.addAttribute("trackNames", trackNames);
                model.addAttribute("packages", collector.getPackages());
                model.addAttribute("assembly", collector.getAssembly());
                model.addAttribute("celllines", DataTableCache.getInstance(collector).getCellLines());

            } else {
                System.err.println("ApplicationController: Collector for data is null");
            }

        } catch (CovariantsException | SocketTimeoutException | NoTracksLeftException e) {
            e.printStackTrace();
        }

        return "data";
    }
}
