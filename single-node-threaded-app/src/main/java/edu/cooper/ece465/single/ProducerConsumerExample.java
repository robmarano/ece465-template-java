package edu.cooper.ece465.single;

import edu.cooper.ece465.threaded.commons.Consumer;
import edu.cooper.ece465.threaded.commons.Producer;
import edu.cooper.ece465.threaded.commons.Drop;

import edu.cooper.ece465.commons.utils.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ProducerConsumerExample {
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(ProducerConsumerExample.class);

    public static int PROD_SIZE = 5;
    public static int CONSUME_SIZE = 5;

    static {
        Properties prop = new Properties();
        InputStream input = (FileInputStream) null;
        try {
            // input =
            // ClassLoader.getSystemClassLoader().getResourceAsStream("cubbyHole.properties");
            // input =
            // ProducerConsumerTest.class.getResourceAsStream("/cubbyHole.properties");
            ClassLoader loader = ProducerConsumerTest.class.getClassLoader();
            InputStream in = loader.getResourceAsStream("cubbyHole.properties");
            prop.load(in);
            System.out.println(prop.getProperty("prod.size"));
            PROD_SIZE = Integer.parseInt(prop.getProperty("prod.size"));
            CONSUME_SIZE = Integer.parseInt(prop.getProperty("consume.size"));
        } catch (Exception ex1) {
            String errorMessage = String.format("Encountered a FATAL exception: %s", ex1.getMessage());
            Utils.handleException(LOG, ex1, errorMessage);
        } catch (Error ex2) {
            String errorMessage = String.format("Encountered a FATAL error: %s", ex2.getMessage());
            Utils.handleError(LOG, ex2, errorMessage);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Drop drop = new Drop();
        (new Thread(new Producer(drop))).start();
        (new Thread(new Consumer(drop))).start();
        (new Thread(new Consumer(drop))).start();
    }
}
