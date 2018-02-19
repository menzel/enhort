package de.thm.spring.controller;


import de.thm.command.BackendCommand;
import de.thm.command.Command;
import de.thm.command.ExpressionCommand;
import de.thm.command.InterfaceCommand;
import de.thm.genomeData.tracks.SerializeableInOutTrack;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.stat.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.thm.spring.controller.ControllerHelper.setModel;

/**
 * Controller for main page. Relies heavily on the model and session/command objects:
 * The model is used to get stuff to the view as well as the command object.
 * The session is used to keep information for reload and reruns.
 * <p>
 * Created by Michael Menzel on 3/2/16.
 */
@Controller
public class UploadController {

    private final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String plainView(Model model, InterfaceCommand iCommand, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        ResultCollector collector = currentSession.getCollector();

        if (collector != null && currentSession.getSites() != null) {

            List<TestResult> covariants = currentSession.getCovariants();

            setModel(model, collector, currentSession.getSites(), currentSession.getOriginalFilename());
            model.addAttribute("covariants", covariants);
            model.addAttribute("covariantCount", covariants.size() + (iCommand.getLogoCovariate()? 1:0));
            model.addAttribute("customTracks", currentSession.getCustomTracks());

            return "result";

        }


        // if there are not results or data show the welcome page
        return "index";

    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file, HttpSession httpSession) {

        String name = file.getOriginalFilename();

        Sessions sessionControll = Sessions.getInstance();
        StatisticsCollector stats = StatisticsCollector.getInstance();

        try {
            UserData data = ControllerHelper.getUserData(file);

            if (data.getPositionCount() < 1) {
                model.addAttribute("errorMessage", "There were no positions in the file '" + file.getOriginalFilename() + "' you uploaded." +
                        "Make sure your file is in .bed format, where each line contains a position e.g. chr1\\t10\\t100 (where \\t is a tab)");
                return "error";
            }

            Session currentSession = sessionControll.addSession(httpSession.getId());
            currentSession.setSites(data);
            BackendCommand command = //new BackendCommand(data, Command.Task.ANALZYE_SINGLE);
                    new BackendCommand.Builder(Command.Task.ANALZYE_SINGLE, data.getAssembly()).sites(data).build();

            command.addCustomTrack(currentSession.getCustomTracks());

            /////////// Run analysis ////////////
            ResultCollector collector = (ResultCollector) currentSession.getConnector().runAnalysis(command);
            /////////////////////////////////////

            if (collector != null) {

                currentSession.setCollector(collector);
                currentSession.setOriginalFilename(name);

                setModel(model, collector, data, name);
                model.addAttribute("covariants", new ArrayList<>());
                model.addAttribute("covariantCount", 0);
                model.addAttribute("customTracks", currentSession.getCustomTracks());

                stats.addAnaylseC();
                stats.addFileC();

                return "result";
            }

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }


        model.addAttribute("errorMessage", "No results from backend server. Maybe the server is down right now. Try again in a few minutes or contact an admin.");
        return "error";
    }



    @RequestMapping(value = "/uploadbg", method = RequestMethod.POST)
    public String handleBackgroundUpload(Model model, @RequestParam("file") MultipartFile bgFile, HttpSession httpSession) {


        Sessions sessionControll = Sessions.getInstance();
        Session currentSession = sessionControll.getSession(httpSession.getId());
        StatisticsCollector stats = StatisticsCollector.getInstance();

        String name = currentSession.getOriginalFilename();

        try {
            UserData sitesBg = ControllerHelper.getUserData(bgFile); //new UserData(assembly, inputFilepath, "none");

            currentSession.setBgSites(sitesBg);

            String bgname = bgFile.getOriginalFilename();
            currentSession.setBgFilename(bgname);

            BackendCommand backendCommand = // new BackendCommand(currentSession.getSites(), sitesBg, Command.Task.ANALZYE_SINGLE);
                    new BackendCommand.Builder(Command.Task.ANALZYE_SINGLE, currentSession.getSites().getAssembly()).sites(currentSession.getSites()).sitesBg(sitesBg).build();

            /////////// Run analysis ////////////
            ResultCollector collector = (ResultCollector) currentSession.getConnector().runAnalysis(backendCommand);
            /////////////////////////////////////

            if (collector != null) {

                currentSession.setCollector(collector);

                setModel(model, collector, currentSession.getSites(), name);
                model.addAttribute("covariants", new ArrayList<>());
                model.addAttribute("covariantCount", 0);
                model.addAttribute("customTracks", currentSession.getCustomTracks());
                model.addAttribute("bgfilename", currentSession.getBgname());

                stats.addAnaylseC();
                stats.addFileC();

                return "result";
            }

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage() + e.toString());
            return "error";
        }

