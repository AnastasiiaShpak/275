/*
Category is a list of tasks that share the same color.
Category takes color of the first added task and changes color of every next added task to the category color.
If task is removed from category, new color is generated for it.
If category is cleared, all tasks in from this category get new colors and removed from the list.

CMPT275 Project
Group 21
 */
package com.company;
import Support.ColorGenerator;
import java.awt.*;
import java.util.Vector;

public class Category{
    Vector<Task> tasks;
    private String name;
    private Color color;

    public Category(String name){
        this.name = name;
        tasks = new Vector<Task>();
    }

    //-1 if invalid name
    //0 if successful
    public int setName(String name){
        if(name.length() > 25|| name.equals(""))
            return -1;

        this.name = name;
        //change category name for all tasks in category
        for(Task t: tasks)
            t.setCategory(name);
        return 0;
    }
    public String getName(){
        return name;
    }
    public int getSize(){
        return tasks.size();
    }

    //-1 if task already exist
    //-2 if task is in different category
    //0 is successful
    public int addTask(Task t){
        //if task is already in the category
        for(Task task: tasks){
            if(task == t){
                return -1;
            }
        }

        //if task is in different category
        if(t.getCategory() != ""){
            return -2;
        }

        //set color of category (if it's the first task) or task added
        if(tasks.size() == 0){
            color = t.getColor();
            t.setCategory(name);
        }else{
            ColorGenerator.freeColor(t.getColor());
            t.setColor(color);
        }
        tasks.add(t);
        return 0;
    }

    public void removeTask(Task t){
        if(tasks.contains(t)){
            if(tasks.size() == 1){
                ColorGenerator.freeColor(t.getColor());
            }
            t.setColor(ColorGenerator.generateColor());
            t.setCategory("");
            tasks.remove(t);
        }
    }

    public void clear(){
        ColorGenerator.freeColor(color);
        for(Task t: tasks){
            t.setColor(ColorGenerator.generateColor());
        }
        tasks = null;
    }
}
