package de.thm.spring;


import de.thm.backgroundModel.RandomBackgroundModel;
import de.thm.calc.IntersectMultithread;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;
import de.thm.positionData.WebData;
import de.thm.run.Server;
import de.thm.stat.ResultCollector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Date;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@RestController
public class InputController {

    private static Path basePath = new File("/tmp").toPath();

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name, @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {

            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(basePath.resolve(name).toFile()));
                stream.write(bytes);
                stream.close();

                File input = basePath.resolve(name).toFile();

                UserData data = new UserData(input);

                return runAnalysis(data, "");

            } catch (Exception e) {
                System.err.println("You failed to upload " + name + " => " + e.getMessage());
            }

        } else {
            System.err.println("You failed to upload " + name + " because the file was empty.");
        }

        return null;
    }


    @RequestMapping("/input")
    public String input(HttpServletRequest request){
        String data = request.getParameter("data");
        String format = request.getParameter("format");
        Date timestamp = new Date();

        WebData input = new WebData(data, timestamp);

        return runAnalysis(input, format);
    }

    private String runAnalysis(Sites input, String format){
        Sites bg = new RandomBackgroundModel(input.getPositionCount());
        ModelAndView modelAndView = new ModelAndView("results");
        String result;

        IntersectMultithread multi = new IntersectMultithread(Server.getIntervals(), input, bg);


        if(format != null && format.equals("json"))
            result = ResultCollector.getInstance().toJson();
        else
            result = ResultCollector.getInstance().toString().replaceAll("\n", "<br />");

        modelAndView.addObject("results", ResultCollector.getInstance().getResults());

        ResultCollector.getInstance().clear();

        return  result;
    }

}
