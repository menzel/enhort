package de.thm.run;

import de.thm.exception.CovariantsException;
import de.thm.exception.NoTracksLeftException;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.genomeData.tracks.Tracks;
import de.thm.logo.GenomeFactory;
import de.thm.misc.TrackBuilder;
import de.thm.precalc.SiteFactoryFactory;
import de.thm.result.Result;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.Command;
import de.thm.spring.command.ExpressionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.*;

/**
 * Controlls requests from the Webinterface.
 *
 * Sets up a ServerSocket and listens on a hard coded port for input.
 *
 * Input is a BackendCommand.
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendController {

    private static final int port = 42412;
    private static final Logger logger = LoggerFactory.getLogger(BackendController.class);


    public static void main(String[] args) {

        logger.info("Starting Enhort backend server");

        new Thread(() -> {
            TrackFactory tf = TrackFactory.getInstance();
            tf.loadAllTracks();
            logger.info(tf.getTrackCount()  + " Track files loaded");

            if (logger.isDebugEnabled())
                tf.getTracks(GenomeFactory.Assembly.hg19)
                        .parallelStream()
                        .filter(track -> !Tracks.checkTrack(track))
                        .forEach(t -> logger.info("Error in track " + t.getName()));

        }).run();

        BackendServer server = new BackendServer(port);

        try{

            Thread thread = new Thread(server);
            thread.run();

        } catch (Exception e){
            logger.error("Exception {}", e.getMessage(), e);
            System.exit(1);
        }

        SiteFactoryFactory.getInstance(); // preload instance of factory
    }

    /**
     * Impl. for backend listener
     */
    private static class BackendServer implements Runnable{

        private ServerSocket serverSocket;
        private Socket socket;
        private ObjectInputStream inStream;
        private ObjectOutputStream outStream;


        BackendServer(int port) {
            try {

                serverSocket = new ServerSocket(port);

            } catch (BindException b){
                logger.warn("Port already in use: " + port);
                System.exit(1);

            } catch (IOException e) {
                logger.error("Exception {}", e.getMessage(), e);
            }

        }

        @Override
        public void run() {

            boolean isConnected = false;
            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(16);
            ThreadPoolExecutor exe = new ThreadPoolExecutor(1, 4, 5L, TimeUnit.MILLISECONDS, queue);

            //noinspection InfiniteLoopStatement
            while(true) {

                try {
                    socket = serverSocket.accept();
                    logger.info("Webinterface connected");

                    inStream = new ObjectInputStream(socket.getInputStream());
                    outStream = new ObjectOutputStream(socket.getOutputStream());
                    isConnected = true;

                } catch (IOException e) {
                    logger.error("Exception {}", e.getMessage(), e);
                }

                //after interface is connected
                while (isConnected) {
                    Command command = null;
                    Future f = null;

                    try {
                        command = (Command) inStream.readObject(); //wait for some input

                        if (command instanceof BackendCommand) {

                            BackgroundRunner runner = new BackgroundRunner((BackendCommand) command);

                            /////// Run Analysis ///////////
                            f = exe.submit(runner);
                            ////////////////////////////////

                            f.get(2, TimeUnit.MINUTES);

                        } else if (command instanceof ExpressionCommand) {
                            TrackBuilder builder = new TrackBuilder();

                            /////// Build new Track
                            ExpressionCommand cmd = (ExpressionCommand) command;
                            Track track = builder.build(cmd.getExpression());
                            ///////////////////////

                            //return new track:
                            //TODO move to an extra thread
                            outStream.writeObject(track);
                        }

                    }catch(TimeoutException e){
                        logger.warn("Timeout for " + command.hashCode());
                        f.cancel(true);

                    }catch (EOFException | StreamCorruptedException | SocketException e){

                        //do nothing here. client is disconected.
                        logger.info("Webinterface lost");
                        isConnected = false;

                    } catch (IOException | ClassNotFoundException  | InterruptedException | ExecutionException e) {
                        logger.error("Exception {}", e.getMessage(), e);
                        exe.shutdownNow();

                        queue = new ArrayBlockingQueue<>(16);
                        exe = new ThreadPoolExecutor(1, 4, 5L, TimeUnit.MILLISECONDS, queue);

                        try {
                            serverSocket.close();
                            socket.close();
                            serverSocket = new ServerSocket(port);
                            socket = serverSocket.accept();

                            inStream = new ObjectInputStream(socket.getInputStream());
                            outStream = new ObjectOutputStream(socket.getOutputStream());
                            isConnected = true;

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } catch (Exception e){
                        logger.error("Exception {}", e.getMessage(), e);
                    }

                } //close while(isConnected) loop

                logger.info("Webinterface lost");
            }

        }

        /**
         * Background runner class for a new analysis command object
         */
        private class BackgroundRunner implements Runnable{

            private final BackendCommand command;

            BackgroundRunner(BackendCommand command) {
                this.command = command;
            }

            @Override
            public void run() { //put stuff into background thread once data is recived
                try {
                    long time = System.currentTimeMillis();
                    logger.info("recieved a command " + command.hashCode());

                    Result collector = new AnalysisHelper().runAnalysis(command);
                    outStream.writeObject(collector);

                    long diff = System.currentTimeMillis() - time;
                    logger.info("answered request " + command.hashCode() + " in " +  diff + " mils");

                }catch (CovariantsException | NoTracksLeftException e){ //if a covariant exception is thrown return it as answer
                    try {
                        outStream.writeObject(e);

                    } catch (IOException e1) {
                        logger.warn("connection problem " + e1.getMessage());
                    }
                } catch (IOException | ClassCastException e) {
                    logger.warn("connection problem " + e.getMessage());
                    logger.error("Exception {}", e.getMessage(), e);
                } catch (Exception e){
                    logger.error("Exception {}", e.getMessage(), e);
                }
            }
        }
    }
}

