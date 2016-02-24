package de.thm.spring.controller;

import de.thm.misc.ChromosomSizes;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.ArrayList;
import java.util.List;

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
            //noinspection ResultOfMethodCallIgnored
            output.createNewFile();
            writer.write(currentSession.getCollector().getCsv());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FileSystemResource(output);
    }



    @RequestMapping(value="/export/bg", method= RequestMethod.GET)
    @ResponseBody
    public FileSystemResource exportBgSites(HttpSession httpSession) {
        Session currentSession = Sessions.getInstance().getSession(httpSession.getId());
        StatisticsCollector.getInstance().addDownloadC();
        ChromosomSizes chromosomSizes = ChromosomSizes.getInstance();

        List<String> positions = new ArrayList<>();

        for(Long pos: currentSession.getCollector().getBackgroundSites().getPositions()){
            Pair<String, Long> p = chromosomSizes.mapToChr(pos);
            positions.add(p.getLeft() + "\t" + p.getRight() + "\t" + p.getRight()+1 + "\n");
        }

        //create file
        File output = new File("/tmp/bg_output_" + httpSession.getId());

        try(BufferedWriter writer = Files.newBufferedWriter(output.toPath())){
            //noinspection ResultOfMethodCallIgnored
            output.createNewFile();

            for(String line: positions) writer.write(line);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FileSystemResource(output);
    }
}
