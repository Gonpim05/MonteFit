package com.example.montefit;

public class Exercise {
    private int id;
    private String name;
    private String bodyPart;

    public Exercise(int id, String name, String bodyPart) {
        this.id = id;
        this.name = name;
        this.bodyPart = bodyPart;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBodyPart() {
        return bodyPart;
    }

    @Override
    public String toString() {
        return name;
    }
}
