package com.company;
import java.util.Vector;
import java.util.Date;

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
        daysOff.add(d);
    }
    public void removeDay(Date d){
        daysOff.remove(d);
    }
    public void clear(){
        daysOff.clear();
    }
}
