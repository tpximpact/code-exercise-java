package com.tpximpact.url_shortener.util;

import java.util.Random;

public class RandomStringGenerator {
    private static String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345679";

    public static String GenerateString(int size){
        String generatedString = "";
        Random rand = new Random();
        for(int i = 0; i <= size - 1; i++){
            int pos = rand.nextInt(ALLOWED_CHARS.length());
            generatedString += ALLOWED_CHARS.charAt(pos);
        }

        return generatedString;
    }
}
