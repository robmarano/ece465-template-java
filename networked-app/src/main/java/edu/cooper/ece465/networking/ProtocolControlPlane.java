package edu.cooper.ece465.networking;

import java.util.Locale;

public class ProtocolControlPlane {

    protected enum STATE {
        STARTUP, READY, CONNECT, DISCONNECT, PROCESSING, SHUTDOWN, UPLOAD
    };
    protected String[] COMMANDS = { "CONNECT", "SEND_FILE", "RCV_FILE", "GET_TIME", "DISCONNECT", "SHUTDOWN" };
    protected STATE state;
    public ProtocolControlPlane() {
        this.setState(STATE.STARTUP);
    }

    public STATE getState() {
        return this.state;
    }

    protected void setState(STATE state) {
        this.state = state;
    }

    protected String process(String input) {
        String output;
        switch (input.toUpperCase()) {
            case "CONNECT":
                this.setState(STATE.CONNECT);
                output = "CONNECTED";
                break;
            case "UPLOAD":
                this.setState(STATE.UPLOAD);
                output = "START UPLOAD";
                break;
            case "DISCONNECT":
                this.setState(STATE.DISCONNECT);
                output = "BYE";
                break;
            case "SHUTDOWN":
                this.setState(STATE.SHUTDOWN);
                output = "SHUTTING DOWN";
                break;
            default:
                this.setState(STATE.READY);
                output = "READY";
                break;
        }
        return(output);
    }


    public String processInput(String theInput) {
        String theOutput = null;

        switch (this.getState()) {
            case STARTUP:
                System.out.printf("%s is in %s state.\n",this.getClass().getName(), this.getState().toString());
                theOutput = this.process(theInput);
                break;
            case CONNECT:
                System.out.printf("%s is in %s state.\n",this.getClass().getName(), this.getState().toString());
                theOutput = this.process(theInput);
                break;
            case UPLOAD:
                System.out.printf("%s is in %s state.\n",this.getClass().getName(), this.getState().toString());
                theOutput = this.process(theInput);
            case DISCONNECT:
                System.out.printf("%s is in %s state.\n",this.getClass().getName(), this.getState().toString());
                theOutput = this.process(theInput);
                break;
            case READY:
                System.out.printf("%s is in %s state.\n",this.getClass().getName(), this.getState().toString());
                theOutput = this.process(theInput);
                break;
            case PROCESSING:
                System.out.printf("%s is in %s state.\n",this.getClass().getName(), this.getState().toString());
                theOutput = this.process(theInput);
                break;
            case SHUTDOWN:
                System.out.printf("%s is in %s state.\n",this.getClass().getName(), this.getState().toString());
                theOutput = this.process(theInput);
                break;
            default:
                System.err.printf("reach unknown state (%s) in ProcessControlPlane\n", state.toString());
                break;
        }
        return theOutput;
    }
}
