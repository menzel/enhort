package de.thm.run;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.TrackFactory;
import de.thm.spring.command.backendCommand;
import de.thm.stat.ResultCollector;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Controlls requests from the Webinterface.
 *
 * Sets up a ServerSocket and listens on a hard coded port for input.
 *
 * Input is a backendCommand.
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendController {

    private static final int port = 42412;
    private static final String prefix = "[Enhort Backend ::=]: ";

    public static void main(String[] args) {
        System.out.println(prefix + "Starting Enhort backend server");

        TrackFactory tf = TrackFactory.getInstance();
        System.out.println(prefix + tf.getAllIntervals().size()  + " Track files loaded");

        BackendServer server = new BackendServer(port);

        Thread thread = new Thread(server);
        thread.run();
    }


    /**
     * Impl. for backend listener
     */
    private static class BackendServer implements Runnable{

        private ServerSocket serverSocket;
        private Socket socket;
        private ObjectInputStream inStream;
        private ObjectOutputStream outStream;


        public BackendServer(int port) {
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
                        backendCommand command;
                    try {
                        command = (backendCommand) inStream.readObject(); //wait for some input

                        BackgroundRunner runner = new BackgroundRunner(command);

                        new Thread(runner).run(); //maybe use a thread pool here to prevent unlimited threads running and prevent one thread running forever


                    }catch (EOFException e){
                        //do nothing here. client is disconected.
                        isConnected = false;

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } //close while(isConnected) loop

                System.out.println(prefix + "Webinterface lost");
            }

        }

        /**
         * Background runner class for a new command object
         */
        private class BackgroundRunner implements Runnable{

            private final backendCommand command;

            public BackgroundRunner(backendCommand command) {
                this.command = command;
            }

            @Override
            public void run() { //put stuff into background thread once data is recived
                try {
                    System.out.println(prefix + "recieved a command " + command.hashCode());

                    ResultCollector collector = AnalysisHelper.runAnalysis(command);
                    System.out.println(prefix + "writing answer " + command.hashCode());

                    outStream.writeObject(collector);
                    System.out.println(prefix + "answered request " + command.hashCode());

                } catch (IOException | ClassCastException | CovariantsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

