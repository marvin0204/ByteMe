package network;

import config.ConfigManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Receiver implements Runnable {

    private final ConfigManager config;

    public Receiver(ConfigManager config) {
        this.config = config;
    }

    @Override
    public void run() {
        int port = Integer.parseInt(config.get("port"));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("📥 Receiver läuft auf Port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleConnection(clientSocket)).start();
            }

        } catch (IOException e) {
            System.err.println("❌ Fehler beim Starten des Receivers: " + e.getMessage());
        }
    }
