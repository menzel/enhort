package de.thm.spring.controller;

import de.thm.command.BackendCommand;
import de.thm.command.Command;
import de.thm.command.InterfaceCommand;
import de.thm.exception.CovariatesException;
import de.thm.exception.NoTracksLeftException;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackPackage;
import de.thm.misc.Genome;
import de.thm.positionData.UserData;
import de.thm.result.DataViewResult;
import de.thm.result.ResultCollector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.stat.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static de.thm.spring.controller.ControllerHelper.setModel;


@Controller
public class WizardController {

    private final Logger logger = LoggerFactory.getLogger(WizardController.class);


    @RequestMapping(value = {"/wiz", "/wizcov", "/wizresult"}, method = RequestMethod.GET)
    public String wizard(Model model, HttpSession httpSession){

        model.addAttribute("page", "upload");

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());
        currentSession.setSites(null); // reset old session


        InterfaceCommand interfaceCommand = new InterfaceCommand();
        model.addAttribute("interfaceCommand", interfaceCommand);

        return "wizard";
    }


    @RequestMapping(value = "/wizfile", method = RequestMethod.GET)
    public String wizard_get(Model model, HttpSession httpSession, @ModelAttribute InterfaceCommand interfaceCommand) {

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        if(currentSession.getCollector() != null)
            interfaceCommand.setTracks(currentSession.getCollector().getTracks());

        model.addAttribute("interfaceCommand", interfaceCommand);
        model = loadDataTableModel(model, currentSession.getSites().getAssembly(), httpSession);
        model.addAttribute("page", "tracks");

        return "wizard";
    }


    @RequestMapping(value = "/wizfile", method = RequestMethod.POST)
    public String wizard1(Model model, HttpSession httpSession, @RequestParam("file") MultipartFile file, @ModelAttribute InterfaceCommand interfaceCommand) {
        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        if(currentSession.getSites() == null) {

            UserData data = ControllerHelper.getUserData(file);

            currentSession.setSites(data);
            currentSession.setOriginalFilename(file.getOriginalFilename());
            interfaceCommand.setAssembly(data.getAssembly().toString());

            if(currentSession.getCollector() != null)
                interfaceCommand.setTracks(currentSession.getCollector().getTracks());

            if(data.getPositionCount() < 1){
                model.addAttribute("message", "There are no genomic positions in the .bed file you uploaded. Does it have the correct format, example: chr1\\t10\\t100 (where \\t is a tab)");
                return "error";
            }

            /* Add presets */

            model = loadDataTableModel(model, data.getAssembly(), httpSession);

            model.addAttribute("interfaceCommand", interfaceCommand);
            model.addAttribute("page", "tracks");

            return "wizard";
        }
        return "error";
    }

    @RequestMapping(value = "/wizresult", method = RequestMethod.POST)
    public String wizard2(Model model, HttpSession httpSession, @ModelAttribute InterfaceCommand interfaceCommand){

        StatisticsCollector stats = StatisticsCollector.getInstance();

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        if(interfaceCommand.getTracks().size() > 0) {

            interfaceCommand.setPositionCount(currentSession.getSites().getPositionCount());

            BackendCommand command =
                    new BackendCommand.Builder(Command.Task.ANALZYE_SINGLE, currentSession.getSites().getAssembly())
                            .sites(currentSession.getSites())
                            .tracks(interfaceCommand.getTracks())
                            .covariants(currentSession.getCovariants().stream()
                                    .map(TestResult::getTrack)
                                    .map(Track::getUid)
                                    .map(String::valueOf)
                                    .collect(Collectors.toList()))
                            .logoCovariate(interfaceCommand.getLogoCovariate())
                            .sitesBg(interfaceCommand.getSitesBg())
                            .createLogo(interfaceCommand.getLogo() || interfaceCommand.getLogoCovariate())
                            .customTracks(currentSession.getCustomTracks())
                            .build();

            ResultCollector collector = null;

            try {
                collector = (ResultCollector) currentSession.getConnector().runAnalysis(command);

            } catch(NoTracksLeftException e){

                model.addAttribute("message", "There are no tracks for this combination of cell lines and packages");
                return "error";

            } catch (CovariatesException | SocketTimeoutException e) {
                logger.error("Exception {}", e.getMessage(), e);
            }

            if (collector == null) {
                model.addAttribute("errorMessage", "There were no results for this given input. You can try to submit your request again with a different set of tracks");
                return "error";
            }


            setModel(model, collector, currentSession.getSites(), currentSession.getOriginalFilename());

            List<TestResult> covariants = currentSession.getCovariants();
            model.addAttribute("covariants", covariants);
            model.addAttribute("covariantCount", covariants.size());
            model.addAttribute("customTracks", currentSession.getCustomTracks());

            stats.addAnaylseC();
            stats.addFileC();

            currentSession.setCollector(collector);
            interfaceCommand.setMinBg(10000);
            interfaceCommand.setOriginalFilename(currentSession.getOriginalFilename());
            interfaceCommand.setHotspots(collector.getHotspots());

            model.addAttribute("interfaceCommand", interfaceCommand);
            model.addAttribute("tracks", currentSession.getCollector().getInOutResults(false));
            model.addAttribute("page", "covariates");

            model.addAttribute("sigTrackCount", collector.getSignificantTrackCount());
            model.addAttribute("trackCount", collector.getTrackCount());

            //TODO

            return "result";
        }
        return "error";
    }


    /**
     * Loads all data for the matrix data table view
     * @param model - model container to be loaded with the data
     *
     * @return the filled model
     */
    private Model loadDataTableModel(Model model, Genome.Assembly assembly, HttpSession httpSession) {

        BackendCommand command = // new BackendCommand(assembly, Command.Task.GET_TRACKS);
                new BackendCommand.Builder(Command.Task.GET_TRACKS, assembly).build();

        try {

            Sessions sessionsControll = Sessions.getInstance();
            Session currentSession = sessionsControll.getSession(httpSession.getId());

            /////////// Run analysis ////////////
            DataViewResult collector = (DataViewResult) currentSession.getConnector().runAnalysis(command);
            /////////////////////////////////////

            if(collector != null) {

                List<String> trackNames = collector.getPackages().stream()
                        .flatMap(trackPackage -> trackPackage.getTrackList().stream())
                        .map(Track::getName)
                        .sorted()
                        .collect(Collectors.toList());

                List<List<Integer>> ids = new ArrayList<>();

                List<TrackPackage> packages = collector.getPackages();
                packages.sort(Comparator.comparing(TrackPackage::getCellLine));

                for (TrackPackage pack : packages) {

                    List<Integer> linkIds = new ArrayList<>();
                    List<String> packNames = pack.getTrackList().stream().map(Track::getName).map(String::toLowerCase).collect(Collectors.toList());

                    for(String name: trackNames){
                        name = name.toLowerCase();

                        if(packNames.contains(name))
                            linkIds.add(pack.getTrackList().get(packNames.indexOf(name)).getUid());
                        else
                            linkIds.add(-1);
                    }

                    ids.add(linkIds);
                }

                model.addAttribute("ids", ids);

                model.addAttribute("trackNames", trackNames);
                model.addAttribute("packages", collector.getPackages());
                model.addAttribute("assembly", collector.getAssembly());

                model.addAttribute("celllines", new ArrayList<>(collector.getCellLines().keySet()).parallelStream()
                        .filter(cl -> collector.getPackages()
                                .stream()
                                .map(TrackPackage::getCellLine)
                                .anyMatch(cl::equals))
                        .sorted()
                        .collect(Collectors.toList()));

            } else {
                logger.warn("ApplicationController: Collector for data is null");
            }

        } catch (CovariatesException | SocketTimeoutException | NoTracksLeftException | ClassCastException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }


        return model;
    }
}
