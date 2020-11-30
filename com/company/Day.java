package com.company;
import java.util.Date;

public class Day{
    private Date date;
    private double limit;

    public Day(Date d, int l){
        this.date = d;
        this.limit = l;
    }
    public void setLimit(double limit){
        this.limit = limit;
    }
    public void setDate(Date d){
        this.date = d;
    }
    public double getLimit(){
        return limit;
    }
    public Date getDate(){
        return date;
    }
}
