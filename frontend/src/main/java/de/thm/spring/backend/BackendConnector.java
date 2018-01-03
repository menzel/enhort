package de.thm.spring.backend;

import de.thm.command.BackendCommand;
import de.thm.command.ExpressionCommand;
import de.thm.exception.CovariatesException;
import de.thm.exception.NoTracksLeftException;
import de.thm.genomeData.tracks.Track;
import de.thm.result.DataViewResult;
import de.thm.result.Result;
import de.thm.result.ResultCollector;
import de.thm.security.Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SealedObject;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Connects the interface to a backend server. Sends backendCommands and recives resultCollectors.
 *
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendConnector {
    private final Logger logger = LoggerFactory.getLogger(BackendConnector.class);

    private final int port;
    private final String ip;
    private boolean isConnected = false;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;
    private static AtomicInteger clientID = new AtomicInteger(0);
    private int id;

    private String secret; // = "UOziBurnfxzcgy9CNU5HAb6a19k0SVAP";


    BackendConnector(){

        this.port = 42412;

        if (System.getenv("HOME").contains("menzel"))
            this.ip = "127.0.0.1";
        else
            this.ip = "bioinf-ladon.mni.thm.de";

        id = clientID.getAndIncrement();


        try {
            secret = Files.readAllLines(new File("key.dat").toPath()).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Connect to backend if is not already connected
     *
     */
    private void connect(){

        if (!isConnected) {

            logger.info("[" + id + "]: Starting backend connection to " + this.ip);
            int connectionTimeout = 2; //max tries timeout
            int tries = 0;

            while (!isConnected) {
                try {
                    socket = new Socket(ip, port);

                    isConnected = socket.isConnected();
                    logger.info("[" + id + "]: Created " + isConnected + " socket on port: " + port);

                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    socket.setSoTimeout(120 * 1000);

                } catch (IOException e) {
                    logger.warn("[" + id + "]: Cannot connect to backend: " + ip + " reason: " + e.getMessage());
                }
                try {
                    tries++;
                    if (tries >= connectionTimeout)
                        return;

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("Exception {}", e.getMessage(), e);
                }
            }

            logger.info("[" + id + "]: Connected to backend");
        }
    }

    /**
     * Implements the command pattern. Accepts a command object with data and directives to execute.
     *
     * @param command - data and directives
     * @return Results of the executed commnads
     * @throws CovariatesException - if too many or impossible combination of covariants is given
     */
    public Result runAnalysis(BackendCommand command) throws SocketTimeoutException {

        connect();


        if (isConnected) {
            try {

                logger.info("[" + id + "]: writing command");

                outputStream.writeObject(Crypt.encrypt(command, secret));

                logger.info("[" + id + "]: waiting for result");

                //TODO only wait for fixed time. apply timeout

                Object answer = Crypt.decrypt((SealedObject) inputStream.readObject(), secret);

                ResultCollector collector;

                if (answer instanceof Exception) {

                    if (answer instanceof CovariatesException)
                        throw (CovariatesException) answer;
                    if (answer instanceof NoTracksLeftException)
                        throw (NoTracksLeftException) answer;


                    logger.info("[" + id + "]: got exception: " + ((Exception) answer).getMessage());

                } else if (answer instanceof ResultCollector) {

                    collector = (ResultCollector) answer;
                    logger.info("[" + id + "]: got result: " + collector.getResults().size());
                    //TODO check collector for correct answers:

                    checkCollector(collector);

                    return collector;

                } else if (answer instanceof DataViewResult) {
                    DataViewResult result = (DataViewResult) answer;
                    logger.info("[" + id + "]: got data table: " + result.getPackages().size());

                    return result;


                } else throw new IllegalArgumentException("answer is not a result: " + answer.getClass());

            } catch (SocketTimeoutException e) {
                logger.error("Exception {}", e.getMessage());
                throw new SocketTimeoutException("The backend took to long to respond. Maybe there are too many sites");

            } catch (IOException | ClassNotFoundException e) {
                isConnected = false;
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                logger.error("Exception {}", e.getMessage(), e);
                logger.warn("Something went wrong in the BackendConnector. Trying to start all over again");
            } catch (Exception e) {
                logger.error("Exception {}", e.getMessage(), e);
                logger.warn("Something went wrong in the BackendConnector." + e.getMessage());
            }
        } else {

            logger.warn("[" + id + "]: No connection to backend");


            //this.run(); //try to connect to backend again
            return null; //runAnalysis(command);
        }

        return null;
    }

    private void checkCollector(ResultCollector collector) {
        if(collector == null)
            logger.error("Result is null");
        else {

            if (collector.getHotspots() == null) {
                logger.warn("BackendConnector: No Hotspots");
            }

            if (collector.getResults().size() < 1) {
                logger.warn("BackendConnector: Not Results in Collector");
            }
        }

    }

    public Optional<Track> createCustomTrack(ExpressionCommand expressionCommand) {

        connect();

        if (isConnected) {
            try {

                logger.info("[" + id + "]: writing command");

                outputStream.writeObject(expressionCommand);

                logger.info("[" + id + "]: waiting for result");

                Object answer = inputStream.readObject();

                Track track;

                if (answer instanceof Exception) {

                    logger.warn("[" + id + "]: got exception: " + ((Exception) answer).getMessage());

                } else if (answer instanceof Track) {

                    track = (Track) answer;

                    return Optional.of(track);

                } else {
                    logger.warn("answer is not a result: " + answer.getClass());
                    return Optional.empty();
                }


            } catch (IOException | ClassNotFoundException e) {
                isConnected = false;
                logger.warn("Something went wrong in the BackendConnector. Trying to start all over again" + e);
            }
        }

        logger.warn("No connection in createCustomTrack");
        return Optional.empty();
    }

    /**
     * Close the connection to backend (close socket)
     */
    public void close() {
        try{
            if(socket != null)
                socket.close();
        } catch (IOException e){
            logger.error("Exception {}", e.getMessage(), e);
        }
    }
}
