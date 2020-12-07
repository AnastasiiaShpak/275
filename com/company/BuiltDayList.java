package com.company;
import java.util.Vector;

public class BuiltDayList {
    private Vector<BuiltDay> builtDays;
    public boolean successful = true;
    public double totalHours = 0;
    public BuiltDayList(){
        builtDays = new Vector<BuiltDay>();
    }
    public Vector<BuiltDay> getBuiltDays() {
        return builtDays;
    }
    public void setBuiltDays(Vector<BuiltDay> builtDays) {
        this.builtDays = builtDays;
    }
    public void addBDay(BuiltDay bd){
        builtDays.add(bd);
    }
    public int getSize(){
        return builtDays.size();
    }
}
