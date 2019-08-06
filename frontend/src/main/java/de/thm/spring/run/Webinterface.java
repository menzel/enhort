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
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static net.sourceforge.argparse4j.impl.Arguments.store;

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


        Namespace input = null;

        ArgumentParser parser = ArgumentParsers.newFor("Enhort frontend").build()
                .defaultHelp(true)
                .description("Enhort frontend server");

        parser.addArgument("--ip").help("IP to connect to").action(store()).required(true);
        parser.addArgument("-p", "--port").help("Port connect to").setDefault(42412).action(store());
        parser.addArgument("--contigs-path").help("Path to contigs files.").action(store()).required(true);
        parser.addArgument("--stat-path").help("Path to store statistics.").setDefault("tmp").action(store());

        try {
            input = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        Settings.setBackendip(input.getString("ip"));
        Settings.setContigsPath(input.getString("contigs_path"));
        Settings.setStatfile_path(input.getString("stat_path"));
        Settings.setPort(input.getInt("port"));

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
