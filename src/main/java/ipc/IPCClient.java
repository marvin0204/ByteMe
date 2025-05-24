
package ipc;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class IPCClient {
    public void sendCommand(String command) {
        try (Socket socket = new Socket("localhost", 7777);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
            out.println(command);
        } catch (Exception e) {
            System.out.println("Fehler beim Senden über IPC: " + e.getMessage());
        }
    }
}
