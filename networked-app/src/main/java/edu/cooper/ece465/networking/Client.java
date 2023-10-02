package edu.cooper.ece465.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) throws Exception {
        String hostName;
        int cportNumber;
        int dportNumber;

        if (args.length != 3) {
            hostName = "localhost";
            cportNumber = DefaultConfigurations.DEFAULT_CONTROL_PLANE_PORT;
            dportNumber = DefaultConfigurations.DEFAULT_DATA_PLANE_PORT;
            System.err.println(
                    "Usage: java Client <host name> <control plane port number> <data plane port number");
            System.out.printf("Using default port numbers for control (%d) and data (%d) ports\n", cportNumber, dportNumber);
        } else {
            hostName = args[0];
            cportNumber = Integer.parseInt(args[1]);
            dportNumber = Integer.parseInt(args[2]);
        }
        System.out.printf("Connecting to %s: control (%d) and data (%d) ports.\n", hostName, cportNumber, dportNumber);

        try (
                // setup control plane with server
                Socket cSocket = new Socket(hostName, cportNumber);
                PrintWriter cout = new PrintWriter(cSocket.getOutputStream(), true);
                BufferedReader cin = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
                // set up data plane with server
                Socket dSocket = new Socket(hostName, dportNumber);
        ) {
            // Sending commands over control plane
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            cout.println("CONNECT");

            while ((fromServer = cin.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("BYE"))
                    break;

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    cout.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(-2);
        }
    }
}