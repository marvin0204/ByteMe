
package network;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkManager {
    public void sendJoin(String handle, int port) {
        String message = "JOIN " + handle + " " + port + "\n";
        broadcastMessage(message);
    }

    public void sendMsg(String handle, String text, String ip, int port) {
        String message = "MSG " + handle + " " + text + "\n";
        try (Socket socket = new Socket(ip, port);
             OutputStream out = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(out, true)) {
            writer.println(message);
            System.out.println("Nachricht gesendet an " + handle);
        } catch (Exception e) {
            System.out.println("Fehler beim Senden der Nachricht: " + e.getMessage());
        }
    }

    private void broadcastMessage(String message) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length,
                InetAddress.getByName("255.255.255.255"), 4000
            );
            socket.send(packet);
            socket.close();
            System.out.println("Broadcast gesendet: " + message.trim());
        } catch (Exception e) {
            System.out.println("Broadcast fehlgeschlagen: " + e.getMessage());
        }
    }
}
