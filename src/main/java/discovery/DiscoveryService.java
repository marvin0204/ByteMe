package discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @class DiscoveryService
 * @brief Verwaltet die Teilnehmer des Netzwerks und beantwortet Discovery-Anfragen.
 *
 * Der DiscoveryService ist ein zentraler Dienst, der JOIN- und WHO-Anfragen von Clients
 * verarbeitet und darauf basierend eine Teilnehmerliste pflegt. Er läuft als separater Prozess
 * und antwortet über UDP-Broadcast.
 */
public class DiscoveryService {

    /// Port auf dem der Discovery-Dienst lauscht (z. B. 4000)
    private static final int PORT = 4000;

    /// Interne Map zur Speicherung von Handle zu Adresse:Port
    private static final Map<String, String> participants = new HashMap<>();

    /**
     * @brief Einstiegspunkt des Discovery-Dienstes.
     * @param args Wird nicht verwendet.
     */
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("[INFO] Discovery-Dienst gestartet auf Port " + PORT);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                InetAddress senderAddress = packet.getAddress();
                int senderPort = packet.getPort();

                System.out.println("[RECV] " + msg + " von " + senderAddress + ":" + senderPort);

                if (msg.startsWith("JOIN")) {
                    String[] parts = msg.split(" ");
                    if (parts.length >= 3) {
                        String handle = parts[1];
                        String udpPort = parts[2];
                        String value = senderAddress.getHostAddress() + ":" + udpPort;
                        participants.put(handle, value);
                        System.out.println("[INFO] \"" + handle + "\" wurde zur Teilnehmerliste hinzugefügt.");
                    }
                } else if (msg.startsWith("WHO")) {
                    String[] parts = msg.split(" ");
                    if (parts.length >= 2) {
                        String searchHandle = parts[1];
                        if (participants.containsKey(searchHandle)) {
                            String info = participants.get(searchHandle);
                            String[] ipPort = info.split(":");
                            String response = "KNOWNUSER \"" + searchHandle + "\" " + ipPort[0] + ":" + ipPort[1];
                            byte[] responseBytes = response.getBytes();
                            DatagramPacket responsePacket = new DatagramPacket(
                                    responseBytes, responseBytes.length, senderAddress, senderPort);
                            socket.send(responsePacket);
                            System.out.println("[SEND] " + response);
                        } else {
                            System.out.println("[WHO] Handle \"" + searchHandle + "\" nicht gefunden.");
                        }
                    }
                } else if (msg.startsWith("LEAVE")) {
                    String[] parts = msg.split(" ");
                    if (parts.length >= 2) {
                        String handle = parts[1];
                        participants.remove(handle);
                        System.out.println("[INFO] \"" + handle + "\" hat den Chat verlassen.");
                    }
                } else {
                    System.out.println("[WARN] Unbekannter Befehl: " + msg);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
