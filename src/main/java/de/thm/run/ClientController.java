package de.thm.run;


import de.thm.genomeData.tracks.Track;
import de.thm.misc.TrackBuilder;
import de.thm.result.Result;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.Command;
import de.thm.spring.command.ExpressionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Impl. for backend listener
 */
class ClientController implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    private static AtomicInteger clientID = new AtomicInteger(0);
    private ServerSocket serverSocket;
    private ThreadPoolExecutor clientExe = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    ClientController(int port) {

        try {

            serverSocket = new ServerSocket(port);

        } catch (BindException b){
            logger.warn("Port already in use: " + port);
            System.exit(1);

        } catch (IOException e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

        Thread load = new Thread(() -> {
            while(true) {
                logger.debug("currently connected to " + clientExe.getActiveCount() + " clients");
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        load.start();
    }

    @Override
    public void run() {


        while(!clientExe.isTerminated()) {

            try {

                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket);

                clientExe.execute(handler);

            } catch (Exception e) {
                logger.error("Exception {}", e.getMessage(), e);
            }

        } //close while(isConnected) loop
    }

    private class ClientHandler implements Runnable{

        private ObjectInputStream inStream;
        private ObjectOutputStream outStream;
        private Socket socket;
        private int clientID;

        ClientHandler(Socket socket) {

            this.socket = socket;
            this.clientID = ClientController.clientID.incrementAndGet();
            logger.info("[" + clientID + "]: " + " Webinterface connected ");

            try {
                inStream = new ObjectInputStream((socket.getInputStream()));
                outStream = new ObjectOutputStream((socket.getOutputStream()));

            } catch (IOException e) {
                logger.error("[" + clientID + "]: " + "Exception {}", e.getMessage(), e);
            }

        }

        @Override
        public void run() {

            while (!socket.isClosed()) {
                try {
                    Command command = (Command) inStream.readObject();

                    if (command instanceof BackendCommand) {

                        long time = System.currentTimeMillis();
                        logger.info("[" + clientID + "]: " + "recieved a command " + command.hashCode());

                        Result collector = new AnalysisHelper().runAnalysis((BackendCommand) command);

                        outStream.writeObject(collector);

                        long diff = System.currentTimeMillis() - time;
                        logger.info("[" + clientID + "]: " + "answered request " + command.hashCode() + " in " + diff + " mils");

                    } else if (command instanceof ExpressionCommand) {
                        TrackBuilder builder = new TrackBuilder();

                        /////// Build new Track
                        ExpressionCommand cmd = (ExpressionCommand) command;
                        Track track = builder.build(cmd.getExpression());
                        ///////////////////////

                        //return new track:
                        outStream.writeObject(track);
                    }

                } catch (Exception e) {
                    logger.error("[" + clientID + "]: " + "Exception {}", e.getMessage(), e);

                    try {
                        socket.close();

                    } catch (IOException e1) {
                        logger.error("[" + clientID + "]: " + "Exception {}", e1.getMessage(), e1);
                    }

                }

            }
        }
    }
}