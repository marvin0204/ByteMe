package test;

import network.NetworkManager;
import network.Receiver;
import config.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NetworkTest {
    public static void main(String[] args) {
        System.out.println("🚀 Test startet...");

        ConfigManager config = new ConfigManager("src/main/resources/config.toml");

        // 1. Receiver starten
        Receiver receiver = new Receiver(config);
        new Thread(receiver).start();

        // 2. Kurze Pause, damit Receiver bereit ist
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        // 3. Textnachricht senden
        NetworkManager nm = new NetworkManager();
        int port = Integer.parseInt(config.get("port"));
        String ip = "127.0.0.1";
        String handle = "Sadik";

        System.out.println("💬 Sende Textnachricht...");
        nm.sendMsg(handle, "Hallo vom Test!", ip, port);

        // 3.5: Wartezeit für stabilen Empfang
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        // 4. Bild senden (mit Debug-Ausgabe)
        try {
            System.out.println("🧪 Versuche Bild zu laden...");
            byte[] img = Files.readAllBytes(Paths.get("testbild.jpg"));
            System.out.println("📏 Gelesene Bildgröße: " + img.length + " Bytes");

            System.out.println("🚀 Rufe sendImg auf...");
            nm.sendImg(handle, img, ip, port);
        } catch (IOException e) {
            System.err.println("❌ Fehler beim Lesen des Bildes: " + e.getMessage());
        }

        System.out.println("✅ Testlauf abgeschlossen.");
    }
}
// es fehlen noch code kommentare aber eventuell durchaus nicht durch die ganze zeit di