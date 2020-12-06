/*
List of Dates that user chose to be days off. Day off can't have work to do.

CMPT275 Project
Group 21
 */

package com.company;
import java.util.Vector;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DayList {
    private Vector<Date> daysOff;
    public DayList(){
        daysOff = new Vector<>();
    }
    public Vector<Date> getDays() {
        return daysOff;
    }
    public int getSize(){
        return daysOff.size();
    }
    public void setDays(Vector<Date> daysOff) {
        this.daysOff = daysOff;
    }
    public void addDayOff(Date d){
        if(!daysOff.contains(d))
            daysOff.add(d);
    }

    //adds Date d in any case
    //-1 if cycle is negative
    //0 otherwise
    public int addRepeatedDayOff(Date d, int cycle){
        addDayOff(d);
        if(cycle < 0)
            return -1;

        else if(cycle == 0){
            return 0;
        }

        Date d2 = new Date(d.getTime() + TimeUnit.DAYS.toMillis(cycle));
        while (d2.compareTo(Data.getTaskList().getEarliest()) >= 0 && d2.compareTo(Data.getTaskList().getLatest()) <= 0){
            addDayOff(d2);
            d2 = new Date(d2.getTime() + TimeUnit.DAYS.toMillis(cycle));
        }
        return 0;
    }
    public void removeDay(Date d){
        daysOff.remove(d);
    }
    public void clear(){
        daysOff.clear();
    }
}
