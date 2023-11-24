package ece465.zk;

import ece465.zk.services.StorageService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/** @author "Bikas Katwal" 26/03/19 */
//@ComponentScan("ece465.zk")
@SpringBootApplication(scanBasePackages={"ece465.zk"})
//@EnableAutoConfiguration()
public class Application {
    static String PID_FILE_NAME = "zkApp";
    @Getter(AccessLevel.PUBLIC)
    final long pid;

    public Application() {
        this.pid = this.fetchPid();
        try {
            writePidToLocalFile(PID_FILE_NAME + "." + this.pid+".pid", this.pid);
        } catch (IOException ex) {
            System.err.println("Cannot write pid file for this pid = " + this.pid);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }

    public long fetchPid() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

    public static void writePidToLocalFile(String fileName, final long pid) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(String.format("%d", pid));
        writer.close();
    }
}