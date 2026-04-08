package com.example.montefit;

import android.content.Context;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GestorUsuarios {
    private static GestorUsuarios instance;
    private GestorBaseDatos gestorBD;
    private String correoActual;

    private GestorUsuarios() {

    }

    public static synchronized GestorUsuarios getInstance() {
        if (instance == null) {
            instance = new GestorUsuarios();
        }
        return instance;
    }

    public void init(Context contexto) {
        if (gestorBD == null) {
            gestorBD = new GestorBaseDatos(contexto);
            // Ensure admin user exists for testing
            if (!gestorBD.checkUser("admin@montefit.com")) {
                register("admin@montefit.com", "1234", "Admin User");
            }
        }
    }


    public boolean register(String correo, String contrasena, String nombre) {
        if (gestorBD == null)
            return false;
        if (gestorBD.checkUser(correo)) {
            return false;
        }
        String hashedPassword = contrasena;


        return gestorBD.addUser(correo, contrasena, nombre);
    }
    public boolean login(String correo, String contrasena) {
        if (gestorBD == null)
            return false;

        if (gestorBD.checkUserPassword(correo, contrasena)) {
            this.correoActual = correo;
            return true;
        }
        return false;
    }

    public String getCurrentUserEmail() {
        return correoActual;
    }

    public void logout() {
        correoActual = null;
    }

    public GestorBaseDatos getDbHelper() {
        return gestorBD;
    }
    private String hashPassword(String contrasena) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contrasena.getBytes());
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










