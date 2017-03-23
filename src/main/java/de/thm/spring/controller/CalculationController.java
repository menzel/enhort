package de.thm.spring.controller;


import de.thm.exception.CovariantsException;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.UserData;
import de.thm.spring.backend.BackendConnector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.ExpressionCommand;
import de.thm.spring.command.InterfaceCommand;
import de.thm.stat.ResultCollector;
import de.thm.stat.TestResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Controller for main page. Relies heavily on the model and session/command objects:
 * The model is used to get stuff to the view as well as the command object.
 * The session is used to keep information for reload and reruns.
 * <p>
 * Created by Michael Menzel on 3/2/16.
 */
@Controller
public class CalculationController {

    private static final Path basePath = new File("/tmp").toPath();


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

   @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String plainView(Model model, InterfaceCommand iCommand, HttpSession httpSession) {


        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        ResultCollector collector = currentSession.getCollector();

        if (collector != null) {
            Path file = currentSession.getFile();
            String assembly = iCommand.getAssembly();

            if(assembly == null) assembly = "hg19"; //TODO use last used assembly set by user

            UserData data = new UserData(GenomeFactory.Assembly.valueOf(assembly), file);

            List<TestResult> covariants = currentSession.getCovariants();

            setModel(model, collector, data, currentSession.getOriginalFilename());
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

        //TODO guess genome Nr
        GenomeFactory.Assembly assembly = GenomeFactory.Assembly.hg19;

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(uuid).toFile()));
                stream.write(bytes);
                stream.close();

                Path inputFilepath = basePath.resolve(uuid);

                Session currentSession = sessionControll.addSession(httpSession.getId(), inputFilepath);

                UserData data = new UserData(assembly, inputFilepath);
                BackendCommand command = new BackendCommand(data);

                command.addCustomTrack(currentSession.getCustomTracks());

                /////////// Run analysis ////////////
                ResultCollector collector = BackendConnector.getInstance().runAnalysis(command);
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
            model.addAttribute("errorMessage", "You failed to upload " + name + " because the file was empty.");
            return "error";
        }

        model.addAttribute("errorMessage", "No results from backend server. Maybe the server is down right now. Try again in a few minutes or contact an admin.");
        return "error";
    }


    @RequestMapping(value = "/covariate", method = RequestMethod.GET)
    public String covariant_get(@ModelAttribute InterfaceCommand command, Model model, HttpSession httpSession) {
        //TODO: try to reset and show results

        model.addAttribute("errorMessage", "The calculation could not be saved, please run it again");
        return "error";
    }

    @RequestMapping(value = "/covariate", method = RequestMethod.POST)
    public String covariant(@ModelAttribute InterfaceCommand command, Model model, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();

        Session currentSession = sessionsControll.getSession(httpSession.getId());
        Path file = currentSession.getFile();
        UserData data = new UserData(GenomeFactory.Assembly.valueOf(command.getAssembly()), file);

        ResultCollector collector;
        List<TestResult> covariants = new ArrayList<>();
        StatisticsCollector stats = StatisticsCollector.getInstance();
        command.setSites(data);


        //command.setCreateLogo(false);
        // remove uuid from filename for display and set it to the old InterfaceCommand, because it will be sent to the View again:
        String filename = file.toFile().getName().substring(0, file.toFile().getName().length()-37);
        filename = filename.length() > 12 ? filename.substring(0,12) +  ".." : filename;
        command.setOriginalFilename(filename);

        try {

            BackendCommand backendCommand = new BackendCommand(command);
            backendCommand.addCustomTrack(currentSession.getCustomTracks());

            /////////// Run analysis ////////////
            collector = BackendConnector.getInstance().runAnalysis(backendCommand);
            /////////////////////////////////////

            covariants = collector.getCovariants(command.getCovariants());
            currentSession.setCovariants(covariants);

            model.addAttribute("covariants", covariants);
            model.addAttribute("covariantCount", covariants.size() + (command.getLogoCovariate()? 1:0));
            model.addAttribute("customTracks", currentSession.getCustomTracks());

        } catch (CovariantsException e) {
            model.addAttribute("errorMessage", "Too many covariants, a max of " + "10 covariants is allowed.");
            collector = currentSession.getCollector();

            //TODO reset last known state: set command object and put to runAnalysis
            //covariants = currentSession.getCovariants();
        } catch (Exception e) {
            model.addAttribute(e.toString());
            collector = currentSession.getCollector();
        }

        currentSession.setCollector(collector);
        setModel(model, collector, command, covariants);

        ExpressionCommand exCommand = new ExpressionCommand();
        model.addAttribute("expressionCommand", exCommand);

        command.setPositionCount(data.getPositionCount());

        stats.addAnaylseC();
        return "result";
    }


    @RequestMapping(value = "/trackbuilder", method = RequestMethod.POST)
    public String covariant(Model model, ExpressionCommand expressionCommand, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        Track track = BackendConnector.getInstance().createCustomTrack(expressionCommand);

        currentSession.addCustomTrack(track);

        return withNewTrack(currentSession, model);
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

    /**
     * Set params for model with known interfaceCommand
     *
     * @param model - model to set params to
     * @param collector - result collector to get results from
     * @param cmd - interfaceCommand for user set params
     */
    private void setModel(Model model, ResultCollector collector, InterfaceCommand cmd, List<TestResult> covariants) {

        List<TestResult> inout = collector.getInOutResults();
        inout.removeAll(covariants);
        model.addAttribute("results_inout", inout);

        List<TestResult> score = collector.getScoredResults();
        score.removeAll(covariants);
        model.addAttribute("results_score", score);

        List<TestResult> name = collector.getNamedResults();
        name.removeAll(covariants);
        model.addAttribute("results_named", name);

        model.addAttribute("insig_results", collector.getInsignificantResults());

        cmd.setHotspots(collector.getHotspots());
        cmd.setAssembly(cmd.getAssembly() == null? "hg19": cmd.getAssembly()); //set assembly nr if there was none set in the previous run
        model.addAttribute("interfaceCommand", cmd);

        model.addAttribute("bgCount", collector.getBgCount());
        model.addAttribute("sigTrackCount", collector.getSignificantTrackCount());
        model.addAttribute("trackCount", collector.getTrackCount());

        model.addAttribute("trackPackages", collector.getKnownPackages());
        model.addAttribute("ran", true);

        Logo logo1 = collector.getLogo();
        Logo logo2 = collector.getSecondLogo();

        if(logo1 != null && logo2 != null) {
            model.addAttribute("sequencelogo", logo1.getHeights().toString());
            model.addAttribute("sequencelogo2", logo2.getHeights().toString());

            model.addAttribute("sequencelogo_name", logo1.getName());
            model.addAttribute("sequencelogo2_name", logo2.getName());
        }

        model.addAttribute("sl_effect", collector.logoEffectSize());

    }

    /**
     * Set params for model
     *
     * @param model - model to set params to
     * @param collector - collector to get results from
     * @param data - user data to get size from
     * @param filename - name of uploaded file
     */
    private void setModel(Model model, ResultCollector collector, UserData data, String filename) {

        InterfaceCommand command = new InterfaceCommand();
        command.setPositionCount(data.getPositionCount());

        //cut off filenames longer than 12 chars:
        filename = filename.length() > 12 ? filename.substring(0,12) +  ".." : filename;
        command.setOriginalFilename(filename);

        command.setMinBg(collector.getBgCount());

        setModel(model, collector, command, new ArrayList<>());


        ExpressionCommand exCommand = new ExpressionCommand();
        model.addAttribute("expressionCommand", exCommand);
    }
}
