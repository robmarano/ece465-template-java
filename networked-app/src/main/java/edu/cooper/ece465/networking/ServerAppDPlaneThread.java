package edu.cooper.ece465.networking;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerAppDPlaneThread extends Thread {
    protected Server server = null;
    protected Socket socket = null;
    public ServerAppDPlaneThread(Server server, Socket socket) {
        super("ServerAppDPlaneThread");
        System.out.printf("Created a ServerAppDPlaneThread to handle socket %s\n", socket.toString());
        this.server = server;
        this.socket = socket;
    }

    protected void receiveFile(DataInputStream in) throws IOException {
//        // see https://www.baeldung.com/java-inputstream-server-socket
//        char dataType = in.readChar();
//        int length = in.readInt();
//        if (dataType == 's') {
//            byte[] messageByte = new byte[length];
//            boolean end = false;
//            StringBuilder dataString = new StringBuilder(length);
//            int totalBytesRead = 0;
//            while(!end) {
//                int currentBytesRead = in.read(messageByte);
//                totalBytesRead = currentBytesRead + totalBytesRead;
//                if(totalBytesRead <= length) {
//                    dataString
//                            .append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
//                } else {
//                    dataString
//                            .append(new String(messageByte, 0, length - totalBytesRead + currentBytesRead,
//                                    StandardCharsets.UTF_8));
//                }
//                if(dataString.length()>=length) {
//                    end = true;
//                }
//            }
//        }
        System.out.println("Downloading file from client" + socket.toString());
        OutputStream outFile = new FileOutputStream("ttt.txt");
        byte[] bytes = new byte[16*1024];

        int count;
        while ((count = in.read(bytes)) > 0) {
            outFile.write(bytes, 0, count);
        }
        outFile.close();

    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
//            String inputLine, outputLine;
//            ProtocolControlPlane cproto = new ProtocolControlPlane();
//            outputLine = cproto.processInput(null);
//            out.println(outputLine);
            while (!Thread.currentThread().isInterrupted()) {
//                while ((inputLine = in.readLine()) != null) {
//                    outputLine = cproto.processInput(inputLine);
//                    out.println(outputLine);
//                    if (outputLine.equals("BYE"))
//                        break;
//                }
                receiveFile(in);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
