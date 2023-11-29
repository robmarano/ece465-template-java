package ece465.zk.configuration;

import ece465.zk.listeners.AppFileChangeListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.time.Duration;

@Configuration
@Scope("singleton")
public class FileWatcherConfig {
    public static final long DEFAULT_POLL_INTERVAL_MS = 2000L;
    public static final long DEFAULT_QUIET_PERIOD_MS = 1000L;

    @Value("${storage.location}")
    private String location;

    @Bean
    public FileSystemWatcher fileSystemWatcher() {
        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(true,
                Duration.ofMillis(DEFAULT_POLL_INTERVAL_MS), Duration.ofMillis(DEFAULT_QUIET_PERIOD_MS));

        fileSystemWatcher.addSourceDirectory(new File(location));
        fileSystemWatcher.addListener( new AppFileChangeListener() ) ;

        fileSystemWatcher.start() ;

        System.out.println( "started fileSystemWatcher" ) ;
        return(fileSystemWatcher);
    }
}
