package com.itheima.ssm.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderUtils {
    private  static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String encodingPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public static void main(String[] args) {
        String password = "ws.2821.";
        //$2a$10$5x0SuSAGC5hycyfhkVtjp.3jCgPnb9OXWzqo1q89gQDJuEGhh1lCO
        System.out.println(encodingPassword(password));
    }
}
