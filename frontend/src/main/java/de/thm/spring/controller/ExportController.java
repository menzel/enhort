package de.thm.spring.controller;

import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    private final Logger logger = LoggerFactory.getLogger(ExportController.class);

    @RequestMapping(value = "/export/csv", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource downloadCSV(HttpSession httpSession) {
        Session currentSession = Sessions.getInstance().getSession(httpSession.getId());

        //create file
        File output = new File("/tmp/csv_output_" + httpSession.getId());
        try (BufferedWriter writer = Files.newBufferedWriter(output.toPath())) {
            //noinspection ResultOfMethodCallIgnored
            output.createNewFile();
            writer.write(currentSession.getCollector().getBarplotdataExport());

        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

        StatisticsCollector.getInstance().addDownloadC();
        return new FileSystemResource(output);
    }

    @RequestMapping(value = "/export/bg", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource exportBgSites(HttpSession httpSession) {
        Session currentSession = Sessions.getInstance().getSession(httpSession.getId());
        ChromosomSizes chromosomSizes = ChromosomSizes.getInstance();

        List<String> positions = new ArrayList<>();

        Sites sites = currentSession.getCollector().getBackgroundSites();

        //TODO check null pointer exp where there is no bg
        for (Long pos : sites.getPositions()) {
            Pair<String, Long> p = chromosomSizes.mapToChr(sites.getAssembly(), pos);
            positions.add(p.getLeft() + " " + p.getRight() + " " + (p.getRight()+1) + "<br>");
        }

        //create file
        File output = new File("/tmp/bg_output_" + httpSession.getId());

        try (BufferedWriter writer = Files.newBufferedWriter(output.toPath())) {
            //noinspection ResultOfMethodCallIgnored
            output.createNewFile();

            for (String line : positions) writer.write(line);

        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

        StatisticsCollector.getInstance().addDownloadC();
        return new FileSystemResource(output);
    }
}
