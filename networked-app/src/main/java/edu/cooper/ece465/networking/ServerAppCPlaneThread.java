package edu.cooper.ece465.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerAppCPlaneThread extends Thread {
    protected Server server = null;
    protected Socket socket = null;
    protected boolean shuttingDown = false;
    public ServerAppCPlaneThread(Server server, Socket socket) {
        super("ServerAppCPlaneThread");
        System.out.printf("Created a ServerAppCPlaneThread to handle socket %s\n", socket.toString());
        this.server = server;
        this.socket = socket;
    }

    synchronized public boolean isShuttingdown() {
        return(this.shuttingDown);
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            ProtocolControlPlane cproto = new ProtocolControlPlane();
//            outputLine = cproto.processInput(null);
//            out.println(outputLine);
            while (!Thread.currentThread().isInterrupted()) {
                while ((inputLine = in.readLine()) != null) {
                    outputLine = cproto.processInput(inputLine);
                    out.println(outputLine);
                    if (outputLine.equals("BYE"))
                        break;
                    else if (outputLine.toUpperCase().equals("SHUTTING DOWN"))
                        this.shuttingDown = true;
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
