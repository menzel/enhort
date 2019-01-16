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
package de.thm.spring.run;

import de.thm.spring.backend.Sessions;
import de.thm.spring.backend.Settings;
import de.thm.spring.backend.StatisticsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Server
 *
 * Created by Michael Menzel on 3/2/16.
 */
@SpringBootApplication
@ComponentScan(basePackages = "de.thm.spring")
public class Webinterface {

    private static final Logger logger = LoggerFactory.getLogger(Webinterface.class);

    public static void main(String[] args) {

        /* Set up Settings */

        Settings.setBackendip(args[0]);
        Settings.setContigsPath(args[1]);
        Settings.setLogfile_path(args[2]);

        try {

            SpringApplication.run(Webinterface.class, args);

            Sessions.getInstance().addSession("monitorsession");

            attachShutDownHook();

        } catch (Exception e) {
            logger.error("Exception {}", e.getMessage(), e);
            StatisticsCollector.getInstance().addErrorC();
        }
    }

    /**
     * Shutdown hook to save the stats before exit
     */
    private static void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> StatisticsCollector.getInstance().saveStats()));
    }
}
