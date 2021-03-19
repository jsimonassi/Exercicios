/*
 * Classe: Exercise
 *
 * Descrição: Classe que representa o objeto Exercise
 */



package com.simonassi.exercicios;

public class Exercise {

    private String registered_at;
    private String exercise_type;
    private String bpm;
    private String steps;
    private String duration;
    private String distance;
    private String altitude;
    private String calories;

    public Exercise (){ }

    public Exercise(String registered_at, String exercise_type, String bpm, String steps, String duration, String distance, String altitude, String calories) {
        this.registered_at = registered_at;
        this.exercise_type = exercise_type;
        this.bpm = bpm;
        this.steps = steps;
        this.duration = duration;
        this.distance = distance;
        this.altitude = altitude;
        this.calories = calories;

    }

    public String getRegistered_at() {
        return registered_at;
    }

    public void setRegistered_at(String registered_at) {
        this.registered_at = registered_at;
    }

    public String getExercise_type() {
        return exercise_type;
    }

    public void setExercise_type(String exercise_type) {
        this.exercise_type = exercise_type;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}
