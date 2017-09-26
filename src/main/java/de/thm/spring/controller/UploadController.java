package de.thm.spring.controller;


import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.guess.AssemblyGuesser;
import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.UserData;
import de.thm.result.ResultCollector;
import de.thm.spring.backend.BackendConnector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.ExpressionCommand;
import de.thm.spring.command.InterfaceCommand;
import de.thm.stat.TestResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
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

    private static final Path basePath = new File("/tmp").toPath();

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

        } else {

            InterfaceCommand command = new InterfaceCommand();
            command.setOriginalFilename("");
            command.setMinBg(10000);

            model.addAttribute("interfaceCommand", command);
            model.addAttribute("bgCount", 10000);
            model.addAttribute("sigTrackCount", null);
            model.addAttribute("trackCount", null);


            ExpressionCommand exCommand = new ExpressionCommand();
            model.addAttribute("expressionCommand", exCommand);
        }

        return "result";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file, HttpSession httpSession) {

        String name = file.getOriginalFilename();
        String uuid = name + "-" + UUID.randomUUID();

        Sessions sessionControll = Sessions.getInstance();

        StatisticsCollector stats = StatisticsCollector.getInstance();

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(uuid).toFile()));
                stream.write(bytes);
                stream.close();

                Path inputFilepath = basePath.resolve(uuid);

                Session currentSession = sessionControll.addSession(httpSession.getId(), inputFilepath);

                UserData data = new UserData(AssemblyGuesser.guessAssembly(inputFilepath),inputFilepath);

                currentSession.setSites(data);
                BackendCommand command = new BackendCommand(data);

                command.addCustomTrack(currentSession.getCustomTracks());

                /////////// Run analysis ////////////
                ResultCollector collector = (ResultCollector) BackendConnector.getInstance().runAnalysis(command);
                /////////////////////////////////////

                if(collector != null) {

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

        } else {
            model.addAttribute("errorMessage", "Upload failed. No file was selected.");
            return "error";
        }

        model.addAttribute("errorMessage", "No results from backend server. Maybe the server is down right now. Try again in a few minutes or contact an admin.");
        return "error";
    }



    @RequestMapping(value = "/uploadbg", method = RequestMethod.POST)
    public String handleBackgroundUpload(Model model, @RequestParam("file") MultipartFile bgFile, HttpSession httpSession) {

        String bgname = bgFile.getOriginalFilename();
        String uuid = bgname + "-" + UUID.randomUUID();

        Sessions sessionControll = Sessions.getInstance();
        Session currentSession = sessionControll.getSession(httpSession.getId());
        StatisticsCollector stats = StatisticsCollector.getInstance();
        GenomeFactory.Assembly assembly = GenomeFactory.Assembly.hg19;


        String name = currentSession.getOriginalFilename();


        if (!bgFile.isEmpty()) {
            try {
                byte[] bytes = bgFile.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(uuid).toFile()));
                stream.write(bytes);
                stream.close();

                Path inputFilepath = basePath.resolve(uuid);
                UserData sitesBg = new UserData(assembly, inputFilepath);

                Path file = currentSession.getFile();
                UserData userSites = new UserData(GenomeFactory.Assembly.hg19, file);
                currentSession.setBgSites(sitesBg);
                currentSession.setBgFilename(bgname);

                BackendCommand backendCommand = new BackendCommand(userSites, sitesBg);

                /////////// Run analysis ////////////
                ResultCollector collector = (ResultCollector) BackendConnector.getInstance().runAnalysis(backendCommand);
                /////////////////////////////////////

                if(collector != null) {

                    currentSession.setCollector(collector);

                    setModel(model, collector, userSites, name);
                    model.addAttribute("covariants", new ArrayList<>());
                    model.addAttribute("covariantCount", 0);
                    model.addAttribute("customTracks", currentSession.getCustomTracks());
                    model.addAttribute("bgfilename", currentSession.getBgname());

                    stats.addAnaylseC();
                    stats.addFileC();

                    return "result";
                }

            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "error";
            }

        } else {
            model.addAttribute("errorMessage", "You failed to upload " + name + " because the file was empty.");
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

        Pattern interval = Pattern.compile("(chr(\\d{1,2}|X|Y))\\s(\\d*)\\s(\\d*)");
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

         if (!file.isEmpty()) {
             try {
                 byte[] bytes = file.getBytes();
                 BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(uuid).toFile()));
                 stream.write(bytes);
                 stream.close();

                 Path path = basePath.resolve(uuid);

                 List<Long> start = new ArrayList<>();
                 List<Long> end = new ArrayList<>();


                try (Stream<String> lines = Files.lines(path)) {

                    Iterator it = lines.iterator();

                    while (it.hasNext()) {

                        String line = (String) it.next();
                        Matcher line_matcher = interval.matcher(line);
                        if (line_matcher.matches()) {
                            start.add(Long.parseLong(line_matcher.group(3)) + chrSizes.offset(GenomeFactory.Assembly.hg19, line_matcher.group(1)));
                            end.add(Long.parseLong(line_matcher.group(4)) + chrSizes.offset(GenomeFactory.Assembly.hg19, line_matcher.group(1)));
                        }
                    }

                    lines.close();

                    Files.delete(path);
                    //TODO do not write file which needs to be deleted after anyway

                    Track track = TrackFactory.getInstance().createInOutTrack(start,end,name,name, GenomeFactory.Assembly.hg19);

                    currentSession.addCustomTrack(track);

                } catch (IOException e) {
                    e.printStackTrace();
                }


             } catch (Exception e){
                 e.printStackTrace();
                 return "error";
             }
         }


        return withNewTrack(currentSession, model);

    }



    @RequestMapping(value = "/trackbuilder", method = RequestMethod.POST)
    public String covariant(Model model, ExpressionCommand expressionCommand, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        Track track;
        Optional<Track> trackToUse  = BackendConnector.getInstance().createCustomTrack(expressionCommand);

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
            Path position_file= currentSession.getFile();
            UserData data = new UserData(collector.getBackgroundSites().getAssembly(), position_file);

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
