package com.example.montefit;

import java.io.Serializable;
import java.util.List;

public class Entrenamiento implements Serializable {
    private long id;
    private String fecha;
    private List<EjercicioDetalle> ejercicios;

    public static class EjercicioDetalle implements Serializable {
        public String nombre;
        public int series;
        public double peso;

        public EjercicioDetalle(String nombre, int series, double peso) {
            this.nombre = nombre;
            this.series = series;
            this.peso = peso;
        }
    }

    public Entrenamiento(long id, String fecha, List<EjercicioDetalle> ejercicios) {
        this.id = id;
        this.fecha = fecha;
        this.ejercicios = ejercicios;
    }

    public long getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public List<EjercicioDetalle> getEjerciciosDetalle() {
        return ejercicios;
    }
}
