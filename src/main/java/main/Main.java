package main;

import ui.CLI;
import config.ConfigManager;

public class Main {
    public static void main(String[] args) {
        ConfigManager config = new ConfigManager("src/main/resources/config.toml");
        CLI cli = new CLI(config);
        cli.start();
    }
}


