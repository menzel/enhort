package de.thm.run;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.TrackFactory;
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
        System.out.println("[Enhort Backend]: Starting Enhort backend server");

        TrackFactory.getInstance();
        System.out.println("[Enhort Backend]: Track files loaded");

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
                System.err.println("[Enhort Backend]: Port already in use: " + port);
                System.exit(1);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {

            boolean isConnected = false;

            while(true) {

                try {
                    socket = serverSocket.accept();
                    System.out.println("[Enhort Backend]: Webinterface connected");

                    inStream = new ObjectInputStream(socket.getInputStream());
                    outStream = new ObjectOutputStream(socket.getOutputStream());
                    isConnected = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //if interface is connected
                while (isConnected) {
                    try {
                        RunCommand command;
                        command = (RunCommand) inStream.readObject();

                        System.out.println("[Enhort Backend]: recieved a command " + command.hashCode());

                        ResultCollector collector = AnalysisHelper.runAnalysis(command);

                        System.out.println("[Enhort Backend]: answered request " + command.hashCode());

                        outStream.writeObject(collector);


                    } catch (EOFException e) {
                        isConnected = false;
                    } catch (IOException | ClassCastException | ClassNotFoundException | CovariantsException e) {
                        isConnected = false;
                        e.printStackTrace();
                    }

                }
            }

        }

    }

}

