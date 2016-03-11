package de.thm.bootstrap;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.TrackFactory;
import de.thm.serverStatistics.StatisticsCollector;
import de.thm.spring.command.RunCommand;
import de.thm.stat.ResultCollector;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendController {

    private static final int port = 42412;

    public static void main(String[] args) {

        TrackFactory.getInstance();
        System.out.println("tracks read");

        BackendServer server = new BackendServer(port);

        Thread thread = new Thread(server);

        thread.run();
    }


    private static class BackendServer implements Runnable{

        private ServerSocket serverSocket;
        private Socket socket;
        private ObjectInputStream inStream;
        private ObjectOutputStream outStream;


        public BackendServer(int port) {
            try {

                serverSocket = new ServerSocket(port);

            } catch (BindException b){
                System.err.println("Port already in use: " + port);
                System.exit(1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            attachShutDownHook();

        }

        @Override
        public void run() {

            try {
                socket = serverSocket.accept();
                System.out.println("Interface connected");
                inStream = new ObjectInputStream(socket.getInputStream());
                outStream = new ObjectOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }

            //if interface is connected
            while(true){
                try {
                    RunCommand command;
                    command = (RunCommand) inStream.readObject();
                    System.out.println("got a command");

                    ResultCollector collector = AnalysisHelper.runAnalysis(command);
                    //send collector back to interface

                    outStream.writeObject(collector);


                } catch (EOFException e){
                    //do nothing here
                    continue;
                } catch (IOException e){
                    e.printStackTrace();

                } catch (ClassCastException e){
                    e.printStackTrace();

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();

                } catch (CovariantsException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    /**
     * Shutdown hook to save the stats before exit
     */
    static void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                StatisticsCollector.getInstance().saveStats();
                System.out.println("server exiting");
            }
        });
    }
}

