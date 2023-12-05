package ece465.zk.listeners;

import ece465.zk.model.Person;
import ece465.zk.util.ClusterInfo;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
        ResponseEntity<String> uploadResponse;

        for( ChangedFiles cfiles : changeSet ) {
            for( ChangedFile cfile : cfiles.getFiles() ) {
                switch(cfile.getType()) {
                    case ChangedFile.Type.ADD:
                        System.out.println("File added: " + "/" + cfile);
                        for (String appNode : appNodes) {
                            uploadResponse = replicateFile(appNode, cfile);
                            System.out.printf("upload of %s from %s = %s", cfile, appNode, uploadResponse);
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
                            uploadResponse = replicateFile(appNode, cfile);
                            System.out.printf("upload of %s from %s = %s", cfile, appNode, uploadResponse);
                        }
                        break;
                }
            }
        }

    }

    protected ResponseEntity<String> replicateFile(String host, ChangedFile file) {
        ResponseEntity<String> response = new ResponseEntity(HttpStatus.OK);
        String whoIsMaster = ClusterInfo.getClusterInfo().getMaster();
        String whoAmI = getHostPortOfServer();
        System.out.printf("replicateFile(master = %s; me = %s) ; host = %s ; file = %s\n", whoIsMaster, whoAmI, host, file);
        if ( !whoAmI.equals(whoIsMaster)) {
            System.out.printf("replicating file %s to %s\n", file.getFile().getName(), host);
            String requestUrl;
            requestUrl = "http://".concat(ClusterInfo.getClusterInfo().getMaster().concat("/"));

//            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl,
//                    ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                            "attachment; filename=\"" + file.getFile().getName() + "\"").body(file)
//                    , String.class);
//            System.out.println(response.getBody());
//
//            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//            map.add("file", fileSystemResource);


            String filePath = file.getFile().getAbsolutePath();
            System.out.println(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            FileSystemResource fileSystemResource = new FileSystemResource(filePath);
            body.add("file", fileSystemResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            response = restTemplate.postForEntity(requestUrl, requestEntity, String.class);
        }
        return(response);
    }
}
