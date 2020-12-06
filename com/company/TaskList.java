/*
Task list
Contains vector of tasks, keeps tasks sorted by start date, keeps track of what is earliest and latest dates in a schedule and schedule duration.
Maximum number of tasks = 50

CMPT275 Project
Group 21
 */
package com.company;
import Support.ColorGenerator;

import java.awt.*;
import java.util.Vector;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskList {
    private Vector<Task> tasks;
    private Date earliest;
    private Date latest;
    private int duration = 0;

    //constructor
    public TaskList(){
        tasks = new Vector<>();
    }
    public int getDuration() {
        return duration;
    }
    public Vector<Task> getTasks() {
        return tasks;
    }
    public Date getEarliest() {
        return earliest;
    }
    public Date getLatest() {
        return latest;
    }
    //-1 if tasks is not initialized
    public int getSize(){
        if(tasks != null)
            return tasks.size();
        else
            return -1;
    }

    //sorted by start date
    //-1 for repeated name
    //-2 for mixed start and deadline
    //-3 for going beyond schedule limit
    //-4 for too long name
    //-5 exceed task limit
    // 0 for successful result
    public int addTask(Task t){
        //if name already exists
        for(Task task: tasks){
            if(task.getName().equals(t.getName())){
                return -1;
            }
        }
       int validation = validateTask(t);
       if(validation != 0)
           return validation;

       if(tasks.size() == 50){
           return -5;
       }

        //update earliest and latest dates
        Date first = earliest;
        Date last = latest;
        if(earliest == null || t.getStart().compareTo(earliest) < 0){
           first = t.getStart();
        }
        if(latest == null || t.getDeadline().compareTo(latest) > 0){
            last = t.getDeadline();
        }
        int difference = Data.difference(first, last);
        earliest = first;
        latest = last;
        duration = difference;


        if(tasks.size() == 0){
            tasks.add(t);
        }else{
            boolean inserted = false;
            for(int i = 0; i < tasks.size(); i++){
                if(tasks.get(i).getStart().compareTo(t.getStart()) > -1){
                    tasks.insertElementAt(t, i);
                    inserted = true;
                    break;
                }
            }

            if(!inserted){
                tasks.add(t);
            }
        }
        t.setColor(ColorGenerator.generateColor());
        return 0;
    }

    //-1 if task is not in the list
    // 0 if successful
    public int removeTask(Task t){
        //task does not exist
        if(!tasks.contains(t))
            return -1;

        //update earliest date
        if(t.getStart().compareTo(earliest) == 0){
            if(tasks.size() == 1)
                earliest = null;
            else {
                Date newEarliest = null;
                for (Task task: tasks){
                    if(task != t){
                        if (newEarliest == null) {
                            newEarliest = task.getStart();
                            if (newEarliest.compareTo(earliest) == 0)
                                break;
                        } else if(task.getStart().compareTo(newEarliest) < 0){
                            newEarliest = task.getStart();
                            if(newEarliest.compareTo(earliest) == 0)
                                break;
                        }
                    }

                }
                earliest = newEarliest;

            }
        }
        //end update earliest

        //update last date
        if(t.getDeadline().compareTo(latest) == 0){
            if(tasks.size() == 1)
                latest = null;
            else {
                Date newLatest = null;
                for (Task task: tasks){
                    if(task != t){
                        if (newLatest == null) {
                            newLatest = task.getDeadline();
                            if (newLatest.compareTo(latest) == 0)
                                break;
                        }else if(task.getDeadline().compareTo(newLatest) > 0){
                            newLatest = task.getDeadline();
                            if(newLatest.compareTo(latest) == 0)
                                break;
                        }
                    }
                }
                latest = newLatest;
            }
        }
        //end update latest date

        //update duration
        if(earliest != null && latest != null)
            duration = Data.difference(earliest, latest);
        else{
            duration = 0;
        }

        //remove task from category
        ColorGenerator.freeColor(t.getColor());
        if(!t.getCategory().equals("")){
            Category c =  Data.getCategories().getCategory(t.getCategory());
            c.removeTask(t);
        }

        tasks.remove(t);
        return 0;
    }

    //-1 if repeated name in new task
    //-2 for mixed start and deadline in new task
    //-3 for going beyond schedule limit in new task
    //-4 for too long name in new task
    //-5 if task is not is the list
    //0 if successful
    public int editTask(Task oldTask, Task newTask){
        if(!newTask.getName().equals(oldTask.getName())){
            //if name already exists
            for(Task task: tasks){
                if(task.getName().equals(newTask.getName())){
                    return -1;
                }
            }
        }

        if(!tasks.contains(oldTask))
            return -5;

        int validation = validateTask(newTask);
        if(validation != 0)
            return validation;

        if(newTask.getStart().compareTo(earliest) < 0) {
            earliest = newTask.getStart();
        }

        if(newTask.getDeadline().compareTo(latest) > 0)
            latest = newTask.getDeadline();

        duration = Data.difference(earliest, latest);

        oldTask.setDeadline(newTask.getDeadline());
        oldTask.setStart(newTask.getStart());
        oldTask.setHours(newTask.getHours());
        oldTask.setDuration(Data.difference(oldTask.getStart(), oldTask.getDeadline()));
        oldTask.setName(newTask.getName());
        oldTask.setPriority(newTask.getPriority());

        tasks.removeElement(oldTask);
        boolean inserted = false;
        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i).getStart().compareTo(oldTask.getStart()) > -1){
                tasks.insertElementAt(oldTask, i);
                inserted = true;
                break;
            }
        }

        if(!inserted){
            tasks.add(oldTask);
        }
        return 0;
    }

    //-1 if repeated name in new task
    //-2 for mixed start and deadline in new task
    //-3 for going beyond schedule limit in new task
    //-4 for too long name in new task
    // 0 from correct input
    private int validateTask(Task t){
        //if deadline is before start
        if(t.getStart().compareTo(t.getDeadline()) > 0){
            return -2;
        }

        //if task duration goes beyond schedule limit
        Date first = earliest;
        Date last = latest;
        if(earliest == null || t.getStart().compareTo(earliest) < 0){
            first = t.getStart();
        }
        if(latest == null || t.getDeadline().compareTo(latest) > 0){
            last = t.getDeadline();
        }
        int difference = Data.difference(first, last);
        if(difference > 365)                          //schedule limit is 1 year
            return -3;

        //if task name is too long
        if(t.getName().length() > 25){
            return -4;
        }

        return 0;
    }

    //if days > 0 move forward
    //if move < 0 move back
    //return 0 if successful
    //return -1 if schedule becomes too long
    //return -2 if task is not in a list
    public int moveTask(Task t, int days){
        Date newStart;
        Date newDeadline;
        Date newEarliest = null;
        Date newLatest = null;
        if(!tasks.contains(t))
            return -2;

        if(days == 0){
            return 0;
        }else{
            newStart = new Date(t.getStart().getTime() + TimeUnit.DAYS.toMillis(days));
            newDeadline = new Date(t.getDeadline().getTime() + TimeUnit.DAYS.toMillis(days));
        }
        //update earliest date
        if(t.getStart().compareTo(earliest) > 0){
            if (newStart.compareTo(earliest) < 0)
                newEarliest = newStart;
            else
                newEarliest = earliest;
        }else if(t.getStart().compareTo(earliest) == 0){
            if(newStart.compareTo(earliest) < 0){
                newEarliest = newStart;
            }else {
                newEarliest = newStart;
                for (Task currentTask : tasks) {
                    if (currentTask != t) {
                        if (currentTask.getStart().compareTo(newEarliest) < 0) {
                            newEarliest = currentTask.getStart();
                        }
                    }
                }
            }
        }

        //end update earliest
        //update last date
        if(t.getDeadline().compareTo(latest) < 0){
            if (newDeadline.compareTo(latest) > 0)
                newLatest = newDeadline;
            else
                newLatest = latest;
        }else if(t.getDeadline().compareTo(latest) == 0){
            if(newDeadline.compareTo(latest) > 0){
                newLatest = newDeadline;
            }else {
                newLatest = newDeadline;
                for (Task currentTask : tasks) {
                    if (currentTask != t) {
                        if (currentTask.getDeadline().compareTo(newLatest) > 0) {
                            newLatest = currentTask.getDeadline();
                        }
                    }
                }
            }
        }
        //end update latest date
        // if schedule becomes too long
        if(Data.difference(newEarliest, newLatest) > 365)
            return -1;
        else{
            earliest = newEarliest;
            latest = newLatest;
        }
        //update duration
        duration = Data.difference(earliest, latest);
        //update task dates
        t.setStart(newStart);
        t.setDeadline(newDeadline);
        tasks.removeElement(t);
        boolean inserted = false;
        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i).getStart().compareTo(t.getStart()) > -1){
                tasks.insertElementAt(t, i);
                inserted = true;
                break;
            }
        }
        if(!inserted){
            tasks.add(t);
        }
        return 0;
    }
    //clear task list
    public void clear(){
        tasks.clear();
        earliest = null;
        latest = null;
        duration = 0;
    }
}

