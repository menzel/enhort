package de.thm.run;

import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.Tracks;
import de.thm.misc.Genome;
import de.thm.precalc.SiteFactoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Startes the backend server for Enhort and loads the annotation tracks
 * A ServerSocket is set up to listens on a hard coded port 42412 for input from the frontend
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendServer {

    private static final int port = 42412;
    private static final Logger logger = LoggerFactory.getLogger(BackendServer.class);


    public static void main(String[] args) {

        logger.info("Starting Enhort backend server");

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

