package edu.cooper.ece465.threaded.commons;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Runner extends Thread {

    private static final Logger LOG = LogManager.getLogger(Runner.class);

    protected String toStringVal;

    public Runner() {
        toStringVal = "Runner class: " + super.toString();
    }

    @Override
    public void run() {
        LOG.debug("Runner.run() - begin");

        for (int i = 0; i < 100; i++) {
            System.out.print(" " + i + " ");
        }

        LOG.debug("Runner.run() - end");
    }

    @Override
    public String toString() {
        return toStringVal;
    }
}
