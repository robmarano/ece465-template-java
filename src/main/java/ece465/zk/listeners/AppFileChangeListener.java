package ece465.zk.listeners;

import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.stereotype.Component;

import java.util.Set;

public class AppFileChangeListener implements FileChangeListener {

    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        String mainPath = changeSet.iterator().next().getSourceDirectory().toString();
        for( ChangedFiles cfiles : changeSet ) {
            for( ChangedFile cfile : cfiles.getFiles() ) {
                System.out.println("File changed: " + mainPath + "/" + cfile);
            }

        }
    }
}
