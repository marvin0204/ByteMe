package config;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @class ConfigManager
 * @brief Verwaltet die Konfiguration des Clients, indem Werte aus einer Konfigurationsdatei gelesen werden.
 *
 * Unterstützt sowohl `.toml`- als auch `.properties`-Dateiformate.
 * Alle Konfigurationswerte werden intern in einer Map gespeichert.
 */
public class ConfigManager {

    /// Interne Konfigurationsmap (Key-Value-Paare)
    private final Map<String, String> config = new HashMap<>();

    /**
     * Konstruktor – Lädt die Konfigurationsdaten aus einer Datei.
     *
     * @param configPath Pfad zur Konfigurationsdatei (z. B. `resources/config.toml` oder `.properties`)
     */
    public ConfigManager(String configPath) {
        try {
            if (configPath.endsWith(".toml")) {
                // Lese TOML-Datei manuell zeilenweise ein
                Files.lines(Paths.get(configPath)).forEach(line -> {
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        String key = parts[0].trim();
                        String value = parts[1].trim().replaceAll("\"", "");
                        config.put(key, value);
                    }
                });
                System.out.println("[Config] Konfiguration geladen aus: " + configPath);
            } else {
                // Alte Properties-Datei lesen (Legacy-Support)
                System.out.println("[WARN] Alte config.properties geladen – verwende config.toml stattdessen!");
                Properties props = new Properties();
                props.load(new FileInputStream(configPath));
                for (String name : props.stringPropertyNames()) {
                    config.put(name, props.getProperty(name));
                }
            }
        } catch (Exception e) {
            System.out.println("[Config] Fehler beim Laden: " + configPath);
            e.printStackTrace();
        }
    }

    /**
     * Gibt einen Wert aus der Konfiguration zurück.
     *
     * @param key Der Name des Konfigurationsparameters (z. B. `handle`, `port`, etc.)
     * @return Der zugehörige Wert oder leerer String, wenn nicht gefunden.
     */
    public String get(String key) {
        return config.getOrDefault(key, "");
    }
}
