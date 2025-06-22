package network;

import config.ConfigManager;

import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

/**
 * @class Receiver
 * @brief Lauscht auf eingehende UDP-Nachrichten und verarbeitet diese.
 *
 * Unterstützt den Empfang von Textnachrichten (MSG) sowie Bilddaten (IMG).
 */
public class Receiver implements Runnable {

    private final ConfigManager config;

    /**
     * @brief Konstruktor für den Receiver.
     * @param config Die Konfiguration, welche die Netzwerkparameter enthält.
     */
    public Receiver(ConfigManager config) {
        this.config = config;
    }

    /**
     * @brief Startet den Empfangs-Thread.
     */
    public void start() {
        run();
    }

    /**
     * @brief Führt den Empfangsprozess aus.
     *
     * Wartet auf Datagramme und verarbeitet je nach Typ die empfangenen Inhalte.
     */
    @Override
    public void run() {
        try {
            int port = Integer.parseInt(config.get("port"));
            DatagramSocket socket = new DatagramSocket(port);
            byte[] buffer = new byte[2048];

            System.out.println("[Receiver] Lausche auf Port " + port + "...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                System.out.println("[Receiver] Nachricht empfangen: " + message);

                if (message.startsWith("MSG")) {
                    String[] parts = message.split(" ", 3);
                    if (parts.length >= 3) {
                        String from = packet.getAddress().getHostAddress();
                        String sender = parts[1];
                        String text = parts[2];
                        System.out.println("[MSG] Von " + sender + " (" + from + "): " + text);
                    }
                } else if (message.startsWith("IMG")) {
                    String[] parts = message.split(" ");
                    if (parts.length >= 3) {
                        String sender = parts[1];
                        int size = Integer.parseInt(parts[2]);

                        System.out.println("[IMG] Bild von " + sender + " erwartet, Größe: " + size + " Bytes");

                        // Empfange Bilddaten
                        byte[] imageData = new byte[size];
                        DatagramPacket imagePacket = new DatagramPacket(imageData, size);
                        socket.receive(imagePacket);

                        String path = config.get("imagepath") + "/bild_von_" + sender + ".jpg";
                        try (FileOutputStream fos = new FileOutputStream(path)) {
                            fos.write(imageData);
                        }

                        System.out.println("[IMG] Bild gespeichert unter: " + path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
