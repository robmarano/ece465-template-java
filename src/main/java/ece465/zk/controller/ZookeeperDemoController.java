package ece465.zk.controller;

import static ece465.zk.util.ZkDemoUtil.getHostPostOfServer;
import static ece465.zk.util.ZkDemoUtil.isEmpty;

import ece465.zk.model.Person;
import ece465.zk.util.ClusterInfo;
import ece465.zk.util.DataStorage;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/** @author "Bikas Katwal" 26/03/19 */
@RestController
public class ZookeeperDemoController {

    private RestTemplate restTemplate = new RestTemplate();

    /*
     * example post using CURL
     * curl -X POST -H "Content-Type: application/json" -d '{ "name": "John Doe", "age": 30 }' https://example.com/api/v1/users
     */
    @PutMapping("/person/{id}/{name}")
    public ResponseEntity<String> savePerson(
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            @PathVariable("name") String name) {

        System.out.println(request);
        String requestFrom = request.getHeader("request_from");
        String leader = ClusterInfo.getClusterInfo().getMaster();
        if (!isEmpty(requestFrom) && requestFrom.equalsIgnoreCase(leader)) {
            Person person = new Person(id, name);
            DataStorage.setPerson(person);
            return ResponseEntity.ok("SUCCESS");
        }
        // If I am leader I will broadcast data to all live node, else forward request to leader
        if (amILeader()) {
            List<String> liveNodes = ClusterInfo.getClusterInfo().getLiveNodes();

            int successCount = 0;
            for (String node : liveNodes) {

                if (getHostPostOfServer().equals(node)) {
                    Person person = new Person(id, name);
                    DataStorage.setPerson(person);
                    successCount++;
                } else {
                    String requestUrl =
                            "http://"
                                    .concat(node)
                                    .concat("/")
                                    .concat("person")
                                    .concat("/")
                                    .concat(String.valueOf(id))
                                    .concat("/")
                                    .concat(name);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("request_from", leader);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    restTemplate.exchange(requestUrl, HttpMethod.PUT, entity, String.class).getBody();
                    successCount++;
                }
            }

            return ResponseEntity.ok()
                    .body("Successfully update ".concat(String.valueOf(successCount)).concat(" nodes"));
        } else {
            String requestUrl =
                    "http://"
                            .concat(leader)
                            .concat("/")
                            .concat("person")
                            .concat("/")
                            .concat(String.valueOf(id))
                            .concat("/")
                            .concat(name);
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(requestUrl, HttpMethod.PUT, entity, String.class);
        }
    }

    private boolean amILeader() {
        String leader = ClusterInfo.getClusterInfo().getMaster();
        return getHostPostOfServer().equals(leader);
    }

    @GetMapping("/persons")
    public ResponseEntity<List<Person>> getPerson() {

        return ResponseEntity.ok(DataStorage.getPersonListFromStorage());
    }

    @GetMapping("/clusterInfo")
    public ResponseEntity<ClusterInfo> getClusterinfo() {

        return ResponseEntity.ok(ClusterInfo.getClusterInfo());
    }
}