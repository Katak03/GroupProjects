package com.example.groupproject.model;

import com.google.gson.annotations.SerializedName;

public class Timer {

    @SerializedName("timerID")
    private long timerID;

    @SerializedName("userID")
    private int userID;

    @SerializedName("exerciseID")
    private int exerciseID;

    @SerializedName("durationSeconds")
    private int durationSeconds;

    @SerializedName("recordedAt")
    private String recordedAt; // store as String (easier for Gson)

    // -------- Getters --------

    public long getTimerID() {
        return timerID;
    }

    public int getUserID() {
        return userID;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    // -------- Setters --------

    public void setTimerID(long timerID) {
        this.timerID = timerID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }

    // -------- Helpers (optional) --------
    public int getMinutes() {
        return durationSeconds / 60;
    }

    public int getSecondsRemainder() {
        return durationSeconds % 60;
    }
}
