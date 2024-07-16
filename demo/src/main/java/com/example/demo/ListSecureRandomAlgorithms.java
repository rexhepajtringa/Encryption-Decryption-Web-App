package com.example.demo;

import java.security.Security;
import java.util.Set;

public class ListSecureRandomAlgorithms {
    public static void main(String[] args) {
        Set<String> algorithms = Security.getAlgorithms("SecureRandom");
        for (String algorithm : algorithms) {
            System.out.println(algorithm);
        }
    }
}
