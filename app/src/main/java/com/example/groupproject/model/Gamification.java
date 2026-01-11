package com.example.groupproject.model;

import com.google.gson.annotations.SerializedName;

public class Gamification {

    // If your DB column is `id` (user id)
    @SerializedName("id")
    private int id;

    @SerializedName("currentLevel")
    private int currentLevel;

    @SerializedName("currentExp")
    private int currentExp;

    @SerializedName("currentStreaks")
    private int currentStreaks;

    @SerializedName("bestStreak")
    private int bestStreak;

    @SerializedName("totalExp")
    private int totalExp;

    // ----- Getters -----
    public int getId() {
        return id;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public int getCurrentStreaks() {
        return currentStreaks;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public int getTotalExp() {
        return totalExp;
    }

    // ----- Setters -----
    public void setId(int id) {
        this.id = id;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
    }

    public void setCurrentStreaks(int currentStreaks) {
        this.currentStreaks = currentStreaks;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public void setTotalExp(int totalExp) {
        this.totalExp = totalExp;
    }
}
