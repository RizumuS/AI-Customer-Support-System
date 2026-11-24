package com.aisupport.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hash a password
    public static String hashPassword(String password) {

        return BCrypt.hashpw(
            password,
            BCrypt.gensalt(12)
        );
    }

    // Verify a password
    public static boolean verifyPassword(
            String password,
            String hashedPassword) {

        return BCrypt.checkpw(
            password,
            hashedPassword
        );
    }
}