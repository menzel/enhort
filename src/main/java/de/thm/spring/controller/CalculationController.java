package de.thm.spring.controller;


import de.thm.exception.CovariantsException;
import de.thm.positionData.UserData;
import de.thm.spring.backend.BackendConnector;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.spring.command.BackendCommand;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String plainView(Model model, HttpSession httpSession) {


        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        ResultCollector collector = currentSession.getCollector();

        if (collector != null) {
            Path file = currentSession.getFile();
            UserData data = new UserData(file);

            List<TestResult> covariants = currentSession.getCovariants();

            setModel(model, collector, data, currentSession.getOriginalFilename());
            model.addAttribute("covariants", covariants);
            model.addAttribute("covariantCount", covariants.size());

            StatisticsCollector.getInstance().addAnaylseC();

        } else {

            InterfaceCommand command = new InterfaceCommand();
            command.setOriginalFilename("");
            command.setMinBg(10000);

            model.addAttribute("interfaceCommand", command);
            model.addAttribute("bgCount", 10000);
            model.addAttribute("sigTrackCount", null);
            model.addAttribute("trackCount", null);

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

                UserData data = new UserData(inputFilepath);
                BackendCommand command = new BackendCommand(data);

                //run analysis:
                ResultCollector collector = BackendConnector.getInstance().runAnalysis(command);

                if(collector != null) {

                    currentSession.setCollector(collector);
                    currentSession.setOriginalFilename(name);

                    setModel(model, collector, data, name);
                    model.addAttribute("covariants", new ArrayList<>());
                    model.addAttribute("covariantCount", 0);

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


    @RequestMapping(value = "/covariant", method = RequestMethod.POST)
    public String covariant(@ModelAttribute InterfaceCommand command, Model model, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();

        Session currentSession = sessionsControll.getSession(httpSession.getId());
        Path file = currentSession.getFile();
        UserData data = new UserData(file);

        ResultCollector collector;
        List<TestResult> covariants = new ArrayList<>();
        StatisticsCollector stats = StatisticsCollector.getInstance();
        command.setSites(data);

        try {

            collector = BackendConnector.getInstance().runAnalysis(new BackendCommand(command));

            covariants = collector.getCovariants(command.getCovariants());
            currentSession.setCovariants(covariants);

            model.addAttribute("covariants", covariants);
            model.addAttribute("covariantCount", covariants.size());

        } catch (CovariantsException e) {
            model.addAttribute("errorMessage", "Too many covariants, a max of " + "10 covariants is allowed.");
            collector = currentSession.getCollector();

            //TODO reset last known state: set command object and put to runAnalysis
            //covariants = currentSession.getCovariants();
        } catch (Exception e) {
            model.addAttribute(e.getMessage());
            collector = currentSession.getCollector();
        }

        currentSession.setCollector(collector);
        setModel(model, collector, command, covariants);

        command.setPositionCount(data.getPositionCount());

        stats.addAnaylseC();
        return "result";
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

        model.addAttribute("interfaceCommand", cmd);

        model.addAttribute("bgCount", collector.getBgCount());
        model.addAttribute("sigTrackCount", collector.getSignificantTrackCount());
        model.addAttribute("trackCount", collector.getTrackCount());

        //TODO AAAAARG:
        model.addAttribute("trackPackages", collector.getKnownPackages());
        model.addAttribute("ran", true);

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
        command.setOriginalFilename(filename);
        command.setMinBg(collector.getBgCount());

        setModel(model, collector, command, new ArrayList<>());

    }
}
