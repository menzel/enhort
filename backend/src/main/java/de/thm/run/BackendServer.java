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
package de.thm.run;

import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.Tracks;
import de.thm.misc.Genome;
import de.thm.precalc.SiteFactoryFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.sourceforge.argparse4j.impl.Arguments.store;


/**
 * Startes the backend server for Enhort and loads the annotation tracks
 * A ServerSocket is set up to listens on a hard coded port 42412 for input from the frontend
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendServer {

    public static String secretKey = "abcddferti5iwiei";
    private static int port;
    private static final Logger logger = LoggerFactory.getLogger(BackendServer.class);
    public static Path basePath;
    public static String dbfilepath;
    public static String customtrackpath;

    public static void main(String[] args) {

        logger.info("Starting Enhort backend server");

        Namespace input = null;

        ArgumentParser parser = ArgumentParsers.newFor("Enhort backend").build()
                .defaultHelp(true)
                .description("Enhort backend server");

        parser.addArgument("--data-path").help("Path to data directory").action(store());
        parser.addArgument("--db").help("Path to sqlite metadata database").action(store());
        parser.addArgument("--custom").help("Path to custom files").action(store());
        parser.addArgument("-p", "--port").help("Port to listen on").setDefault(42412).action(store());
        parser.addArgument("-s", "--secret").help("Keyfile for encrpytion.").setDefault("/home/mmnz21/enhort/key.dat").action(store());

        try {
            input = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }

        if (input == null) {
            logger.error("Example call: /usr/bin/java -jar -Xmx60g -XX:StringTableSize=1000003 " +
                    "/home/mmnz21/enhort/enhort.jar " +
                    "/permData/gogol/sgls22/enhort  " +
                    "/home/mmnz21/enhort/sqlitedatabase.db \n" +
                    "where the first param is the path to the data directory and" +
                    "the second path is the .db file");
            System.exit(1);
        }

        port = Integer.parseInt(input.getString("port"));

        if(input.getString("data_path") != null && input.getString("db") != null) {
            basePath = new File(input.getString("data_path")).toPath();
            dbfilepath = input.getString("db");
        } else {
            if (input.getString("custom") != null)
                customtrackpath = input.getString("custom") + "/";
        }

        new Thread(() -> {
            TrackFactory tf = TrackFactory.getInstance();
            tf.loadAllTracks();
            logger.info(tf.getTrackCount()  + " Track files loaded");

            if (logger.isDebugEnabled())
                tf.getTracks(Genome.Assembly.hg19)
                        .parallelStream()
                        .filter(track -> !Tracks.checkTrack(track))
                        .forEach(t -> logger.info("Error in track " + t.getName()));

        }).run();


        try {
            secretKey = Files.readAllLines(new File(input.getString("secret")).toPath()).get(0);
        } catch (IOException e) {
            logger.warn("No secret key. Using unsafe hard-coded key for encryption.");
        }

        //run an inital client controller, which will listen to clients
        ClientController server = new ClientController(port);

        try{

            Thread thread = new Thread(server);
            thread.run();

        } catch (Exception e){
            logger.error("Exception {}", e.getMessage(), e);
            System.exit(1);
        }

        SiteFactoryFactory.getInstance(); // preload instance of factory
    }

}

