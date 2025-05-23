
package network;

import config.ConfigManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {
    private final ConfigManager config;

    public Receiver(ConfigManager config) {
        this.config = config;
    }

    @Override
    public void run() {
        int port = Integer.parseInt(config.get("port"));
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Empfangsserver läuft auf Port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = reader.readLine();
                if (message != null) {
                    System.out.println("Empfangen: " + message);
                }
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
