package ece465;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Utils {
    public static String generateUniqueName(Socket socket) throws IOException {
        // Get the remote address of the socket
//        String remoteAddress = socket.getRemoteSocketAddress().getHostName() + ":" + socket.getRemoteSocketAddress().getPort();
        StringBuilder sb = new StringBuilder();
        sb.append(socket.getInetAddress().getCanonicalHostName());
        sb.append("-");
        sb.append(socket.getInetAddress().getHostAddress());
        sb.append("-");
        sb.append(socket.getLocalPort());
        sb.append("-");
        sb.append(socket.getPort());

        // Generate a random number
//        int randomNumber = (int) (Math.random() * 1000000000);

        // Combine the remote address and the random number to create a unique name
//        String uniqueName = remoteAddress + ":" + randomNumber;

        // Return the unique name
        System.out.printf("%s\n", sb.toString());
        return(sb.toString());
    }

    public static String generateUniqueName(ServerSocket ssocket) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(ssocket.getInetAddress());
        sb.append("-");
        sb.append(ssocket.getLocalPort());
        sb.append("-");
        sb.append(ssocket.getLocalSocketAddress());

        System.out.printf("%s\n", sb.toString());
        return(sb.toString());
    }
}
