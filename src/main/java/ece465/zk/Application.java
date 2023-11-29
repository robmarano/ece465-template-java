package ece465.zk;

import ece465.zk.configuration.SwaggerConfiguration;
import ece465.zk.listeners.AppFileChangeListener;
import ece465.zk.listeners.SpringAppEventsListener;
import ece465.zk.services.StorageService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/** @author "Rob Marano" 2023/11/27 */
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
//        SpringApplication.run(Application.class, args);
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new SpringAppEventsListener());
        application.addListeners(new SwaggerConfiguration());
        application.run(args);
//        application.setWebEnvironment(true);
//        application.setBannerMode(Banner.Mode.OFF);
//        application.setLogStartupInfo(false);
//        application.setRegisterShutdownHook(false);
//        application.setHeadless(false);
//        application.setAddCommandLineProperties(false);
//        application.setAllowBeanDefinitionOverriding(false);
//        application.setAllowCircularReferences(false);
//        application.setAllowContextSensitiveLogging(false);
//        application.setAllowEagerClassLoading(false);
//        application.setAllowMultipleApplications(false);
//        application.setAllowReference(false);
//        application.setAllowSyntheticDefaultImports(false);
//        application.setAllowStartupFailure(false);
//        application.setAllowBeanDefinitionOverriding(false);
//        application.setAllowCircularReferences(false);
//        application.setAllowContextSensitiveLogging(false);
//        application.setAllowEagerClassLoading(false);
//        application.setAllowMultipleApplications(false);
//        application.setAllowReference(false);
//        application.setAllowSyntheticDefaultImports(false);
        System.out.println("Application started");
//        System.out.println("Application pid = " + new Application().pid);
//        System.out.println("Application pid file = " + PID_FILE_NAME + "." + new Application().pid + ".pid");
        System.out.println("Application pid file location = " + System.getProperty("user.dir") + "/" + PID_FILE_NAME + "." + new Application().pid + ".pid");
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