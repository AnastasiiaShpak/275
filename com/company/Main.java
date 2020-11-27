package com.company;
import ScheduleBuilder.ScheduleBuilder;

import java.util.Calendar;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        Date date1 = new Date(120, Calendar.NOVEMBER, 23);
        Date date2 = new Date(120, Calendar.NOVEMBER, 23);

        Date date3 = new Date(120, Calendar.NOVEMBER, 25);
        Date date4 = new Date(120, Calendar.DECEMBER, 7);

        Date date5 = new Date(120, Calendar.NOVEMBER, 25);
        Date date6 = new Date(120, Calendar.DECEMBER, 2);

        Date date7 = new Date(120, Calendar.NOVEMBER, 25);
        Date date8 = new Date(120, Calendar.DECEMBER, 8);

        Date date9 = new Date(120, Calendar.NOVEMBER, 25);
        Date date10 = new Date(120, Calendar.DECEMBER, 8);

        Date date11 = new Date(120, Calendar.NOVEMBER, 25);
        Date date12 = new Date(120, Calendar.DECEMBER, 15);


	    Task t1 = new Task("380 homework", date1, date1, 5, false);
        Task t2 = new Task("275 quiz", date3, date4, 3, true);
        Task t3 = new Task("351 part 5", date5, date6, 20, false);
        Task t4 = new Task("324 quiz", date7, date8, 12, false);
        Task t5 = new Task("275 Project", date9, date10, 60, true);
        Task t6 = new Task("380 exam", date11, date12, 36, true);


        Data.getTaskList().addTask(t1);
        Data.getTaskList().addTask(t2);
        Data.getTaskList().addTask(t3);
        Data.getTaskList().addTask(t4);
        Data.getTaskList().addTask(t5);
        Data.getTaskList().addTask(t6);

        Data.getDayList().addDayOff(date8);
        //Data.getDayList().addDayOff(date1);

        ScheduleBuilder sb = new ScheduleBuilder();

        if(Data.getBuiltDayList().successful)
            Data.printBDList();
        else
            System.out.println("Unsuccessful");
    }
}
