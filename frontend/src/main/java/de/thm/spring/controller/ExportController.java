// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.spring.controller;

import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import de.thm.spring.backend.Session;
import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.StatisticsCollector;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
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
    public void downloadCSV(HttpSession httpSession, HttpServletResponse response) {

        Session currentSession = null;
        try {
            currentSession = Sessions.getInstance().getSessionOrError(httpSession.getId());
        } catch (Exception e) {
            logger.error("Exception {}", e.getMessage(), e);
            //TODO show error message, there is no data to download
        }

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

        response.setContentType("text/plain");
        response.addHeader("Content-Disposition", "attachment; filename=results.csv");

        try {
            Files.copy(output.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/export/bg", method = RequestMethod.GET)
    public void exportBgSites(HttpSession httpSession, HttpServletResponse response) {
        Session currentSession = Sessions.getInstance().getSession(httpSession.getId());
        ChromosomSizes chromosomSizes = ChromosomSizes.getInstance();

        List<String> positions = new ArrayList<>();

        Sites sites = currentSession.getCollector().getBackgroundSites();

        //TODO check null pointer exp where there is no bg
        for (Long pos : sites.getPositions()) {
            Pair<String, Long> p = chromosomSizes.mapToChr(sites.getAssembly(), pos);
            positions.add(p.getLeft() + "\t" + p.getRight() + "\t" + (p.getRight()+1) + "\n");
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

        response.setContentType("text/plain");
        response.addHeader("Content-Disposition", "attachment; filename=enhort_background_sites.bed");

        try {
            Files.copy(output.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }
    }
}
