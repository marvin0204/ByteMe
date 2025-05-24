package ui;

import config.ConfigManager;
import ipc.IPCClient;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class CLI {
    private final ConfigManager configManager;
    private final IPCClient ipcClient;

    public CLI(ConfigManager configManager) {
        this.configManager = configManager;
        this.ipcClient = new IPCClient();
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n--- ByteMe Chat CLI ---");
                System.out.println("1. JOIN senden");
                System.out.println("2. MSG senden");
                System.out.println("3. IMG senden");
                System.out.println("4. LEAVE senden");
                System.out.println("5. WHOIS suchen");
                System.out.println("6. Beenden");
                System.out.print("Auswahl: ");
                String input = scanner.nextLine();

                switch (input) {
                    case "1":
                        String handle = configManager.get("handle");
                        String port = configManager.get("port");
                        ipcClient.sendCommand("JOIN " + handle + " " + port);
                        break;
                    case "2":
                        System.out.print("Empfänger Handle: ");
                        String toHandle = scanner.nextLine();
                        System.out.print("Nachricht: ");
                        String msg = scanner.nextLine();
                        System.out.print("Empfänger IP: ");
                        String ip = scanner.nextLine();
                        System.out.print("Empfänger Port: ");
                        String toPort = scanner.nextLine();
                        ipcClient.sendCommand("MSG " + toHandle + " " + msg + ";" + ip + ";" + toPort);
                        break;
                    case "3":
                        System.out.print("Empfänger Handle: ");
                        String imgHandle = scanner.nextLine();
                        System.out.print("Pfad zum Bild: ");
                        String path = scanner.nextLine();
                        System.out.print("Empfänger IP: ");
                        String imgIp = scanner.nextLine();
                        System.out.print("Empfänger Port: ");
                        String imgPort = scanner.nextLine();
                        ipcClient.sendCommand("IMG " + imgHandle + " " + path + ";" + imgIp + ";" + imgPort);
                        break;
                    case "4":
                        String leaveHandle = configManager.get("handle");
                        ipcClient.sendCommand("LEAVE " + leaveHandle);
                        break;
                    case "5":
                        System.out.print("Handle zum Suchen (WHOIS): ");
                        String target = scanner.nextLine();
                        sendWhois(target);
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println("Ungültige Eingabe.");
                }
            }
        }
    }

    private void sendWhois(String handle) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(3000);  // 3 Sekunden auf Antwort warten
            String whoisMessage = "WHOIS " + handle + "\n";
            byte[] buffer = whoisMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length,
                InetAddress.getByName("255.255.255.255"), 4000
            );
            socket.setBroadcast(true);
            socket.send(packet);
            System.out.println("WHOIS gesendet: " + handle);

            byte[] recvBuf = new byte[512];
            DatagramPacket response = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(response);
            String iam = new String(response.getData(), 0, response.getLength());
            System.out.println("Antwort erhalten: " + iam);
            socket.close();
        } catch (Exception e) {
            System.out.println("Keine Antwort auf WHOIS oder Fehler: " + e.getMessage());
        }
    }
}

