package ece465.zk.util;

import java.util.Random;

public class Utils {
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
