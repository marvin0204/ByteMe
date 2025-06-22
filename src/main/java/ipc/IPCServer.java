package ipc;

import config.ConfigManager;
import network.NetworkManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @class IPCServer
 * @brief Dieser Server empfängt Steuerkommandos über IPC (lokale TCP-Verbindung) und leitet sie weiter an den NetworkManager.
 * 
 * Er nimmt JOIN-, LEAVE-, WHO-, MSG- und IMG-Kommandos entgegen und führt die jeweilige Netzwerklogik aus.
 */
public class IPCServer {

    /// Referenz zur geladenen Konfiguration
    private final ConfigManager config;

    /// Verwaltet alle ausgehenden Netzwerkaktionen (JOIN, LEAVE, WHO, MSG, IMG)
    private final NetworkManager networkManager;

    /**
     * Konstruktor.
     * 
     * @param config Die geladene Konfigurationsinstanz.
     * @param networkManager Der zentrale Netzwerkmanager.
     */
    public IPCServer(ConfigManager config, NetworkManager networkManager) {
        this.config = config;
        this.networkManager = networkManager;
    }

    /**
     * Startet den Server in einem neuen Thread, damit die Anwendung nicht blockiert.
     */
    public void start() {
        new Thread(this::run).start();
    }

    /**
     * Hauptlogik des IPC-Servers:
     * - Lauscht auf dem konfigurierten IPC-Port.
     * - Liest Kommandozeilen vom Client.
     * - Führt netzwerkbezogene Aktionen basierend auf den Kommandos aus.
     */
    private void run() {
        try {
            // Extrahiere den lokalen Port aus der Konfiguration
            int ipcPort = Integer.parseInt(config.get("ipcport"));
            ServerSocket serverSocket = new ServerSocket(ipcPort);
            System.out.println("[IPC] Server gestartet auf Port " + ipcPort);

            // Endlosschleife zum Verarbeiten eingehender IPC-Befehle
            while (true) {
                Socket clientSocket = serverSocket.accept();  ///< Verbindung mit lokalem Client
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String command = reader.readLine();           ///< Erste Zeile des IPC-Kommandos
                clientSocket.close();                         ///< Verbindung sofort wieder schließen

                if (command == null) continue;

                System.out.println("[IPC] Befehl empfangen: " + command);

                // === JOIN Kommando ===
                if (command.startsWith("JOIN")) {
                    networkManager.sendJoin();

                // === LEAVE Kommando ===
                } else if (command.startsWith("LEAVE")) {
                    networkManager.sendLeave();

                // === WHO Kommando ===
                } else if (command.startsWith("WHO ")) {
                    String[] parts = command.split(" ");
                    if (parts.length >= 2) {
                        String targetHandle = parts[1];
                        networkManager.sendWho(targetHandle);
                    }

                // === MSG Kommando ===
                } else if (command.startsWith("MSG ")) {
                    String[] parts = command.substring(4).split(" ", 2); // MSG <handle> <message;ip;port>
                    if (parts.length == 2) {
                        String[] msgParts = parts[1].split(";");
                        if (msgParts.length == 3) {
                            String toHandle = parts[0];
                            String message = msgParts[0];
                            String ip = msgParts[1];
                            int port = Integer.parseInt(msgParts[2]);

                            // Formatierte Nachricht an Empfänger
                            String finalMessage = "MSG " + config.get("handle") + " " + message + "\n";
                            networkManager.sendUnicast(finalMessage, ip, port);
                        }
                    }

                // === IMG Kommando ===
                } else if (command.startsWith("IMG ")) {
                    String[] parts = command.substring(4).split(" ", 2); // IMG <handle> <pfad;ip;port>
                    if (parts.length == 2) {
                        String toHandle = parts[0];
                        String[] imageParts = parts[1].split(";");
                        if (imageParts.length == 3) {
                            String imagePath = imageParts[0];
                            String ip = imageParts[1];
                            int port = Integer.parseInt(imageParts[2]);

                            File imageFile = new File(imagePath);
                            if (!imageFile.exists()) {
                                System.err.println("[IMG] Bilddatei nicht gefunden: " + imagePath);
                                continue;
                            }

                            // Lese das Bild in ein Byte-Array
                            byte[] imageBytes;
                            try (FileInputStream fis = new FileInputStream(imageFile)) {
                                imageBytes = fis.readAllBytes();
                            }

                            // Sende Header + Bilddaten separat
                            String header = "IMG " + config.get("handle") + " " + imageBytes.length + "\n";
                            networkManager.sendUnicast(header, ip, port);
                            networkManager.sendImageBytes(imageBytes, ip, port);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
