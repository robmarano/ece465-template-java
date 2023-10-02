package edu.cooper.ece465.networking;

public class ServerMonitorThread extends Thread {
    protected final Server serverInstance;
    protected boolean shutdown = false;

    public ServerMonitorThread(Server iserver) {
        this.serverInstance = iserver;
    }

    public synchronized boolean getShutdown() {
        return(this.shutdown);
    }
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            while (!(this.shutdown = this.serverInstance.pendingShutdown())) {
                System.out.printf("ServerMonitorThread: shutdown = %b\n", this.getShutdown());
                if (this.getShutdown()) {
                    synchronized (this.serverInstance) {
                        this.serverInstance.shutdown();
                    }
                }
                try {
                    Thread.sleep(DefaultConfigurations.DEFAULT_SLEEP_TIME_CHECK_SHUTDOWN);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
