package ece465;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Server extends Protocol implements Runnable {

    MonitorServer monitorServer;
    Thread monitorServerThread;
    protected ServerSocket cPlaneSsocket;
    protected ServerSocket dPlaneSsocket;

    protected BlockingQueue<String> queue;

    protected List<cPlaneRunnable> serverThreadList;
    protected boolean listening;
    public Server() {
        super();
        this.serverThreadList = Collections.synchronizedList(new ArrayList<cPlaneRunnable>());
        this.monitorServer = new MonitorServer(this);
        this.queue = new LinkedBlockingDeque<String>();
        System.out.println(Protocol.SERVER_HEADER);
    }

    public Server(int cport, int dport) throws IOException {
        this();
        super.setControlPort(cport);
        super.setDataPort(dport);
        this.cPlaneSsocket = new ServerSocket(this.getControlPort());
        this.dPlaneSsocket = new ServerSocket(this.getDataPort());
    }

    public void putToQ(String s) {
        System.out.println(s);
        try {
            this.queue.put(s);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            System.err.println("Could not put() " + s);
        }
    }

    @Override
    public String getServerHostname() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            super.setServerHostname(addr.getHostName());
        } catch (UnknownHostException ex) {
            ex.printStackTrace(System.err);
        }
        return (super.getServerHostname());
    }

    /**
     * method to add a control plane runnable from Server instance's record
     * <p>
     * @param  cpRunnable  running cPlaneRunnable that is now being served by Server
     * @return      boolean if successfully added
     * @see         cPlaneRunnable
     */
    synchronized protected boolean addControlRunnable(cPlaneRunnable cpRunnable) {
        boolean added = false;
        System.out.printf("\tADDING (%b) cPlaneRunnable = %s\n", added, cpRunnable.toString());
        added = this.serverThreadList.add(cpRunnable);
        System.out.printf("\t\tADDING (%b) cPlaneRunnable = %s\n", added, cpRunnable.toString());
        return(added);
    }

    /**
     * method to remove a control plane runnable from Server instance's record
     * <p>
     * @param  cpRunnable  running cPlaneRunnable that is now considered completed by Server
     * @return      boolean if successfully removed
     * @see         cPlaneRunnable
     */
    synchronized protected boolean removeControlRunnable(cPlaneRunnable cpRunnable) {
        boolean removed = false;
        System.out.printf("\tREMOVING cPlaneRunnable = %s\n",cpRunnable.toString());
        removed = this.serverThreadList.remove(cpRunnable);
        System.out.printf("\t\tREMOVED cPlaneRunnable = %s\n",cpRunnable.toString());
        return(removed);
    }

    /**
     * method to return if Server instance is listening on the control plane
     * <p>
     * @return      boolean true if listening; Server can change this state
     * @see         Server
     */
    public boolean isListening() {
        return this.listening;
    }

    protected void setListening(boolean l) {
        this.listening = l;
    }

    /**
     * method to process the commands received from Client
     * <p>
     * @param  instruction  a dictionary of commands for each side of communication
     * @param  cout output stream over socket to communicate to Client
     * @return      set through processing the STATE of the Server instance connection to Client
     * @see         Server
     * @see         Client
     */
    protected void executeInstruction(Map<String, String> instruction, PrintWriter cout) {
        System.out.println("executeInstruction( " + instruction.toString() + " " + cout.toString());
        System.out.println(instruction.toString());
        this.printState();
        String myAction = instruction.get("my-action");
        String peerAction = instruction.get("peer-action");
        String object = instruction.get("object");
        String textSendToClient = null;

        switch(myAction) {
            case "VERSION":
                this.printState();
                textSendToClient = String.format("%s %s",peerAction, object); // version set in Protocol
                System.out.printf("executeInstruction() -> Client: %s\n", textSendToClient);
                cout.println(textSendToClient);
                cout.flush();
                this.setCurrentState(STATE.READY);
                this.printState();
                break;
            case "LIST":
                this.printState();
                textSendToClient = String.format("%s %s",peerAction, object); // commands set in Protocol
                System.out.printf("executeInstruction() -> Client: %s\n", textSendToClient);
                cout.println(textSendToClient);
                cout.flush();
                this.setCurrentState(STATE.READY);
                this.printState();
                break;
            case "COMMANDS":
                this.printState();
                textSendToClient = String.format("%s %s",peerAction, object); // commands set in Protocol
                System.out.printf("executeInstruction() -> Client: %s\n", textSendToClient);
                cout.println(textSendToClient);
                cout.flush();
                this.setCurrentState(STATE.READY);
                this.printState();
                break;
            case "SHUTDOWN":
                this.printState();
                textSendToClient = String.format("%s %s",peerAction, object); // Shutdown
                System.out.printf("executeInstruction() -> Client: %s\n", textSendToClient);
                cout.println(textSendToClient);
                cout.flush();
                this.setCurrentState(STATE.EXITING);
                this.printState();
                break;
            case "HELO": //HELO
                this.printState();
                this.setCurrentState(STATE.READY);
                this.printState();
                System.out.println("executeInstruction() HELO");
                cout.println("HELO");
                cout.flush();
                break;
            case "DISCONNECT": // DISCONNECT
                this.printState();
                this.setCurrentState(STATE.DISCONNECTING);
                textSendToClient = String.format("%s %s",peerAction, object); // commands set in Protocol
                System.out.printf("executeInstruction() -> Client: %s\n", textSendToClient);
                cout.println(textSendToClient);
                cout.flush();
                this.printState();
                break;
            case "RECEIVE": // RECEIVE object (is filename)
                this.printState();
                this.setCurrentState(STATE.RECEIVING);
                this.printState();
                System.out.println("executeInstruction() RECEIVE");
                textSendToClient = String.format("%s %s",peerAction, object); // RECEIVE
                cout.println(textSendToClient);
                cout.flush();
                System.out.printf("executeInstruction() -> Client: %s\n", textSendToClient);
                try (
                    // input is the data plane
                    Socket dSocket = dPlaneSsocket.accept();
                    InputStream in = dSocket.getInputStream();
                ) {
                    System.out.printf("\tdSocket = %s\n", dSocket.toString());
                    System.out.printf("\tin = %s\n", in.toString());
                    this.printState();
                    this.setCurrentState(STATE.RECEIVING);
                    System.out.println("executeInstruction() receiving file " + object);
                    Protocol.receiveFile(object, in);
                    this.putToQ(object); // TODO add the filename to a list with fully qualified name
                    System.out.println("executeInstruction() finished receiving file " + object);
                } catch (FileNotFoundException ex) {
                    System.err.println("caught FileNotFoundException in executeInstruction()");
                    ex.printStackTrace(System.err);
                } catch (IOException ex) {
                    System.err.println("caught IOException in executeInstruction()");
                    ex.printStackTrace(System.err);
                }
                this.setCurrentState(STATE.READY);
                this.printState();
                break;
            case "SEND": // SEND object (is filename)
                this.printState();
                this.setCurrentState(STATE.SENDING);
                this.printState();
                System.out.println("executeInstruction() SENDING");
                textSendToClient = String.format("%s %s",peerAction, object); // RECEIVE
                cout.println(textSendToClient);
                cout.flush();
                System.out.printf("executeInstruction() -> Client: %s\n", textSendToClient);
                try (
                    Socket dSocket = dPlaneSsocket.accept();
                    OutputStream out = dSocket.getOutputStream();
                ) {
                    System.out.printf("\tdSocket = %s\n", dSocket.toString());
                    System.out.printf("\tout = %s\n", out.toString());
                    this.printState();
                    this.setCurrentState(STATE.SENDING);
                    System.out.println("executeInstruction() sending file " + object);
                    Protocol.sendFile(object, out);
                    System.out.println("executeInstruction() finished sending file " + object);
                } catch (FileNotFoundException ex) {
                    System.err.println("caught FileNotFoundException");
                    ex.printStackTrace(System.err);
                } catch (SecurityException ex) {
                    System.err.println("caught SecurityException");
                    ex.printStackTrace(System.err);
                } catch (IOException ex) {
                    System.err.println("caught IOException");
                    ex.printStackTrace(System.err);
                }
                this.setCurrentState(STATE.READY);
                this.printState();
                break;
            default:
                System.out.println("executeInstruction() NOOP");
                this.setCurrentState(STATE.READY);
                cout.flush();
                break;
        }
    }

    /**
     * method to start and run the actual thread for the Server that a Client thread connects to over network
     * <p>
     * @see         Server
     * @see         Client
     */
    @Override
    public void run() {
        this.setListening(true);
        try {
            System.out.println("Starting Monitor Thread ...");
            monitorServerThread = new Thread(monitorServer);
            monitorServerThread.start();
            System.out.printf("Monitor Thread started as %s\n", monitorServerThread.toString());
            while (this.isListening()) {
                System.out.println("Listening for Client CONTROL connections ...");
                Socket socket = cPlaneSsocket.accept();
                Server.cPlaneRunnable cPlane = new Server.cPlaneRunnable(this, socket);
                boolean added = this.addControlRunnable(cPlane);
                Thread cThread = new Thread(cPlane);
                cThread.start();
                System.out.println("spawned a server CONTROL plane thread to handle a new client " + cPlane.getClientName());
            }
            monitorServerThread.join();
            System.out.printf("MONITORING THREAD (%s) terminated\n", monitorServerThread.toString());
        } catch (IOException ex1) {
            ex1.printStackTrace(System.err);
            System.err.println("Experienced an IOException. Exiting Server program with error.");
        } catch (InterruptedException ex2) {
            ex2.printStackTrace(System.err);
            System.err.println("Experienced an InterruptedException. Exiting Server program with error.");
        } finally {

        }
    }

    /** Represents a Thread Runnable for control plane command processing.
     * @author Rob Marano <rob@konsilix.com>
     * @version 0.1
     * @since 0.1
     */
    protected class cPlaneRunnable implements Runnable, AutoCloseable {
        protected Server server;
        protected Socket socket;
        protected String clientName;

        protected cPlaneRunnable(Server ss, Socket s) {
//            super("cPlaneThread");
            System.out.printf("Created cPlaneRunnable = %s\n",s.toString());
            this.server = ss;
            this.socket = s;
            this.setClientName();
        }

        public String getClientName() {
            return(this.clientName);
        }

        protected void setClientName() {
            String name = null;
            try {
                name = Utils.generateUniqueName(socket);
            } catch (IOException ex) {
                System.err.println("Unable to get socket information to get hostname and IP address:");
                ex.printStackTrace(System.err);
                name = "UNKNOWN";
            }
            this.clientName = name;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Connected to client = %s\n", this.clientName));
            sb.append(String.format("\t%s: server = %s, socket = %s\n", super.toString(),this.server.toString(), this.socket.toString()));
            return(sb.toString());
        }

        @Override
        public void close() {
            this.server.removeControlRunnable(this);
        }

        public void run() {
            System.out.println("cThread run()");
            try (
                    PrintWriter cout = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ) {
                System.out.printf("\t%s: output stream = %s\n", this.toString(), cout.toString());
                System.out.printf("\t%s: input stream = %s\n", this.toString(), in.toString());
                String inputLine, outputLine;
                Map<String, String> instruction = null;
                cout.println("Connected to " + Protocol.SERVER_HEADER);
                cout.flush();
//                while (!Thread.currentThread().isInterrupted()) {
                    while (!this.socket.isClosed() && ( (inputLine = in.readLine()) != null) ) {
                        System.out.println("CLIENT-> " + inputLine + " from " + socket.toString());
                        System.out.println(server.getCurrentState());
                        instruction = this.server.getNextState(inputLine);
                        System.out.println(this.server.getCurrentState());
                        this.server.executeInstruction(instruction, cout);
                        System.out.println(this.server.getCurrentState());
                        switch (this.server.getCurrentState()) {
//                            case Protocol.STATE.NAMING:
//                                if (instruction.get("my-action").equalsIgnoreCase("NAME")) {
//                                    if (instruction.get("object") != null) {
//                                        this.setClientName(instruction.get("object"));
//                                    }
//                                    System.out.printf("New client registered as %s\n", this.getClientName());
//                                }
//                                break;
                            case Protocol.STATE.DISCONNECTING:
                                socket.close();
                                this.server.removeControlRunnable(this);
                                this.server.setCurrentState(Protocol.STATE.DONE);
                                break;
                            case STATE.EXITING:
                                socket.close();
                                System.out.println("DANGER DANGER DANGER - exiting from Server through a cPlaneRunnable");
                                System.exit(0); // VERY DANGEROUS - BRUTE FORCE EXIT.
                            default:
                                break;
                        }
                        System.out.println(server.getCurrentState());
                    }
//                }
                System.out.println("closing socket " + socket.toString());
                socket.close();
//            } catch (IOException | InterruptedException e) {
            } catch (IOException e) {
                System.err.println(server.getCurrentState());
                e.printStackTrace(System.err);
            }
            boolean removedCThread = server.serverThreadList.remove(this);
            System.out.println("cPlane thread end: removed thread = "+removedCThread);
        }
    }

    /** Represents a thread to monitor the Server application.
     * @author Rob Marano <rob@konsilix.com>
     * @version 0.1
     * @since 0.1
     */
    public class MonitorServer implements Runnable {

        protected Server iServer;

        public MonitorServer(Server ss) {
            this.iServer = ss;
        }

        protected void pruneCplanes() {
            System.out.println("PRUNING cPlaneRunnables if their sockets are closed");
            for (cPlaneRunnable cpRunnable : this.iServer.serverThreadList) {
                if (! cpRunnable.socket.isConnected()) {
                    this.iServer.removeControlRunnable(cpRunnable);
                    System.out.printf("\tDELETING (%s):%s\n", cpRunnable.socket.toString(),cpRunnable.toString());
                }
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.print(serverThreadList.size());
                System.out.print(" " + serverThreadList.toString());
                this.pruneCplanes();
                try {
                    Thread.sleep(Protocol.DEFAULT_SLEEP_TIME_CHECK_SHUTDOWN);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * main line for an instance of the Server class
     * <p>
     * @param  args  array of Strings from command line options for the program
     * @return      through System.exit() returns the exit code as processed
     * @see         Server
     */
    public static void main(String[] args) {
        int exitCode = 0;
        int cport = Protocol.DEFAULT_CONTROL_PLANE_PORT;
        int dport = Protocol.DEFAULT_DATA_PLANE_PORT;

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
            Server iServer = new Server(cport, dport);
            System.out.printf("SERVER instance = %s\n",iServer.toString());
            Thread serverThread = new Thread(iServer);
            System.out.printf("SERVER THREAD instance = %s\n",serverThread.toString());
            serverThread.start();
            System.out.println("\tstarted SERVER THREAD instance.");
            serverThread.join();
            System.out.println("SERVER THREAD terminated");
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
        System.out.printf("EXITING server mainline with exit code = %d\n",exitCode);
        System.exit(exitCode);
    }
}
