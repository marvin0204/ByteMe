package ui;

import config.ConfigManager;
import ipc.IPCClient;

import java.util.Scanner;

/**
 * @class CLI
 * @brief Konsoleninterface für den Benutzer zur Interaktion mit dem IPC-System.
 *
 * Bietet die Befehle:
 * - JOIN
 * - MSG
 * - IMG
 * - WHOIS
 * - LEAVE
 */
public class CLI {

    /// Konfiguration, z.B. Ports und Handle
    private final ConfigManager config;

    /**
     * Konstruktor.
     * @param config Konfiguration mit Handle und Ports.
     */
    public CLI(ConfigManager config) {
        this.config = config;
    }

    /**
     * Startet das Hauptmenü für den Benutzer.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            // Menü anzeigen
            System.out.println("\n--- ByteMe Chat CLI ---");
            System.out.println("1. JOIN senden");
            System.out.println("2. MSG senden");
            System.out.println("3. IMG senden");
            System.out.println("4. LEAVE senden");
            System.out.println("5. WHOIS suchen");
            System.out.println("6. Beenden");
            System.out.print("Auswahl: ");
            input = scanner.nextLine().trim();

            // Befehl interpretieren
            switch (input) {
                case "1":
                    new IPCClient(config).sendCommand("JOIN " + config.get("handle") + " " + config.get("port"));
                    break;

                case "2":
                    System.out.print("Empfänger Handle: ");
                    String toHandle = scanner.nextLine().trim();

                    System.out.print("Nachricht: ");
                    String message = scanner.nextLine().trim();

                    System.out.print("Empfänger IP: ");
                    String ip = scanner.nextLine().trim();
                    if (ip.isEmpty()) {
                        System.out.println("⚠️ IP darf nicht leer sein!");
                        break;
                    }

                    System.out.print("Empfänger Port: ");
                    String port = scanner.nextLine().trim();

                    new IPCClient(config).sendCommand("MSG " + toHandle + " " + message + ";" + ip + ";" + port);
                    break;

                case "3":
                    System.out.print("Empfänger Handle: ");
                    String targetHandle = scanner.nextLine().trim();

                    System.out.print("Pfad zur Bilddatei: ");
                    String imagePath = scanner.nextLine().trim();

                    System.out.print("Empfänger IP: ");
                    String imgIp = scanner.nextLine().trim();
                    if (imgIp.isEmpty()) {
                        System.out.println("⚠️ IP darf nicht leer sein!");
                        break;
                    }

                    System.out.print("Empfänger Port: ");
                    String imgPort = scanner.nextLine().trim();

                    new IPCClient(config).sendCommand("IMG " + targetHandle + " " + imagePath + ";" + imgIp + ";" + imgPort);
                    break;

                case "4":
                    new IPCClient(config).sendCommand("LEAVE " + config.get("handle"));
                    break;

                case "5":
                    System.out.print("Handle zum Suchen (WHOIS): ");
                    String handle = scanner.nextLine().trim();
                    new IPCClient(config).sendCommand("WHO " + handle);
                    break;

                case "6":
                    System.out.println("Beenden...");
                    return;

                default:
                    System.out.println("Ungültige Eingabe.");
            }
        }
    }
}


