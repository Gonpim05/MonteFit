package com.example.montefit;

public class Ejercicio {
    private int id;
    private String nombre;
    private String parteCuerpo;

    public Ejercicio(int id, String nombre, String parteCuerpo) {
        this.id = id;
        this.nombre = nombre;
        this.parteCuerpo = parteCuerpo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return nombre;
    }

    public String getBodyPart() {
        return parteCuerpo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}



