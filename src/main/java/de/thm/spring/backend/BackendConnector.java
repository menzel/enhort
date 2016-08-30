package de.thm.spring.backend;

import de.thm.exception.CovariantsException;
import de.thm.spring.command.BackendCommand;
import de.thm.stat.ResultCollector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Connects the interface to a backend server. Sends backendCommands and recives resultCollectors.
 *
 *
 * Created by Michael Menzel on 11/3/16.
 */
public final class BackendConnector implements Runnable{
    private static BackendConnector instance;

    static {
        // check if the instance is running on a local machine or ladon

        if(System.getenv("HOME").contains("menzel")){
            instance = new BackendConnector(42412, "127.0.0.1");
        } else {
            instance = new BackendConnector(42412, "bioinf-ladon.mni.thm.de");
        }
    }

    private final int port;
    private final String ip;
    private Socket socket;
    private boolean isConnected = false;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private BackendConnector(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public static BackendConnector getInstance(){
        return instance;
    }

    @Override
    public void run() {

        System.out.println("[Enhort Webinterface]: Starting backend connection to " + this.ip);
        int connectionTimeout = 40; //max tries timeout
        int tries = 0;

        while (!isConnected){
            try {
                socket = new Socket(ip, port);

                isConnected = socket.isConnected();
                System.out.println("[Enhort Webinterface]: created " + isConnected +  " socket on port: " + port);

                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

            } catch (IOException e) {
                System.err.println("[Enhort Webinterface]: Cannot connect to backend: " + ip + " reason: " + e.getMessage());
            }
            try {
                tries++;
                if(tries >= connectionTimeout)
                    return;

                Thread.sleep(500);
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
    public ResultCollector runAnalysis(BackendCommand command) throws Exception {

        if(isConnected){
            try {

                System.out.println("[Enhort Webinterface]: writing command");
                outputStream.writeObject(command);

                //TODO only wait for fixed time. apply timeout
                System.out.println("[Enhort Webinterface]: waiting for result");
                Object answer = inputStream.readObject();
                ResultCollector collector = null;

                if(answer instanceof Exception){

                    System.out.println("[Enhort Webinterface]: got exception: " + ((Exception) answer).getMessage());
                    throw (Exception) answer;
                } else if( answer instanceof  ResultCollector){
                    collector = (ResultCollector) answer;
                } else {
                    System.err.println("answer is not a result: " + answer.getClass());
                    return null;
                }

                System.out.println("[Enhort Webinterface]: got result: " + collector.getResults().size());
                return collector;

            } catch(SocketException e){
                isConnected = false;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println("[Enhort Webinterface]: No connection to backend");
        StatisticsCollector.getInstance().addErrorC();

        this.run(); //try to connect to backend again

        if(isConnected)
            return runAnalysis(command); //only call run again if backend is connected.
        else
            return null;
    }
}
