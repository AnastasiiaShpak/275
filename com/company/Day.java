package com.company;
import java.util.Date;

public class Day {
    private Date date;
    private double limit;

    Day(Date d, int l){
        this.date = d;
        this.limit = l;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }
}
