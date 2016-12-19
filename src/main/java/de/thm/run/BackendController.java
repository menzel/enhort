package de.thm.run;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.misc.TrackBuilder;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.Command;
import de.thm.spring.command.ExpressionCommand;
import de.thm.stat.ResultCollector;

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
    private static final String prefix = "[Enhort Backend ::=]: ";

    public static void main(String[] args) {
        System.out.println(prefix + "Starting Enhort backend server");

        new Thread(() -> {
            TrackFactory tf = TrackFactory.getInstance();
            tf.loadTracks();
            System.out.println(prefix + tf.getTrackCount()  + " Track files loaded");
        }).run();

        BackendServer server = new BackendServer(port);

        try{

            Thread thread = new Thread(server);
            thread.run();

        } catch (Exception e){
            System.err.println(e.toString());
            System.exit(1);
        }
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
                System.err.println(prefix + "Port already in use: " + port);
                System.exit(1);

            } catch (IOException e) {
                e.printStackTrace();
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
                    System.out.println(prefix + "Webinterface connected");

                    inStream = new ObjectInputStream(socket.getInputStream());
                    outStream = new ObjectOutputStream(socket.getOutputStream());
                    isConnected = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //after interface is connected
                while (isConnected) {
                        Command command;
                    try {
                        command = (Command) inStream.readObject(); //wait for some input

                        if(command instanceof BackendCommand) {

                            BackgroundRunner runner = new BackgroundRunner((BackendCommand) command);

                            /////// Run Analysis ///////////
                            Future f = exe.submit(runner);
                            ////////////////////////////////

                            f.get(60, TimeUnit.SECONDS);

                        } else if(command instanceof ExpressionCommand){
                            TrackBuilder builder = new TrackBuilder();

                            /////// Build new Track
                            ExpressionCommand cmd = (ExpressionCommand) command;
                            Track track = builder.build(cmd.getExpression());
                            ///////////////////////

                            //return new track:
                            //TODO move to an extra thread
                            outStream.writeObject(track);
                        }

                    }catch (EOFException | StreamCorruptedException | SocketException e){

                        //do nothing here. client is disconected.
                        System.out.println(prefix + "Webinterface lost");
                        isConnected = false;

                    } catch (IOException | ClassNotFoundException  | InterruptedException | TimeoutException | ExecutionException e) {
                        System.err.println(e.getCause().toString() + " in BackendController Line 140");
                        exe.shutdownNow();

                        queue = new ArrayBlockingQueue<>(16);
                        exe = new ThreadPoolExecutor(1, 4, 5L, TimeUnit.MILLISECONDS, queue);

                        try {
                            outStream.writeObject(new Exception(e.getMessage()));

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                } //close while(isConnected) loop

                System.out.println(prefix + "Webinterface lost");
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
                    System.out.println(prefix + "recieved a command " + command.hashCode());

                    ResultCollector collector = new AnalysisHelper().runAnalysis(command);
                    outStream.writeObject(collector);

                    System.out.println(prefix + "answered request " + command.hashCode());

                }catch (CovariantsException e){ //if a covariant exception is thrown return it as answer
                    try {
                        outStream.writeObject(e);

                    } catch (IOException e1) {
                        System.err.println(prefix + " connection problem " + e1.getMessage());
                    }
                } catch (IOException | ClassCastException e) {
                    System.err.println(prefix + " connection problem " + e.getMessage());
                }
            }
        }
    }
}

