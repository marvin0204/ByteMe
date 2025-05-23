
package discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryService {
    private static final int WHOIS_PORT = 4000;

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(WHOIS_PORT, InetAddress.getByName("0.0.0.0"))) {
            byte[] buf = new byte[512];
            System.out.println("DiscoveryService gestartet. Warte auf WHOIS Anfragen...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Empfangen: " + received);
                // Weitere Verarbeitung folgt...
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
