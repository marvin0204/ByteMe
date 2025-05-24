
package main;

import config.ConfigManager;
import ui.CLI;

public class CliMain {
    public static void main(String[] args) {
        ConfigManager config = new ConfigManager("src/main/resources/config.toml");
        CLI cli = new CLI(config);
        cli.start();
    }
}
