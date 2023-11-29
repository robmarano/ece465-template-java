package ece465.zk.listeners;

import ece465.zk.model.Person;
import ece465.zk.util.ClusterInfo;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ece465.zk.util.ZkDemoUtil.getHostPortOfServer;

public class AppFileChangeListener implements FileChangeListener {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        String mainPath = changeSet.iterator().next().getSourceDirectory().toString();
        List<String> appNodes = new ArrayList<>();
        appNodes.add("localhost:8081");
        appNodes.add("localhost:8082");
        appNodes.add("localhost:8083");

        for( ChangedFiles cfiles : changeSet ) {
            for( ChangedFile cfile : cfiles.getFiles() ) {
                switch(cfile.getType()) {
                    case ChangedFile.Type.ADD:
                        System.out.println("File added: " + "/" + cfile);
                        for (String appNode : appNodes) {
                            replicateFile(appNode, cfile);
                        }
                        break;
                    case ChangedFile.Type.DELETE:
                        System.out.println("File delete: " + "/" + cfile);
                        // TODO delete file on remote server, ??
                        break;
                    case ChangedFile.Type.MODIFY:
                        System.out.println("File changed: " + "/" + cfile);
                        for (String appNode : appNodes) {
                            System.out.printf("updating file %s to %s\n", cfile.getFile().getName(),appNode);
                            replicateFile(appNode, cfile);
                        }
                        break;
                }
            }
        }

    }

    protected void replicateFile(String host, ChangedFile file) {
        if ( !getHostPortOfServer().equals(ClusterInfo.getClusterInfo().getMaster())) {
            System.out.printf("replicating file %s to %s\n", file.getFile().getName(), host);
            String requestUrl;
            requestUrl = "http://".concat(ClusterInfo.getClusterInfo().getMaster().concat("/"));

            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl,
                    ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFile().getName() + "\"").body(file)
                    , String.class);
            System.out.println(response.getBody());
        }
    }
}
