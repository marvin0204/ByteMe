package ipc;

import config.ConfigManager;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @class IPCClient
 * @brief Sendet Steuerbefehle (JOIN, MSG, IMG, WHO, LEAVE) an den lokalen IPCServer über eine TCP-Verbindung.
 */
public class IPCClient {

    /// Die geladene Konfiguration, z.B. ipcport, handle etc.
    private final ConfigManager config;

    /**
     * Konstruktor des IPCClients.
     * @param config Die Konfigurationsinstanz mit u.a. IPC-Port.
     */
    public IPCClient(ConfigManager config) {
        this.config = config;
    }

    /**
     * Sendet einen Befehl (JOIN, MSG, WHO, IMG, LEAVE) über eine lokale TCP-Verbindung an den IPCServer.
     *
     * @param command Der zu übertragende Befehl als String.
     */
    public void sendCommand(String command) {
        try {
            // Lese den Ziel-Port aus der Konfiguration
            int ipcPort = Integer.parseInt(config.get("ipcport"));

            // Verbinde zum lokalen Server
            Socket socket = new Socket("127.0.0.1", ipcPort);

            // Sende den Textbefehl als erste Zeile
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            writer.println(command);

            socket.close(); // Verbindung wieder schließen
        } catch (Exception e) {
            System.err.println("[IPCClient] Fehler beim Senden über IPC: " + e.getMessage());
        }
    }
}
