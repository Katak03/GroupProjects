package com.example.groupproject.model;

import com.google.gson.annotations.SerializedName;

public class Exercise {
    // These names match your database columns exactly
    private int exerciseid;
    @SerializedName("exerciseName")
    private String exerciseName;
    @SerializedName("calories")
    private int calories;
    @SerializedName(value="expPoints", alternate={"exp_points", "points"})
    private int expPoints;
    @SerializedName(value="exerciseDetails", alternate={"details", "description"})
    private String exerciseDetails;
    private String exerciseType;
    private String category;
    private int isActive;

    // Constructors, Getters, and Setters
    public int getId() { return exerciseid; }
    public String getExerciseName() { return exerciseName; }
    public int getCalories() { return calories; }
    public int getExpPoints() { return expPoints; }
    public String getExerciseDetails() { return exerciseDetails; }
    public String getExerciseType() { return exerciseType; }
    public String getCategory() { return category; }

    public int getIsActive() { return isActive; }
}
