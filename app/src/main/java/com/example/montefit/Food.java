package com.example.montefit;

public class Food {
    private int id;
    private String name;
    private int kcal;
    private double protein;
    private double carbs;
    private double fats;

    public Food(int id, String name, int kcal, double protein, double carbs, double fats) {
        this.id = id;
        this.name = name;
        this.kcal = kcal;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getKcal() {
        return kcal;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFats() {
        return fats;
    }
}
