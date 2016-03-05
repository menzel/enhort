package de.thm.spring.controller;


import de.thm.exception.CovariantsException;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.UserData;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.spring.command.CovariantCommand;
import de.thm.spring.helper.AnalysisHelper;
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

        } else {

            CovariantCommand command = new CovariantCommand();
            command.setOriginalFilename("");
            command.setMinBg(10000);

            model.addAttribute("covariantCommand", command);
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
        StatisticsCollector.getInstance().addFileC();
        StatisticsCollector.getInstance().addAnaylseC();


        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(uuid).toFile()));
                stream.write(bytes);
                stream.close();

                Path inputFilepath = basePath.resolve(uuid);

                Session currentSession = sessionControll.addSession(httpSession.getId(), inputFilepath);

                UserData data = new UserData(inputFilepath);
                ResultCollector collector = AnalysisHelper.runAnalysis(data);
                currentSession.setCollector(collector);
                currentSession.setOriginalFilename(name);

                setModel(model, collector, data, name);
                model.addAttribute("covariants", new ArrayList<>());
                model.addAttribute("covariantCount", 0);

                return "result";

            } catch (Exception e) {
                StatisticsCollector.getInstance().addErrorC();
                System.err.println("You failed to upload " + name + " => " + e.getMessage());
                return null;
            }

        } else {
            StatisticsCollector.getInstance().addErrorC();
            System.err.println("You failed to upload " + name + " because the file was empty.");
            return null;
        }
    }


    @RequestMapping(value = "/covariant", method = RequestMethod.POST)
    public String covariant(@ModelAttribute CovariantCommand command, Model model, HttpSession httpSession) {

        Sessions sessionsControll = Sessions.getInstance();
        StatisticsCollector.getInstance().addAnaylseC();

        Session currentSession = sessionsControll.getSession(httpSession.getId());
        Path file = currentSession.getFile();
        UserData data = new UserData(file);

        ResultCollector collector;
        List<TestResult> covariants;

        try {
            collector = AnalysisHelper.runAnalysis(data, command);

            covariants = collector.getCovariants(command.getCovariants());
            currentSession.setCovariants(covariants);

            model.addAttribute("covariants", covariants);
            model.addAttribute("covariantCount", covariants.size());

        } catch (CovariantsException e) {
            model.addAttribute("errorMessage", "Too many covariants, a max of " + "10 covariants is allowed.");
            collector = currentSession.getCollector();

            if (collector == null) //if there is no collector known to the session run with no covariants
                collector = AnalysisHelper.runAnalysis(data);
            //TODO reset last known state: set command object and put to runAnalysis
            //covariants = currentSession.getCovariants();
        }


        currentSession.setCollector(collector);
        setModel(model, collector, data, currentSession.getOriginalFilename());

        command.setPositionCount(data.getPositionCount());
        return "result";
    }


    /**
     * Set params for model
     *
     * @param model - model to set params to
     * @param collector - colletor to get results from
     * @param data - user data to get size from
     * @param filename - name of uploaded file
     */
    private void setModel(Model model, ResultCollector collector, UserData data, String filename) {

        model.addAttribute("results_inout", collector.getInOutResults());
        model.addAttribute("results_score", collector.getScoredResults());
        model.addAttribute("results_named", collector.getNamedResults());


        CovariantCommand command = new CovariantCommand();
        command.setPositionCount(data.getPositionCount());
        command.setOriginalFilename(filename);
        command.setMinBg(collector.getBgCount());

        model.addAttribute("covariantCommand", command);
        model.addAttribute("bgCount", collector.getBgCount());
        model.addAttribute("sigTrackCount", collector.getSignificantTrackCount());
        model.addAttribute("trackCount", collector.getTrackCount());

        model.addAttribute("trackPackages", TrackFactory.getInstance().getTrackPackageNames());

    }
}
