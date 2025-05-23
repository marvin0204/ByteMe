
package ui;

import config.ConfigManager;

import java.util.Scanner;

public class CLI {
    private final ConfigManager configManager;

    public CLI() {
        this.configManager = new ConfigManager("src/resources/config.toml");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println("\n--- BSRN Chat CLI ---");
            System.out.println("1. Konfiguration anzeigen");
            System.out.println("2. Beenden");
            System.out.print("Auswahl: ");
            input = scanner.nextLine();

            switch (input) {
                case "1":
                    configManager.printConfig();
                    break;
                case "2":
                    System.out.println("Beende Programm...");
                    return;
                default:
                    System.out.println("Ungültige Eingabe.");
            }
        }
    }
}
