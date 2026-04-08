package com.example.montefit;

import java.util.ArrayList;
import java.util.List;

public class EjercicioSesion {
    private Ejercicio Ejercicio;
    private List<DetalleSerie> sets;

    public EjercicioSesion(Ejercicio Ejercicio) {
        this.Ejercicio = Ejercicio;
        this.sets = new ArrayList<>();
    }

    public Ejercicio getEjercicio() {
        return Ejercicio;
    }

    public List<DetalleSerie> getSeries() {
        return sets;
    }

    public void addSerie(int repeticiones, double peso) {
        sets.add(new DetalleSerie(repeticiones, peso));
    }

    public static class DetalleSerie {
        public int repeticiones;
        public double peso;

        public DetalleSerie(int repeticiones, double peso) {
            this.repeticiones = repeticiones;
            this.peso = peso;
        }
    }
}








