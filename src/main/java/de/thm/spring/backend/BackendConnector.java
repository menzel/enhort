package de.thm.spring.backend;

import de.thm.spring.command.backendCommand;
import de.thm.stat.ResultCollector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Michael Menzel on 11/3/16.
 */
public class BackendConnector implements Runnable{
    private static BackendConnector instance = new BackendConnector(42412, "127.0.0.1");
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
        while (!isConnected){
            try {
                socket = new Socket(ip, port);

                isConnected = socket.isConnected();

                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: Maybe sleep a while here
        }
        System.out.println("[Enhort Webinterface]: connected to backend");

    }


    public ResultCollector runAnalysis(backendCommand command){
        if(isConnected){
            try {

                System.out.println("[Enhort Webinterface]: writing command");
                outputStream.writeObject(command);

                System.out.println("[Enhort Webinterface]: getting result");
                ResultCollector collector = (ResultCollector) inputStream.readObject();


                System.out.println("[Enhort Webinterface]: got result: " + collector.getResults().size());
                return collector;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else{
            System.out.println("[Enhort Webinterface]: got result");
        }

        return null;
    }
}
