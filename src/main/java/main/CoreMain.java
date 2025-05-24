
package main;

import config.ConfigManager;
import discovery.DiscoveryService;
import network.NetworkManager;
import network.Receiver;
import ipc.IPCServer;

public class CoreMain {
    public static void main(String[] args) {
        ConfigManager config = new ConfigManager("src/main/resources/config.toml");
        NetworkManager networkManager = new NetworkManager();

        Thread discovery = new Thread(new DiscoveryService(config));
        Thread receiver = new Thread(new Receiver(config));
        Thread ipcServer = new Thread(new IPCServer(networkManager));

        discovery.start();
        receiver.start();
        ipcServer.start();

        System.out.println("ByteMe Core gestartet. Discovery, Receiver und IPC-Server laufen.");
    }
}
