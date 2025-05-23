
package discovery;

import config.ConfigManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryService implements Runnable {
    private static final int WHOIS_PORT = 4000;
    private final ConfigManager config;

    public DiscoveryService(ConfigManager config) {
        this.config = config;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(WHOIS_PORT, InetAddress.getByName("0.0.0.0"))) {
            byte[] buf = new byte[512];
            System.out.println("DiscoveryService gestartet. Warte auf WHOIS Anfragen...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if (received.startsWith("WHOIS")) {
                    String[] parts = received.split(" ");
                    if (parts.length >= 2 && parts[1].trim().equals(config.get("handle"))) {
                        String response = "IAM " + config.get("handle") + " 127.0.0.1 " + config.get("port") + "\n";
                        byte[] responseData = response.getBytes();
                        DatagramPacket responsePacket = new DatagramPacket(
                                responseData, responseData.length,
                                packet.getAddress(), packet.getPort());
                        socket.send(responsePacket);
                        System.out.println("IAM Antwort gesendet.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
