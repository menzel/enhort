package de.thm.spring.controller;


import de.thm.exception.TooManyCovariantsException;
import de.thm.genomeData.Interval;
import de.thm.positionData.UserData;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import de.thm.spring.command.CovariantCommand;
import de.thm.spring.helper.AnalysisHelper;
import de.thm.stat.ResultCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@Controller
public class CalculationController {

    private static Path basePath = new File("/tmp").toPath();

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public String plainView(Model model, HttpSession httpSession){

        CovariantCommand command = new CovariantCommand();

        Sessions sessionsControll = Sessions.getInstance();
        Session currentSession = sessionsControll.getSession(httpSession.getId());

        ResultCollector collector = currentSession.getCollector();

        if(collector != null){

            model.addAttribute("results_inout", collector.getResultsByType(Interval.Type.inout));
            model.addAttribute("results_score", collector.getResultsByType(Interval.Type.score));
            model.addAttribute("results_named", collector.getResultsByType(Interval.Type.named));


            model.addAttribute("covariants", collector.getCovariants());

            Path file = currentSession.getFile();
            UserData data = new UserData(file);

            command.setPositionCount(data.getPositionCount());
            model.addAttribute("covariantCommand", command);
            model.addAttribute("bgHash", collector.getBgModelHash());
            model.addAttribute("bgCount", collector.getResults().get(0).getExpectedIn() + collector.getResults().get(0).getExpectedOut());

        }

        model.addAttribute("covariantCommand", command);
        return "result";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file, HttpSession httpSession){

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

                model.addAttribute("results_inout", collector.getResultsByType(Interval.Type.inout));
                model.addAttribute("results_score", collector.getResultsByType(Interval.Type.score));
                model.addAttribute("results_named", collector.getResultsByType(Interval.Type.named));

                model.addAttribute("covariants", collector.getCovariants());

                CovariantCommand command = new CovariantCommand();
                //command.setFilepath(inputFilepath.toString());
                command.setPositionCount(data.getPositionCount());
                command.setOriginalFilename(name);
                command.setUserData(data);

                model.addAttribute("covariantCommand", command);
                model.addAttribute("bgHash", collector.getBgModelHash());
                model.addAttribute("bgCount", collector.getResults().get(0).getExpectedIn() + collector.getResults().get(0).getExpectedOut());

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

      // Error page
      @RequestMapping("/error.html")
      public String error(HttpServletRequest request, Model model) {
        model.addAttribute("errorCode", request.getAttribute("javax.servlet.error.status_code"));
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String errorMessage = null;
        if (throwable != null) {
          errorMessage = throwable.getMessage();
        }
        model.addAttribute("errorMessage", errorMessage);
        return "error.html";
      }


    @RequestMapping(value="/covariant", method= RequestMethod.POST)
    public String covariant(@ModelAttribute CovariantCommand command, Model model, HttpSession httpSession){

        Sessions sessionsControll = Sessions.getInstance();
        StatisticsCollector.getInstance().addAnaylseC();

        Session currentSession = sessionsControll.getSession(httpSession.getId());
        Path file = currentSession.getFile();
        UserData data = new UserData(file);

        ResultCollector collector;
        try {
            collector = AnalysisHelper.runAnalysis(data,command.getCovariants());

        } catch (TooManyCovariantsException e) {
            model.addAttribute("errorMessage", "Too many covariants, a max of 7 covariants is allowed.");
            collector = currentSession.getCollector();

            if(collector == null) //if there is no collector known to the session run with no covariants
                collector = AnalysisHelper.runAnalysis(data);
        }

        currentSession.setCollector(collector);


        model.addAttribute("results_inout", collector.getResultsByType(Interval.Type.inout));
        model.addAttribute("results_score", collector.getResultsByType(Interval.Type.score));
        model.addAttribute("results_named", collector.getResultsByType(Interval.Type.named));


        model.addAttribute("covariants", collector.getCovariants());

        command.setPositionCount(data.getPositionCount());
        model.addAttribute("covariantCommand", command);
        model.addAttribute("bgHash", collector.getBgModelHash());
        model.addAttribute("bgCount", collector.getResults().get(0).getExpectedIn() + collector.getResults().get(0).getExpectedOut());


        return "result";
    }
}
