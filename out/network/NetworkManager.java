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