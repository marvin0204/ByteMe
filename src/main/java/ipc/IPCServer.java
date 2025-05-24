
package ipc;

import network.NetworkManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IPCServer implements Runnable {
    private final NetworkManager networkManager;

    public IPCServer(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(7777)) {
            System.out.println("IPC-Server lauscht auf Port 7777...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("IPC erhalten: " + inputLine);
                    String[] parts = inputLine.split(" ", 3);
                    switch (parts[0]) {
                        case "JOIN":
                            networkManager.sendJoin(parts[1], Integer.parseInt(parts[2]));
                            break;
                        case "LEAVE":
                            networkManager.sendLeave(parts[1]);
                            break;
                        case "MSG":
                            String handle = parts[1];
                            String[] msgParts = parts[2].split(";", 3);
                            String message = msgParts[0];
                            String ip = msgParts[1];
                            int port = Integer.parseInt(msgParts[2]);
                            networkManager.sendMsg(handle, message, ip, port);
                            break;
                        case "IMG":
                            String imgHandle = parts[1];
                            String[] imgParts = parts[2].split(";", 3);
                            String imagePath = imgParts[0];
                            String imgIp = imgParts[1];
                            int imgPort = Integer.parseInt(imgParts[2]);
                            byte[] imageData = Files.readAllBytes(Paths.get(imagePath));
                            networkManager.sendImg(imgHandle, imageData, imgIp, imgPort);
                            break;
                        default:
                            System.out.println("Unbekannter Befehl: " + inputLine);
                    }
                }
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
