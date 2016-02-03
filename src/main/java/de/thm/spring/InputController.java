package de.thm.spring;


import de.thm.backgroundModel.RandomBackgroundModel;
import de.thm.calc.IntersectMultithread;
import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;
import de.thm.positionData.WebData;
import de.thm.run.Server;
import de.thm.stat.ResultCollector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Date;

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
        if (!file.isEmpty()) {

            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(name).toFile()));
                stream.write(bytes);
                stream.close();

                File input = basePath.resolve(name).toFile();

                UserData data = new UserData(input);

                ResultCollector collector = runAnalysis(data);
                model.addAttribute("results_inout", collector.getResultsByType(Interval.Type.inout));
                model.addAttribute("results_score", collector.getResultsByType(Interval.Type.score));
                model.addAttribute("results_named", collector.getResultsByType(Interval.Type.named));

                return "result";

            } catch (Exception e) {
                System.err.println("You failed to upload " + name + " => " + e.getMessage());
            }

        } else {
            System.err.println("You failed to upload " + name + " because the file was empty.");
        }

        return null;
    }


    @RequestMapping("/input")
    public String input(HttpServletRequest request, Model model){
        Date timestamp = new Date();

        WebData data = new WebData(request.getParameter("data"), timestamp);

        ResultCollector collector = runAnalysis(data);
        model.addAttribute("results", collector.getResults());
        return "result";
    }

    private ResultCollector runAnalysis(Sites input){
        Sites bg = new RandomBackgroundModel(input.getPositionCount());

        IntersectMultithread multi = new IntersectMultithread();
        return multi.execute(Server.getIntervals(), input, bg);

    }

}
