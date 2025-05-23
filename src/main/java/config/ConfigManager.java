
package config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private final String configPath;
    private final Properties prop = new Properties();

    public ConfigManager(String configPath) {
        this.configPath = configPath;
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream input = new FileInputStream(configPath)) {
            prop.load(input);
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Konfiguration: " + e.getMessage());
        }
    }

    public void printConfig() {
        System.out.println("--- Konfiguration ---");
        prop.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    public String get(String key) {
        return prop.getProperty(key);
    }
}
