
package config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private final String configPath;

    public ConfigManager(String configPath) {
        this.configPath = configPath;
    }

    public void printConfig() {
        try (InputStream input = new FileInputStream(configPath)) {
            Properties prop = new Properties();
            prop.load(input);

            System.out.println("--- Konfiguration ---");
            prop.forEach((k, v) -> System.out.println(k + ": " + v));
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Konfiguration: " + e.getMessage());
        }
    }
}
