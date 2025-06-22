package main;

import config.ConfigManager;
import ipc.IPCServer;
import network.NetworkManager;
import network.Receiver;
import ui.CLI;

/**
 * @class Main
 * @brief Einstiegspunkt des ByteMe-Clients.
 *
 * Initialisiert Konfiguration, Netzwerkkomponenten, IPC-Server und das CLI.
 */
public class Main {

    /**
     * @brief Startet den Client mit Konfiguration aus einer TOML-Datei.
     * @param args Pfad zur Konfigurationsdatei als Argument.
     *
     * Erwartet genau ein Argument: den Pfad zu einer gültigen TOML-Konfigurationsdatei.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Verwendung: java -cp out main.Main <pfad/zu/config.toml>");
            System.exit(1);
        }

        String configPath = args[0];
        ConfigManager config = new ConfigManager(configPath);

        System.out.println("[DEBUG] Handle: " + config.get("handle"));
        System.out.println("[DEBUG] UDP-Port: " + config.get("port"));
        System.out.println("[DEBUG] IPC-Port: " + config.get("ipcport"));

        // Initialisiere Netzwerkkomponenten
        NetworkManager networkManager = new NetworkManager(config);
        IPCServer ipcServer = new IPCServer(config, networkManager);
        Receiver receiver = new Receiver(config);

        // Starte IPC-Server in neuem Thread
        new Thread(ipcServer::start).start();

        // Starte Netzwerkempfänger in neuem Thread
        new Thread(receiver::start).start();

        // Starte Kommandozeileninterface (läuft im Hauptthread)
        CLI cli = new CLI(config);
        cli.start();
    }
}
