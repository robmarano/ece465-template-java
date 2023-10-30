package ece465;

import jdk.jshell.execution.Util;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Protocol implements Runnable {
    protected Protocol.PROGRAM_EXIT_CODES exitCode;
    protected final String pidFileName;

    public Client() {
        super();
        this.pidFileName = String.format("%s.pid", this.getClass().getName());
        try {
            writePidToLocalFile(pidFileName);
        } catch (IOException ex) {
            System.err.println("Unable to write the PID file:");
            ex.printStackTrace(System.err);
        }
        System.out.println(Protocol.CLIENT_HEADER);
        this.setExitCode(PROGRAM_EXIT_CODES.Success);
    }

    public synchronized Protocol.PROGRAM_EXIT_CODES getExitCode() {
        return (this.exitCode);
    }

    protected synchronized void setExitCode(Protocol.PROGRAM_EXIT_CODES code) {
        this.exitCode = code;
    }

    public Client(String serverHostName, int cport, int dport) {
        this();
        this.setServerHostname(serverHostName);
        this.setControlPort(cport);
        this.setDataPort(dport);
        this.setCurrentState(Protocol.STATE.READY);
    }

    protected void setMyUniqueName(final Socket socket) {
        String name = null;
        if (super.getMyUniqueName().equalsIgnoreCase("UNKNOWN")) {
            try {
                name = Utils.generateUniqueName(socket);
            } catch (IOException ex) {
                System.err.println("Unable to get socket information to get hostname and IP address:");
                ex.printStackTrace(System.err);
                name = "UNKNOWN";
            }
        }
        super.setMyUniqueName(name);
    }

    protected void sendCommandToServer(PrintWriter cout, String fromUser) {
        System.out.print("sendCommandToServer:  ");
        this.printState();
        System.out.println("... sending command to server: "+fromUser+"...");
        cout.println(fromUser);
    }

    protected String getCommandFromServer(BufferedReader cin) throws IOException {
        System.out.print("getCommandFromServer:  ");
        this.printState();
        String fromServer = cin.readLine();
        System.out.println("... sending command to server: "+fromServer+"...");
        return(fromServer);
    }

    @Override
    public void run() {
        System.out.printf("Connecting initial state (%s) to %s: control (%d) and data (%d) ports.\n",
                getCurrentState().toString(), getServerHostname(), getControlPort(), getDataPort());
        try
        (
            // setup control plane with server
            // the client/server talk on this channel
            // when there is something to move back and forth over network,
            // the originator starts up a data channel, sends the port from which to receive,
            // as well as what is requested
            Socket cSocket = new Socket(super.getServerHostname(), getControlPort());
            PrintWriter cout = new PrintWriter(cSocket.getOutputStream(), true);
            BufferedReader cin = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            // set up data plane with server
//            Socket dSocket = new Socket(getHostname(), getDataPort());
//            BufferedReader din = new BufferedReader(new InputStreamReader(dSocket.getInputStream()));
//            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(dSocket.getOutputStream()));
        ) {
            setMyUniqueName(cSocket);
            System.out.printf("\t client unique name = %s\n", getMyUniqueName());
            // Sending commands over control plane
            String fromServer;
            String fromUser;
            while (cSocket.isConnected() && (fromServer = this.getCommandFromServer(cin)) != null) {
                System.out.printf("fromServer = %s\n", fromServer);
                String[] event = fromServer.split(" ");
                String action = event[0].toUpperCase();
                String object = null;
                if (event.length > 1) {
                    object = event[1].toUpperCase();
                }
                this.printState();
                if (action.equalsIgnoreCase("HELO")) {
                    this.setCurrentState(STATE.READY);
                    this.printState();
                    System.out.println(object);
                } else if (action.equalsIgnoreCase("VERSION")) {
                    this.setCurrentState(STATE.READY);
                    this.printState();
                    System.out.println(object);
                } else if (action.equalsIgnoreCase("COMMANDS")) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < event.length; i++) {
                        sb.append(event[i].replaceAll("[\\[\\]]", ""));
                        if (i < event.length - 1) {
                            sb.append(" ");
                        }
                    }
                    System.out.printf("%s\n", sb.toString());
                    this.setCurrentState(STATE.READY);
                    this.printState();
                    System.out.println(object);
                } else if (action.equalsIgnoreCase("NAME")) {
                    System.out.printf("This client registered with Server as %s\n", event[1]); // or object
                    this.setCurrentState(STATE.READY);
                    this.printState();
                } else if (action.equalsIgnoreCase("LIST")) {
                    this.setCurrentState(STATE.READY);
                    this.printState();
                    System.out.println(object);
                } else if (action.equalsIgnoreCase("RECEIVE")) {
                    System.out.println("run()  RECEIVE:");
                    this.printState();
                    try (
                            Socket dSocket = new Socket(getServerHostname(), getDataPort());
                            InputStream in = dSocket.getInputStream();
                    ) {
                        System.out.printf("opened data socket %s\n", dSocket.toString());
                        System.out.printf("\tinput stream = %s\n", in.toString());
                        if (object != null) {
                            this.setCurrentState(STATE.RECEIVING);
                            this.printState();
                            Protocol.receiveFile(object, in);  // TODO check if need to do binary or not
                            System.out.printf("Received file %s\n", object);
                            this.setCurrentState(STATE.READY);
                            this.printState();
                        }
                    } catch (FileNotFoundException ex) {
                        System.err.println("caught FileNotFoundException");
                        this.printState();
                        ex.printStackTrace(System.err);
                    } catch (SecurityException ex) {
                        System.err.println("caught SecurityException");
                        this.printState();
                        ex.printStackTrace(System.err);
                    } catch (IOException ex) {
                        System.err.println("caught IOException");
                        this.printState();
                        ex.printStackTrace(System.err);
                    }
                } else if (action.equalsIgnoreCase("SEND")) {
                    this.printState();
                    try (
                            Socket dSocket = new Socket(getServerHostname(), getDataPort());
                            OutputStream out = dSocket.getOutputStream();
                    ) {
                        if (object != null) {
                            this.setCurrentState(STATE.SENDING);
                            this.printState();
                            Protocol.sendFile(object, out); // TODO check if need to do binary or not
                            System.out.printf("Sent file %s\n", object);
                            this.setCurrentState(STATE.READY);
                            this.printState();
                        }
                    } catch (FileNotFoundException ex) {
                        System.err.println("caught FileNotFoundException");
                        this.printState();
                        ex.printStackTrace(System.err);
                    } catch (SecurityException ex) {
                        System.err.println("caught SecurityException");
                        this.printState();
                        ex.printStackTrace(System.err);
                    } catch (IOException ex) {
                        System.err.println("caught IOException");
                        this.printState();
                        ex.printStackTrace(System.err);
                    }
                } else if (action.equalsIgnoreCase("ERROR")) {
                    // process errors like when Client does not give file name for SEND/GET
                    this.printState();
                    if (object != null) {
                        System.err.println(object);
                    }
                    this.setCurrentState(STATE.READY);
                } else if (action.equalsIgnoreCase("DISCONNECT")) {
                    System.out.println("run() receive server disconnecting ...");
                    this.setCurrentState(STATE.DISCONNECTING);
                    this.printState();
                    cSocket.close();
                    this.setCurrentState(STATE.EXITING);
                    this.printState();
                    break;
                } else if (fromServer.startsWith("Connected to")) { // initial comms exchange, so register name
                    System.out.println(fromServer);
//                    String initialCmd = String.format("NAME %s", this.getMyUniqueName());
//                    this.sendCommandToServer(cout, initialCmd);
                } else {
                    System.out.printf("DID NOT UNDERSTAND server statement: %s\n", fromServer);
                    this.printState();
                    this.setCurrentState(STATE.READY);
                    this.printState();
                }
                // since did not understand, skip and ask client to send another commmand
                if (cSocket.isConnected()) {
                    fromUser = Protocol.getInputFromUser(stdIn, "> ");
                    this.sendCommandToServer(cout, fromUser);
                    cout.flush();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't get file to send ");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + getServerHostname());
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + getServerHostname());
        }
        System.out.println("Exiting Client");
    }

    public void operate() {
        try
        {
            Thread clientThread = new Thread(this);
            clientThread.start();
            clientThread.join(); // wait here until Client decides to end comms with Server or Server disconnects
//        } catch (IOException ex1) {
//            this.setExitCode(CLIENT_EXIT_CODES.IOException);
//            ex1.printStackTrace(System.err);
//            System.err.println("Experienced an IOException. Exiting Client program with error.");
        } catch (InterruptedException ex2) {
            this.setExitCode(PROGRAM_EXIT_CODES.InterruptedException);
            ex2.printStackTrace(System.err);
            System.err.println("Experienced an InterruptedException. Exiting Client program with error.");
        } finally {
            this.setExitCode(PROGRAM_EXIT_CODES.Success);
            System.out.println("Successfully exiting Client program.");
        }
    }

    public static void main(String[] args) {
        String hostName = Protocol.DEFAULT_SERVER_HOST;
        int cport = Protocol.DEFAULT_CONTROL_PLANE_PORT;
        int dport = Protocol.DEFAULT_DATA_PLANE_PORT;

        if (args.length != 3) {
            System.err.println(
                    "Usage: java Client <host name> <control plane port number> <data plane port number");
            System.out.printf("Using default port numbers for control (%d) and data (%d) ports\n", cport, dport);
        } else {
            try {
                hostName = args[0];
                cport = Integer.parseInt(args[1]);
                dport = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ex.printStackTrace(System.err);
                System.err.println("You did not provide an acceptable port number. Check your characters.");
                System.exit(Protocol.PROGRAM_EXIT_CODES.NumberFormatException.ordinal());
            }
        }
        Client iClient = new Client(hostName, cport, dport);
        iClient.operate();
        System.exit(iClient.getExitCode().ordinal());
    }
}
