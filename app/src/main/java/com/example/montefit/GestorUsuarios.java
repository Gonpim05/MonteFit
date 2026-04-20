package com.example.montefit;

/**
 * Singleton que guarda el estado del usuario actual.
 * Ya NO usa GestorBaseDatos (SQLite). Ahora solo guarda el correo del usuario logueado.
 * Las pantallas usan ClienteApi directamente para acceder a MySQL.
 */
public class GestorUsuarios {

    private static GestorUsuarios instancia;
    private String correoActual;
    private int usuarioIdActual = -1;

    private GestorUsuarios() {}

    public static synchronized GestorUsuarios getInstance() {
        if (instancia == null) {
            instancia = new GestorUsuarios();
        }
        return instancia;
    }

    public void setCorreoActual(String correo) {
        this.correoActual = correo;
    }

    public String getCorreoActual() {
        return correoActual;
    }

    public void setUsuarioId(int id) {
        this.usuarioIdActual = id;
    }

    public int getUsuarioId() {
        return usuarioIdActual;
    }

    public void cerrarSesion() {
        correoActual = null;
        usuarioIdActual = -1;
    }
}
