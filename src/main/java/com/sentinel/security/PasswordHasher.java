package com.sentinel.security;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public static String hash(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword,BCrypt.gensalt(12));
    }

    public static boolean verify(String typedPassword,String savedHash){
        return BCrypt.checkpw(typedPassword, savedHash);
    }
}
