package de.thm.spring.controller;


import de.thm.genomeData.Interval;
import de.thm.positionData.UserData;
import de.thm.spring.command.CovariantCommand;
import de.thm.spring.helper.AnalysisHelper;
import de.thm.stat.ResultCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@Controller
public class InputController {

    private static Path basePath = new File("/tmp").toPath();


    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public String handleFileUpload(Model model, @RequestParam("name") String name, @RequestParam("file") MultipartFile file){

        String uuid = name + "-" + UUID.randomUUID();
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(uuid).toFile()));
                stream.write(bytes);
                stream.close();

                Path inputFilepath = basePath.resolve(uuid);

                UserData data = new UserData(inputFilepath);
                ResultCollector collector = AnalysisHelper.runAnalysis(data);

                model.addAttribute("results_inout", collector.getResultsByType(Interval.Type.inout));
                model.addAttribute("results_score", collector.getResultsByType(Interval.Type.score));
                model.addAttribute("results_named", collector.getResultsByType(Interval.Type.named));

                CovariantCommand command = new CovariantCommand();
                command.setFilepath(inputFilepath.toString());
                command.setPositionCount(data.getPositionCount());
                command.setOriginalFilename(name);
                command.setUserData(data);

                model.addAttribute("covariantCommand", command);
                model.addAttribute("bgHash", collector.getBgModelHash());
                model.addAttribute("bgCount", collector.getResults().get(0).getExpectedIn() + collector.getResults().get(0).getExpectedOut());

                return "result";

            } catch (Exception e) {
                System.err.println("You failed to upload " + name + " => " + e.getMessage());
                return null;
            }

        } else {
            System.err.println("You failed to upload " + name + " because the file was empty.");
            return null;
        }
    }

}
