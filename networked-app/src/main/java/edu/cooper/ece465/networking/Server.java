package edu.cooper.ece465.networking;

import java.io.IOException;
import java.net.ServerSocket;

public class Server implements AutoCloseable {
    // has a control plane with its own thread processing, and another data plane and is corresponding thread
    // these threads can be then converted for more involved processing using thread groups
    public static int DEFAULT_CONTROL_PLANE_PORT = 1971;
    public static int DEFAULT_DATA_PLANE_PORT = 1972;
    protected int controlPlanePort;
    protected int dataPlanePort;
    protected ServerSocket serverSocketControlPlane;
    protected ServerSocket serverSocketDataPlane;

    public Server() {
        this(DEFAULT_CONTROL_PLANE_PORT, DEFAULT_DATA_PLANE_PORT);
    }

    public Server(int cport, int dport) throws IOException {
        setControlPlanePort(cport);
        setDataPlanePort(dport);
        serverSocketControlPlane = new ServerSocket(getControlPlanePort());
        serverSocketDataPlane = new ServerSocket(getDataPlanePort());
    }

    protected void setControlPlanePort(int port) {
        this.controlPlanePort = port;
    }
    public int getControlPlanePort() {
        return this.controlPlanePort;
    }

    protected void setDataPlanePort(int port) {
        this.dataPlanePort = port;
    }

    public int getDataPlanePort() {
        return this.dataPlanePort;
    }

    public void close() {

    }
    public static void main(String[] args) {
        int exitCode = 0;
        int cport = Server.DEFAULT_CONTROL_PLANE_PORT;
        int dport = Server.DEFAULT_DATA_PLANE_PORT;

        if (args.length != 2) {
            System.out.println("Usage: java Server <control port number> <data port number>");
            System.out.println("You did not provide the port numbers for Server's control nor data planes.");
            System.out.printf("Using default values for control (%d) and for data (%d).\n", cport, dport);
        } else {
            try {
                cport = Integer.parseInt(args[0]);
                dport = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                exitCode = -1;
                ex.printStackTrace(System.err);
                System.err.println("You did not provide an acceptable port number. Check your characters.");
                System.exit(exitCode);
            }
        }
        ;
        System.out.println("Welcome to the server.");
        boolean listening = true;

        try (Server serverInstance = new Server(cport, dport)) {
            //
        } catch (IOException ex) {
            exitCode = -2;
            ex.printStackTrace(System.err);
            System.err.println("Experienced an IOException. Exiting Server program with error.");
            System.exit(exitCode);
        }
        System.out.println("Successfully exiting Server program.");
        System.exit(exitCode);
    }
}
