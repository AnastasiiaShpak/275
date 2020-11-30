/*

 */

package com.company;
import java.awt.*;
import java.util.Date;

public class Task {
    private String name;
    private Date start;
    private Date deadline;
    private int duration;
    private double hours;
    private int levelOfDifficulty;
    private Color color;
    private String category;
    private boolean priority;

    public Task(String name, Date start, Date deadline, double hours, boolean prioritized){
        this.name = name;
        this.start = start;
        this.deadline = deadline;
        this.hours = hours;
        this.priority = prioritized;
        this.category = "";
        setDuration();
    }
    private void setDuration(){
        long startTime = start.getTime();
        long dueTime = deadline.getTime();
        long diffTime = dueTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24) + 1;
        duration = (int) diffDays;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getName(){
        return name;
    }
    public Date getStart(){
       return start;
    }
    public Date getDeadline(){
        return deadline;
    }
    public double getHours(){
        return hours;
    }
    public int getLevelOfDifficulty(){
        return levelOfDifficulty;
    }
    public Color getColor(){
        return color;
    }
    public String getCategory(){
        return category;
    }
    public boolean getPriority(){
        return priority;
    }
    public int getDuration(){return duration;}
}
