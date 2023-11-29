package ece465.zk.util;

//import org.apache.tomcat.util.codec.binary.Base64;
//import org.springframework.http.HttpHeaders;

//import java.nio.charset.Charset;
import java.util.Random;

public class Utils {

    // see https://www.baeldung.com/how-to-use-resttemplate-with-basic-authentication-in-spring#manual_auth
//    public static HttpHeaders createHeaders(String username, String password){
//        return new HttpHeaders() {{
//            String auth = username + ":" + password;
//            byte[] encodedAuth = Base64.encodeBase64(
//                    auth.getBytes(Charset.forName("US-ASCII")) );;
//            String authHeader = "Basic " + new String( encodedAuth );
//            set( "Authorization", authHeader );
//        }};
//    }

    public static String generateRandomString(int length) {
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String randomString = "";
        for (int i = 0; i < length; i++) {
            randomString += characters.charAt(random.nextInt(characters.length()));
        }
        return randomString;
    }
}
