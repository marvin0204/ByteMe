package test;

import network.NetworkManager;
import network.Receiver;
import config.ConfigManager;


public class NetworkTest {
    public static void main(String[] args) {
        ConfigManager config = new ConfigManager("src/main/resources/config.toml");
    
        // 1. Starte den Receiver in einem eigenen Thread
        Receiver receiver = new Receiver(config);
        new Thread(receiver).start();
    
        // 2. Kurze Pause, damit Receiver startet
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    
        // 3. Nachricht senden
        NetworkManager nm = new NetworkManager();
        nm.sendMsg("Sadik", "Hallo vom Test!", "127.0.0.1", Integer.parseInt(config.get("port")));
    
        // 4. Optional: Bild senden
        /*
        try {
            byte[] img = Files.readAllBytes(Paths.get("testbild.jpg"));
            nm.sendImg("Sadik", img, "127.0.0.1", Integer.parseInt(config.get("port")));
        } catch (IOException e) {
            System.err.println("Fehler beim Bildlesen: " + e.getMessage());
        }
        */
    }
    
    }




    
    

