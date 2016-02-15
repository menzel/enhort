package de.thm.spring.controller;

import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Michael Menzel on 15/2/16.
 */
@Controller
public class ExportController {

    @RequestMapping(value="/export/csv", method= RequestMethod.GET)
    @ResponseBody
    public FileSystemResource downloadCSV(HttpSession httpSession) {
        Session currentSession = Sessions.getInstance().getSession(httpSession.getId());
        StatisticsCollector.getInstance().addDownloadC();

        //create file
        File output = new File("/tmp/csv_output_" + httpSession.getId());
        try(BufferedWriter writer = Files.newBufferedWriter(output.toPath())){
            output.createNewFile();
            writer.write(currentSession.getCollector().getCsv());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FileSystemResource(output);
    }
}