        model.addAttribute("errorMessage", "No results from backend server. Maybe the server is down right now. Try again in a few minutes or contact an admin.");
        return "error";
    }




    /**
     * Handle the upload of a custom track
     *
     * @param model
     * @param file - custom bed file
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "/upload_track", method = RequestMethod.POST)
    public String uploadTrack(Model model, @RequestParam("file") MultipartFile file, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        String name = file.getOriginalFilename();
        String uuid = name + "-" + UUID.randomUUID();

        Pattern interval = Pattern.compile("(chr(\\d{1,2}|X|Y))\\s(\\d*)\\s(\\d*).*");
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();
        Genome.Assembly assembly = Genome.Assembly.hg19;

         if (!file.isEmpty()) {
             try {
                 byte[] bytes = file.getBytes();
                 BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(ControllerHelper.basePath.resolve(uuid).toFile()));
                 stream.write(bytes);
                 stream.close();

                 Path path = ControllerHelper.basePath.resolve(uuid);

                 List<Long> start = new ArrayList<>();
                 List<Long> end = new ArrayList<>();


                try (Stream<String> lines = Files.lines(path)) {

                    Iterator it = lines.iterator();

                    while (it.hasNext()) {

                        String line = (String) it.next();
                        Matcher line_matcher = interval.matcher(line);
                        if (line_matcher.matches()) {
                            start.add(Long.parseLong(line_matcher.group(3)) + chrSizes.offset(assembly, line_matcher.group(1)));
                            end.add(Long.parseLong(line_matcher.group(4)) + chrSizes.offset(assembly, line_matcher.group(1)));
                        }
                    }

                    lines.close();

                    //TODO do not write file which needs to be deleted after anyway
                    Files.deleteIfExists(path);

                    SerializeableInOutTrack track = new SerializeableInOutTrack(start.stream().mapToLong(l -> l).toArray(),
                            end.stream().mapToLong(l -> l).toArray(),
                            name,
                            "Custom track",
                            assembly,
                            "Unknown");

                    currentSession.addCustomTrack(track);

                } catch (IOException e) {
                    logger.error("Exception {}", e.getMessage(), e);
                }


             } catch (Exception e){
                 logger.error("Exception {}", e.getMessage(), e);
                 return "error";
             }
         }


        return withNewTrack(currentSession, model);

    }



    @RequestMapping(value = "/trackbuilder", method = RequestMethod.POST)
    public String covariant(Model model, ExpressionCommand expressionCommand, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        SerializeableInOutTrack track;
        Optional<SerializeableInOutTrack> trackToUse = currentSession.getConnector().createCustomTrack(expressionCommand);

        if(trackToUse.isPresent()) {
            track = trackToUse.get();
            currentSession.addCustomTrack(track);
            return withNewTrack(currentSession, model);
        } else {
            return withNewTrack(currentSession, model);
        }
    }


    /**
     * Resets the collector if there is one in the sessions. Sets new Model and InterfaceCommand if there is no old session.
     * To be called after a new track is created (uploaded or created by an expression)
     *
     * @param currentSession - session of the user
     * @param model - current model
     *
     * @return result page
     */
    private String withNewTrack(Session currentSession, Model model) {

        ResultCollector collector = currentSession.getCollector();

        ExpressionCommand exCommand = new ExpressionCommand();
        model.addAttribute("expressionCommand", exCommand);

        if (collector != null) {
            UserData data = currentSession.getSites();

            List<TestResult> covariants = currentSession.getCovariants();

            setModel(model, collector, data, currentSession.getOriginalFilename());
            model.addAttribute("covariants", covariants);
            model.addAttribute("covariantCount", covariants.size());
            model.addAttribute("customTracks", currentSession.getCustomTracks());


        } else {
            InterfaceCommand command = new InterfaceCommand();
            command.setOriginalFilename("");
            command.setMinBg(10000);

            model.addAttribute("interfaceCommand", command);
            model.addAttribute("bgCount", 10000);
            model.addAttribute("sigTrackCount", null);
            model.addAttribute("trackCount", null);
            model.addAttribute("customTracks", currentSession.getCustomTracks());
        }

        return "result"; //TODO plain view
    }

}
