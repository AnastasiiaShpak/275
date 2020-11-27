package com.company;

import java.util.Vector;

public class Category{
    private Vector<String> tasks; //task names
    private String color;
    private int number;

    Category(String color){
        this.color = color;
        tasks = new Vector<String>();
    }

    //-1 if task already exist
    //-2 if task is in different category
    //0 is successful
    int addTask(Task t){

        //if task is already in the category
        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i) == t.getName()){
                return -1;
            }
        }

        //if task is in different category
        if(t.getCategory() != -1){
            return -2;
        }

        tasks.add(t.getName());
        return 0;
    }

    void removeTask(Task t){
        tasks.remove(t.getName());
    }

    public Vector<String> getTasks() {
        return tasks;
    }

    public void setTasks(Vector<String> tasks) {
        this.tasks = tasks;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
