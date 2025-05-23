
package ui;

import config.ConfigManager;
import network.NetworkManager;

import java.util.Scanner;

public class CLI {
    private final ConfigManager configManager;
    private final NetworkManager networkManager;

    public CLI() {
        this.configManager = new ConfigManager("src/resources/config.toml");
        this.networkManager = new NetworkManager();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println("\n--- ByteMe Chat CLI ---");
            System.out.println("1. Konfiguration anzeigen");
            System.out.println("2. JOIN senden");
            System.out.println("3. MSG senden");
            System.out.println("4. Beenden");
            System.out.print("Auswahl: ");
            input = scanner.nextLine();

            switch (input) {
                case "1":
                    configManager.printConfig();
                    break;
                case "2":
                    String handle = configManager.get("handle");
                    int port = Integer.parseInt(configManager.get("port"));
                    networkManager.sendJoin(handle, port);
                    break;
                case "3":
                    System.out.print("Empfänger Handle: ");
                    String toHandle = scanner.nextLine();
                    System.out.print("Nachricht: ");
                    String msg = scanner.nextLine();
                    System.out.print("Empfänger IP: ");
                    String ip = scanner.nextLine();
                    System.out.print("Empfänger Port: ");
                    int toPort = Integer.parseInt(scanner.nextLine());
                    networkManager.sendMsg(toHandle, msg, ip, toPort);
                    break;
                case "4":
                    System.out.println("Beende ByteMe...");
                    return;
                default:
                    System.out.println("Ungültige Eingabe.");
            }
        }
    }
}
