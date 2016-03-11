package de.thm.spring.backend;

import de.thm.spring.command.RunCommand;
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
    private Socket socket;
    private boolean isConnected = false;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final int port;
    private final String ip;
    private static BackendConnector instance = new BackendConnector(42412, "127.0.0.1");

    public static BackendConnector getInstance(){
        return instance;
    }

    private BackendConnector(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    @Override
    public void run() {
        while (!isConnected){
            try {
                socket = new Socket(ip, port);
                System.out.println("connected to backend");
                isConnected = true;

                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public ResultCollector runAnalysis(RunCommand command){
        if(isConnected){
            try {

                outputStream.writeObject(command);
                return (ResultCollector) inputStream.readObject();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else{
            System.err.println("Not connected to backend");
        }

        return null;
    }
}
