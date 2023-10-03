package edu.cooper.ece465.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server extends Thread {
    // has a control plane with its own thread processing, and another data plane and is corresponding thread
    // these threads can be then converted for more involved processing using thread groups
    protected String shutdownPassword = "seriously";
    protected int controlPlanePort;
    protected int dataPlanePort;
    protected ServerSocket serverSocketCPlane;
    public ServerSocket serverSocketDPlane;
    protected ServerAppCPlaneThread cplaneThread;
    public ServerAppDPlaneThread dplaneThread;

    ServerMonitorThread monitorThread;


    ArrayList<ServerAppCPlaneThread> serverThreadList;
    protected boolean listening = true;
    protected boolean shuttingDown = false;

    public Server() throws IOException {
        this(DefaultConfigurations.DEFAULT_CONTROL_PLANE_PORT, DefaultConfigurations.DEFAULT_DATA_PLANE_PORT);
    }

    public Server(int cport, int dport) throws IOException {
        System.out.println("Welcome to the server.");

        setControlPlanePort(cport);
        setDataPlanePort(dport);
        serverSocketCPlane = new ServerSocket(getControlPlanePort());
        serverSocketDPlane = new ServerSocket(getDataPlanePort());
        this.serverThreadList = new ArrayList<ServerAppCPlaneThread>();
    }

    protected synchronized void shutdown() {

    }
    public synchronized void addServerThreadToList(ServerAppCPlaneThread thread) {
        this.serverThreadList.add(thread);
    }
    protected synchronized void setControlPlanePort(int port) {
        this.controlPlanePort = port;
    }
    public synchronized int getControlPlanePort() {
        return this.controlPlanePort;
    }

    protected synchronized void setDataPlanePort(int port) {
        this.dataPlanePort = port;
    }

    public synchronized int getDataPlanePort() {
        return this.dataPlanePort;
    }

    public synchronized boolean pendingShutdown() {
        boolean result = false;
        for (ServerAppCPlaneThread thread : serverThreadList) {
            result = result || thread.isShuttingdown();
        }
        //System.out.println("Server shutdown flag = "+result);
        return(result);
    }

    public void close() {
        System.out.printf("Shutting down Server\n");
    }

    public void run() {
        try {
            monitorThread = new ServerMonitorThread(this);
            monitorThread.start();
            while (listening && !this.pendingShutdown()) {
                cplaneThread = new ServerAppCPlaneThread(this, this.serverSocketCPlane.accept());
                cplaneThread.start();
                this.addServerThreadToList(cplaneThread);
//                dplaneThread = new ServerAppDPlaneThread(this, this.serverSocketDPlane.accept());
//                dplaneThread.start();
            }
            monitorThread.join();
        } catch (IOException ex1) {
            ex1.printStackTrace(System.err);
            System.err.println("Experienced an IOException. Exiting Server program with error.");
        } catch (InterruptedException ex2) {
            ex2.printStackTrace(System.err);
            System.err.println("Experienced an InterruptedException. Exiting Server program with error.");
        } finally {

        }

    }

    public static void main(String[] args) {
        int exitCode = 0;
        int cport = DefaultConfigurations.DEFAULT_CONTROL_PLANE_PORT;
        int dport = DefaultConfigurations.DEFAULT_DATA_PLANE_PORT;

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

        try
        {
            Server serverInstance = new Server(cport, dport);
            serverInstance.start();
            serverInstance.join();
        } catch (IOException ex1) {
            exitCode = -2;
            ex1.printStackTrace(System.err);
            System.err.println("Experienced an IOException. Exiting Server program with error.");
        } catch (InterruptedException ex2) {
            exitCode = -3;
            ex2.printStackTrace(System.err);
            System.err.println("Experienced an InterruptedException. Exiting Server program with error.");
        } finally {
            System.out.println("Successfully exiting Server program.");
        }
        System.exit(exitCode);
    }
}
