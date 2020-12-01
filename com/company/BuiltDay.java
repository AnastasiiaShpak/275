/*
Built Day represents a particular date and contains list of tasks to do on this date.

CMPT 275
Group 21
 */

package com.company;
import java.util.Date;
import java.util.Vector;

public class BuiltDay {
    private Date date;
    private double totalHours = 0;
    private Vector<BuiltTask> builtTasks;

    public BuiltDay(Date d){
        builtTasks = new Vector<BuiltTask>();
        this.date = d;
    }
    public void addBTask(BuiltTask bt){
        builtTasks.add(bt);
        totalHours += bt.getHours();
    }
    public void setBuiltTasks(Vector<BuiltTask> builtTasks) {
        this.builtTasks = builtTasks;
    }
    public void setDate(Date d){
        this.date = d;
    }
    public void setTotalHours(double tot){
        this.totalHours = tot;
    }
    public Date getDate(){
        return date;
    }
    public double getTotalHours(){
        return totalHours;
    }
    public int getSize(){
        return builtTasks.size();
    }
    public Vector<BuiltTask> getBuiltTasks() {
        return builtTasks;
    }
}
