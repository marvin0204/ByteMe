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

    private void handleConnection(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                InputStream rawIn = socket.getInputStream()
        ) {
            String header = reader.readLine();

            if (header == null) return;

            String[] parts = header.split(" ", 3);
            String command = parts[0];

            switch (command) {
                case "MSG":
                    handleMessage(parts);
                    break;
                case "IMG":
                    handleImage(parts, rawIn);
                    break;
                default:
                    System.out.println("⚠️ Unbekannter Befehl: " + header);
            }

        } catch (IOException e) {
            System.err.println("❌ Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void handleMessage(String[] parts) {
        if (parts.length < 3) {
            System.out.println("⚠️ Ungültige MSG-Nachricht");
            return;
        }

        String from = parts[1];
        String text = parts[2].replace("\"", "");
        System.out.println("💬 Nachricht von " + from + ": " + text);

        // Optional: Auto-Reply
        if (Boolean.parseBoolean(config.get("autoreply_enabled"))) {
            String autoReply = config.get("autoreply");
            System.out.println("↩️  Auto-Antwort aktiviert: " + autoReply);
            // Du könntest hier `NetworkManager.sendMsg()` aufrufen (falls verfügbar).
        }
    }