package de.thm.spring.backend;

import de.thm.exception.CovariantsException;
import de.thm.genomeData.Track;
import de.thm.spring.command.BackendCommand;
import de.thm.spring.command.ExpressionCommand;
import de.thm.stat.ResultCollector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Optional;

/**
 * Connects the interface to a backend server. Sends backendCommands and recives resultCollectors.
 *
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendConnector implements Runnable {
    private static BackendConnector instance;

    static {
        // check if the instance is running on a local machine or ladon

        if (System.getenv("HOME").contains("menzel")) {
            instance = new BackendConnector(42412, "127.0.0.1");
        } else {
            instance = new BackendConnector(42412, "bioinf-ladon.mni.thm.de");
        }
    }

    private final int port;
    private final String ip;
    private boolean isConnected = false;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;

    private BackendConnector(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public static BackendConnector getInstance() {
        return instance;
    }

    @Override
    public void run() {

        System.out.println("[Enhort Webinterface]: Starting backend connection to " + this.ip);
        int connectionTimeout = 20; //max tries timeout
        int tries = 0;

        while (!isConnected) {
            try {
                socket = new Socket(ip, port);

                isConnected = socket.isConnected();
                System.out.println("[Enhort Webinterface]: Created " + isConnected + " socket on port: " + port);

                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                socket.setSoTimeout(120 * 1000);

            } catch (IOException e) {
                System.err.println("[Enhort Webinterface]: Cannot connect to backend: " + ip + " reason: " + e.getMessage());
            }
            try {
                tries++;
                if (tries >= connectionTimeout)
                    return;

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[Enhort Webinterface]: Connected to backend");

    }


    /**
     * Implements the command pattern. Accepts a command object with data and directives to execute.
     *
     * @param command - data and directives
     * @return Results of the executed commnads
     * @throws CovariantsException - if too many or impossible combination of covariants is given
     */
    public ResultCollector runAnalysis(BackendCommand command) throws CovariantsException, SocketTimeoutException {

        if (isConnected) try {

            System.out.println("[Enhort Webinterface]: writing command");
            outputStream.writeObject(command);

            System.out.println("[Enhort Webinterface]: waiting for result");

            //TODO only wait for fixed time. apply timeout

            Object answer = inputStream.readObject();

            ResultCollector collector;

            if (answer instanceof Exception) {

                if (answer instanceof CovariantsException)
                    throw (CovariantsException) answer;

                System.out.println("[Enhort Webinterface]: got exception: " + ((Exception) answer).getMessage());

            } else if (answer instanceof ResultCollector) {

                collector = (ResultCollector) answer;
                System.out.println("[Enhort Webinterface]: got result: " + collector.getResults().size());
                //TODO check collector for correct answers:

                checkCollector(collector);

                return collector;

            } else throw new IllegalArgumentException("answer is not a result: " + answer.getClass());

        }  catch (SocketTimeoutException e){
            e.printStackTrace();
            throw new SocketTimeoutException("The backend took to long to respond. Maybe there are too many sites");

        } catch (IOException | ClassNotFoundException e) {
            isConnected = false;
            System.err.println("Something went wrong in the BackendConnector. Trying to start all over again");
        }

        System.out.println("[Enhort Webinterface]: No connection to backend");
        //StatisticsCollector.getInstance().addErrorC();

        this.run(); //try to connect to backend again

        if (isConnected) {
            //TODO check for endless recursion

            try {
                Thread.sleep(5000);

            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted after error state. Should not be a problem.");
            }
            return null;
            //return runAnalysis(command); //only call run again if backend is connected.
        } else
            return null;
    }

    private void checkCollector(ResultCollector collector) {

        if(collector.getHotspots() == null){
            System.err.println("BackendConnector: No Hotspots");
        }

        if(collector.getResults().size() < 1){
            System.err.println("BackendConnector: Not Results in Collector");
        }

    }

    public Optional<Track> createCustomTrack(ExpressionCommand expressionCommand) {

        if (isConnected) {
            try {

                System.out.println("[Enhort Webinterface]: writing command");

                outputStream.writeObject(expressionCommand);

                System.out.println("[Enhort Webinterface]: waiting for result");

                Object answer = inputStream.readObject();

                Track track;

                if (answer instanceof Exception) {

                    System.err.println("[Enhort Webinterface]: got exception: " + ((Exception) answer).getMessage());

                } else if (answer instanceof Track) {

                    track = (Track) answer;

                    return Optional.of(track);

                } else {
                    System.err.println("answer is not a result: " + answer.getClass());
                    return Optional.empty();
                }


            } catch (IOException | ClassNotFoundException e) {
                isConnected = false;
                System.err.println("Something went wrong in the BackendConnector. Trying to start all over again" + e);
            }
        }

        System.err.println("No connection in createCustomTrack");
        return Optional.empty();
    }
}
