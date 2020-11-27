package com.company;

import java.util.Vector;

public class DayList {
    private Vector<Day> days;

    DayList(){
        days = new Vector<Day>();
    }

    //add day
    //sorted by date
    void addDay(Day d){
        if(days.size() == 0){
            days.add(d);
        }else{
            for(int i = 0; i < days.size(); i++){
                if(days.get(i).getDate().compareTo(d.getDate()) > -1){
                    days.insertElementAt(d, i);
                    break;
                }
            }
        }
    }

    public Vector<Day> getDays() {
        return days;
    }

    public void setDays(Vector<Day> days) {
        this.days = days;
    }

    void removeDay(Day d){
        days.remove(d);
    }

    int getSize(){
        return days.size();
    }
}
