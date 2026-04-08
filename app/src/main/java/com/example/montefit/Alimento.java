package com.example.montefit;

public class Alimento {
    private int id;
    private String nombre;
    private int calorias;
    private double proteinas;
    private double carbohidratos;
    private double grasas;

    public Alimento(int id, String nombre, int calorias, double proteinas, double carbohidratos, double grasas) {
        this.id = id;
        this.nombre = nombre;
        this.calorias = calorias;
        this.proteinas = proteinas;
        this.carbohidratos = carbohidratos;
        this.grasas = grasas;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return nombre;
    }

    public int getKcal() {
        return calorias;
    }

    public double getProtein() {
        return proteinas;
    }

    public double getCarbs() {
        return carbohidratos;
    }

    public double getFats() {
        return grasas;
    }
}






