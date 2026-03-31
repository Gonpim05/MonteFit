package com.example.montefit;

import java.util.ArrayList;
import java.util.List;

public class TrainingSessionItem {
    private Exercise exercise;
    private List<SetDetail> sets;

    public TrainingSessionItem(Exercise exercise) {
        this.exercise = exercise;
        this.sets = new ArrayList<>();
    }

    public Exercise getExercise() {
        return exercise;
    }

    public List<SetDetail> getSets() {
        return sets;
    }

    public void addSet(int reps, double weight) {
        sets.add(new SetDetail(reps, weight));
    }

    public static class SetDetail {
        public int reps;
        public double weight;

        public SetDetail(int reps, double weight) {
            this.reps = reps;
            this.weight = weight;
        }
    }
}
