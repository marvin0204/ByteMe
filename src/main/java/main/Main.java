
package main;

import config.ConfigManager;
import discovery.DiscoveryService;
import network.NetworkManager;
import network.Receiver;
import ipc.IPCServer;
import ui.CLI;

public class Main {
    public static void main(String[] args) {
        ConfigManager config = new ConfigManager("src/main/resources/config.toml");
        NetworkManager networkManager = new NetworkManager();

        Thread discovery = new Thread(new DiscoveryService(config));
        Thread receiver = new Thread(new Receiver(config));
        Thread ipcServer = new Thread(new IPCServer(networkManager));

        discovery.start();
        receiver.start();
        ipcServer.start();

        CLI cli = new CLI(config);
        cli.start();
    }
}
