
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
        try {
            InputStream in = socket.getInputStream();

            // Header manuell zeichenweise lesen bis \n
            ByteArrayOutputStream headerBuffer = new ByteArrayOutputStream();
            int c;
            while ((c = in.read()) != -1) {
                if (c == '\n') break;
                headerBuffer.write(c);
            }
            String header = headerBuffer.toString().trim();

            if (header == null || header.isEmpty()) return;

            String[] parts = header.split(" ", 3);
            String command = parts[0];

            switch (command) {
                case "MSG":
                    handleMessage(parts);
                    break;
                case "IMG":
                    handleImage(parts, in);
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

        if (Boolean.parseBoolean(config.get("autoreply_enabled"))) {
            String autoReply = config.get("autoreply");
            System.out.println("↩️  Auto-Antwort aktiviert: " + autoReply);
            // Optional: automatische Antwort implementierbar mit NetworkManager
        }
    }

    private void handleImage(String[] parts, InputStream in) {
        if (parts.length < 3) {
            System.out.println("⚠️ Ungültige IMG-Nachricht");
            return;
        }

        String from = parts[1];
        int size = Integer.parseInt(parts[2]);

        try {
            byte[] imageData = in.readNBytes(size);

            String imagePath = config.get("imagepath");
            String fileName = "image_from_" + from + "_" + System.currentTimeMillis() + ".jpg";

            Path path = Path.of(imagePath, fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, imageData);

            System.out.println("🖼️ Bild empfangen von " + from + " und gespeichert unter: " + path);

        } catch (IOException e) {
            System.err.println("❌ Fehler beim Speichern des Bildes: " + e.getMessage());
        }
    }
}
