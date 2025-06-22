package network;

import config.ConfigManager;

import java.net.*;

/**
 * @class NetworkManager
 * @brief Diese Klasse verwaltet alle Netzwerkoperationen: Broadcast, Unicast und Bildübertragung.
 */
public class NetworkManager {

    /// Konfigurationsdaten (z. B. Handle, Ports etc.)
    private final ConfigManager config;

    /**
     * Konstruktor.
     * @param config Konfiguration mit Netzwerkparametern.
     */
    public NetworkManager(ConfigManager config) {
        this.config = config;
    }

    /**
     * Sendet eine JOIN-Nachricht via Broadcast, um dem Discovery-Service den neuen Nutzer mitzuteilen.
     */
    public void sendJoin() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String handle = config.get("handle");
            String port = config.get("port");
            String joinCmd = "JOIN " + handle + " " + port + "\n";

            byte[] buffer = joinCmd.getBytes();
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            int whoisPort = Integer.parseInt(config.get("whoisport"));

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, whoisPort);
            socket.send(packet);

            System.out.println("[Broadcast] " + joinCmd.trim());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sendet eine LEAVE-Nachricht via Broadcast.
     */
    public void sendLeave() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String handle = config.get("handle");
            String leaveCmd = "LEAVE " + handle + "\n";

            byte[] buffer = leaveCmd.getBytes();
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            int whoisPort = Integer.parseInt(config.get("whoisport"));

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, whoisPort);
            socket.send(packet);

            System.out.println("[Broadcast] " + leaveCmd.trim());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sendet eine WHO-Anfrage an den Discovery-Dienst und wartet auf Antwort.
     *
     * @param targetHandle Handle der gesuchten Person
     */
    public void sendWho(String targetHandle) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String whoCmd = "WHO " + targetHandle + "\n";
            byte[] buffer = whoCmd.getBytes();

            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            int whoisPort = Integer.parseInt(config.get("whoisport"));

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, whoisPort);
            socket.send(packet);

            System.out.println("[Broadcast] " + whoCmd.trim());

            // Warte auf Antwort
            socket.setSoTimeout(3000);
            byte[] recvBuffer = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);

            socket.receive(recvPacket);
            String response = new String(recvPacket.getData(), 0, recvPacket.getLength()).trim();

            System.out.println("[Antwort vom Discovery-Service]: " + response);
            socket.close();
        } catch (SocketTimeoutException e) {
            System.out.println("Keine Antwort auf WHO oder Fehler: Receive timed out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sendet eine Nachricht über Unicast an einen spezifischen Empfänger.
     *
     * @param message Die zu sendende Nachricht
     * @param ip Ziel-IP-Adresse
     * @param port Ziel-Port
     */
    public void sendUnicast(String message, String ip, int port) {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName(ip);

            System.out.println("→ Sende an IP: " + ip + ", Port: " + port + ", Nachricht: " + message);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
            System.out.println("[Unicast] " + message.trim() + " → " + ip + ":" + port);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sendet ein Bild (als Byte-Array) an einen Empfänger via UDP.
     *
     * @param imageData Byte-Array der Bilddaten
     * @param ip IP-Adresse des Empfängers
     * @param port Zielport
     */
    public void sendImageBytes(byte[] imageData, String ip, int port) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(ip);

            DatagramPacket imagePacket = new DatagramPacket(imageData, imageData.length, address, port);
            socket.send(imagePacket);

            System.out.println("[Bilddaten gesendet] → " + ip + ":" + port + " (" + imageData.length + " Bytes)");
            socket.close();
        } catch (Exception e) {
            System.err.println("Fehler beim Senden der Bilddaten: " + e.getMessage());
        }
    }
}
