package com.example.montefit;

import android.content.Context;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserManager {
    private static UserManager instance;
    private DatabaseHelper dbHelper;
    private String currentUserEmail;

    private UserManager() {

    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
            // Ensure admin user exists for testing
            if (!dbHelper.checkUser("admin@montefit.com")) {
                register("admin@montefit.com", "1234", "Admin User");
            }
        }
    }


    public boolean register(String email, String password, String name) {
        if (dbHelper == null)
            return false;
        if (dbHelper.checkUser(email)) {
            return false;
        }
        String hashedPassword = password;


        return dbHelper.addUser(email, password, name);
    }
    public boolean login(String email, String password) {
        if (dbHelper == null)
            return false;

        if (dbHelper.checkUserPassword(email, password)) {
            this.currentUserEmail = email;
            return true;
        }
        return false;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void logout() {
        currentUserEmail = null;
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
