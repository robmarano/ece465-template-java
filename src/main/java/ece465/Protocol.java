package ece465;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Protocol {
    public static String VERSION = "v0.0.1";
    public static String SERVER_HEADER = "MYFTP Server " + VERSION;
    public static String CLIENT_HEADER = "MYFTP Client " + VERSION;
    public static String DEFAULT_SERVER_HOST = "localhost";
    public static int DEFAULT_CONTROL_PLANE_PORT = 1971;
    public static int DEFAULT_DATA_PLANE_PORT = 1972;
    public static long DEFAULT_SLEEP_TIME_CHECK_SHUTDOWN = 10000; //msec

    public static int DEFAULT_CHUNK_SIZE = 4 * 1024; // 4kB chunk size of files over network

    protected String hostname;
    protected int controlPort;
    protected int dataPort;

    public enum STATE {
        READY, PENDING, ACTIVE, DONE, FAIL, RETRY, SENDING, RECEIVING, DISCONNECTING, EXITING
    }; // IDLE

    public enum CLIENT_EXIT_CODES {
        Success, NumberFormatException, IOException, InterruptedException
    };

    public final static String[] COMMANDS = {
      "VERSION", "COMMANDS", "SEND", "GET", "BYE", "SHUTDOWN"
    };

    protected STATE currentState;

    public Protocol() {
        super();
        this.setCurrentState(STATE.PENDING);
    }

    public Protocol(STATE s) {
        this();
        this.setCurrentState(s);
    }

    protected void printState() {
        System.out.println("Protocol state = " + getCurrentState());
    }

    public STATE getCurrentState() {
        return(currentState);
    }

    protected void setCurrentState(STATE s) {
        this.currentState = s;
    }

    public int getControlPort() {
        return(this.controlPort);
    }

    protected void setControlPort(int c) {
        this.controlPort = c;
    }

    protected void setDataPort(int d) {
        this.dataPort = d;
    }

    public int getDataPort() {
        return(this.dataPort);
    }

    public String getHostname() {
        return(this.hostname);
    }

    public void setHostname(String h) {
        this.hostname = h;
    }

    public static boolean isValidCommand(String userInput) {
        boolean isValid = false;
        String[] event;
        String action;
        String object;
        try {
            if (userInput != null && !userInput.isEmpty()) {
                event = userInput.split(" ");
                action = event[0].toUpperCase();
                switch (action) {
                    case "COMMANDS":
                    case "SHUTDOWN":
                    case "BYE":
                    case "VERSION":
                        isValid = true;
                        break;
                    case "GET":
                    case "SEND":
                        if (event.length == 2) {
                            object = event[1];
                            isValid = true;
                        }
                        break;
                    default:
                        isValid = false;
                        break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            isValid = false;
        } catch (Exception ex) {
            isValid = false;
        }
        return(isValid);
    }

    public static String getInputFromUser(BufferedReader stdin, String prompt) throws IOException {
        boolean done = false;
        String userInput = null;
        while (!done) {
            System.out.print(prompt);
            userInput = stdin.readLine();
            if (userInput != null && !userInput.isEmpty()) {
//                break;
                done = isValidCommand(userInput);
            } else {
                // TODO write error checking code here in order to catch here before sending to server
                done = false;
            }
        }
        return userInput;
    }
    public static void sendFile(final String path, final OutputStream out)
            throws FileNotFoundException, SecurityException, IOException {
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        DataOutputStream dout = new DataOutputStream(out);
        System.out.println("sendFile(): " + file.toString() + " " + dout.toString());

        // send file size
        dout.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[Protocol.DEFAULT_CHUNK_SIZE];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dout.write(buffer,0,bytes);
        }
        fileInputStream.close();
        dout.flush();
    }

    public static void receiveFile(final String fileName, final InputStream in)
            throws FileNotFoundException, IOException {
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        DataInputStream din = new DataInputStream(in);
        System.out.println("receiveFile(): " + fileOutputStream.toString() + " " + din.toString());

        long size = din.readLong();     // read file size
        byte[] buffer = new byte[Protocol.DEFAULT_CHUNK_SIZE];
        while (size > 0 && (bytes = din.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }
        fileOutputStream.close();
    }

    public Map<String, String> getNextState(String command) {
        Map<String, String> instruction = new HashMap<>();
        synchronized (getCurrentState()) {
            //instruction to implementer of Protocol: action of implementer, object of action
            STATE newState = STATE.PENDING;
            String[] event = command.split(" ");
            String action = event[0].toUpperCase();
            // this action is defined by the Client connecting via network with this protocol
            switch (action) {
                case "VER":
                case "VERSION":
                    instruction.put("my-action", "VERSION");
                    instruction.put("peer-action", "VERSION");
                    instruction.put("object", Protocol.VERSION);
                    newState = STATE.READY;
                    break;
                case "COMMANDS":
                    instruction.put("my-action", "COMMANDS");
                    instruction.put("peer-action", "COMMANDS");
                    instruction.put("object", Arrays.toString(Protocol.COMMANDS));
                    newState = STATE.READY;
                    break;
                case "SHUTDOWN":
                    instruction.put("my-action", "SHUTDOWN");
                    instruction.put("peer-action", "DISCONNECT");
                    instruction.put("object", "now");
                    newState = STATE.EXITING;
                    break;
                case "SEND": // SEND /dir/filename
//                    if (this.getCurrentState() == STATE.READY) {
                    if (event.length > 1) {
                        instruction.put("my-action", "RECEIVE");
                        instruction.put("peer-action", "SEND");
                        instruction.put("object", event[1]);
                        newState = STATE.RECEIVING;
                    } else {
                        instruction.put("my-action", "IGNORE");
                        instruction.put("peer-action", "ERROR");
                        instruction.put("object", "MISSING FILE in GET command");
                        newState = STATE.READY;
                    }

//                    } else {
//                        instruction.put("my-action", "IGNORE"); // state-less, peer needs to retry
//                        instruction.put("peer-action", "WAIT");
//                        instruction.put("object", String.format("%s %s", action, event[1]));
//                        newState = STATE.RECEIVING;
//                    }
                    break;
                case "GET": // GET /dir/filename
//                    if (this.getCurrentState() == STATE.PENDING) {
                    if (event.length > 1) {
                        instruction.put("my-action", "SEND");
                        instruction.put("peer-action", "RECEIVE");
                        instruction.put("object", event[1]);
                        newState = STATE.SENDING;
                    } else {
                        instruction.put("my-action", "IGNORE");
                        instruction.put("peer-action", "ERROR");
                        instruction.put("object", "MISSING FILE in SEND command");
                        newState = STATE.READY;
                    }
//                    } else {
//                        instruction.put("WAIT", String.format("%s %s", action, event[1]));
//                    }
                    break;
                case "HELO":
                    instruction.put("my-action", "HELO");
                    instruction.put("peer-action", "HELO");
                    instruction.put("object", "now");
                    newState = STATE.READY;
                    break;
                case "DISCONNECT":
                case "BYE":
                case "EXIT":
                    instruction.put("my-action", "DISCONNECT");
                    instruction.put("peer-action", "DISCONNECT");
                    instruction.put("object", "now");
                    newState = STATE.DISCONNECTING;
                    break;
                default:
                    instruction.put("my-action", "IGNORE");
                    instruction.put("peer-action", "IGNORE");
                    instruction.put("object", "now");
                    newState = STATE.READY;
                    break;
            }
            this.setCurrentState(newState);
            System.out.println(newState + " : " + instruction.toString());
        }
        return(instruction);
    }
}
