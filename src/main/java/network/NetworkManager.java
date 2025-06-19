package network;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NetworkManager {

    private final int BROADCAST_PORT = 4000;

    public void sendJoin(String handle, int port) {
        String message = "JOIN " + handle + " " + port + "\n";
        broadcastMessage(message, "JOIN");
    }

    public void sendLeave(String handle) {
        String message = "LEAVE " + handle + "\n";
        broadcastMessage(message, "LEAVE");
    }

    public void sendMsg(String handle, String messageText, String ip, int port) {
        String message = "MSG " + handle + " \"" + messageText + "\"\n";
        try (Socket socket = new Socket(ip, port);
             OutputStream out = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(out, true)) {

            writer.println(message);
            System.out.println("✅ Textnachricht an " + handle + " gesendet: " + messageText);

        } catch (Exception e) {
            System.out.println("❌ Fehler beim Senden der Nachricht: " + e.getMessage());
        }
    }

    public void sendImg(String handle, byte[] imageData, String ip, int port) {
        if (imageData == null || imageData.length == 0) {
            System.err.println("❌ Bilddaten sind leer oder null. Versand abgebrochen.");
            return;
        }

        String header = "IMG " + handle + " " + imageData.length;
        System.out.println("📤 Starte Bildversand an " + handle);
        System.out.println("📦 Bildgröße: " + imageData.length + " Bytes");
        System.out.println("➡ Ziel: " + ip + ":" + port);

        try (Socket socket = new Socket(ip, port);
             OutputStream out = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(out, true)) {

            writer.println(header);  // wichtig: mit \n
            writer.flush();         // explizit spülen, um sicherzustellen, dass der Header sofort rausgeht

            out.write(imageData);   // Bilddaten direkt im Anschluss
            out.flush();

            System.out.println("✅ Bild erfolgreich an " + handle + " gesendet");

        } catch (Exception e) {
            System.err.println("❌ Fehler beim Senden des Bildes: " + e.getMessage());
        }
    }

    private void broadcastMessage(String message, String type) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(
                    buffer, buffer.length,
                    InetAddress.getByName("255.255.255.255"), BROADCAST_PORT
            );
            socket.send(packet);
            System.out.println("📡 Broadcast (" + type + ") gesendet: " + message.trim());
        } catch (Exception e) {
            System.err.println("❌ Fehler beim Broadcast (" + type + "): " + e.getMessage());
        }
    }
}
