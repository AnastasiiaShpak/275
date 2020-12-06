/*
Work to do for the task on one day. Percentage attribute indicates what is percentage percentage of the whole task is done on this day

CMPT275 Project
Group 21
 */

package com.company;

import java.awt.*;

public class BuiltTask {
    private String name;
    private double hours;
    private double percentage;
    private Color color;

    public BuiltTask(String name, double hours, double percentage, Color color){
        this.name = name;
        this.hours = hours;
        this.percentage = percentage;
        this.color = color;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setHours(double hours){
        this.hours = hours;
    }
    public void setPercentage(double p){
        this.percentage = p;
    }
    public String getName(){
        return name;
    }
    public double getHours(){
        return hours;
    }
    public double getPercentage(){
        return percentage;
    }
    public Color getColor() {
        return color;
    }
}
