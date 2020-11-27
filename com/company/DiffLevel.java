package com.company;

public class DiffLevel {
    private double hours;
    private String name;
    private String color;

    DiffLevel(double hours, String name, String color){
        this.color = color;
        this.hours = hours;
        this.name = name;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
