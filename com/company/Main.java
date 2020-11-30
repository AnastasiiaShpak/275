package com.company;
import ScheduleBuilder.ScheduleBuilder;
import Support.ColorGenerator;
import Support.WorkloadCalculator;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        //create dates
        Date date1 = new Date(120, Calendar.NOVEMBER, 1);
        Date date2 = new Date(120, Calendar.NOVEMBER, 10);

        Date date3 = new Date(120, Calendar.NOVEMBER, 5);
        Date date4 = new Date(120, Calendar.NOVEMBER, 8);

        Date date5 = new Date(120, Calendar.NOVEMBER, 3);
        Date date6 = new Date(120, Calendar.NOVEMBER, 9);

        Date date7 = new Date(120, Calendar.NOVEMBER, 6);
        Date date8 = new Date(120, Calendar.NOVEMBER, 10);

        Date date9 = new Date(120, Calendar.NOVEMBER, 1);
        Date date10 = new Date(120, Calendar.NOVEMBER, 3);

        Date date11 = new Date(120, Calendar.NOVEMBER, 3);
        Date date12 = new Date(120, Calendar.NOVEMBER, 6);

        //create tasks
        Task t1 = new Task("work1", date1, date2, 5, false);
        Data.getTaskList().addTask(t1);
        Task t2 = new Task("work2", date3, date4, 1, false);
        Data.getTaskList().addTask(t2);
        Task t3 = new Task("work3", date5, date6, 10, false);
        Data.getTaskList().addTask(t3);
        Task t4 = new Task("hw1", date7, date8, 5, false);
        Data.getTaskList().addTask(t4);
        Task t5 = new Task("hw2", date9, date10, 4, false);
        Data.getTaskList().addTask(t5);
        Task t6 = new Task("something", date11, date12, 3, false);
        Data.getTaskList().addTask(t6);


        //Data.getDayList().addDayOff(date8);
        //Data.getDayList().addDayOff(date1);
        //ScheduleBuilder sb = new ScheduleBuilder();

        //get workload phrase
        //String wl = WorkloadCalculator.getWorkload();
        //System.out.println(wl);

        for(int i = 0; i < Data.getTaskList().getSize(); i++){

            System.out.println("Task "+ Data.getTaskList().getTasks().get(i).getName() + ": " + Data.getTaskList().getTasks().get(i).getColor());
        }
        System.out.println('\n');

        CategoriesList cList = new CategoriesList();
        cList.addCategory("Work");
        cList.getCategory("Work").addTask(t1);
        cList.getCategory("Work").addTask(t2);
        cList.getCategory("Work").addTask(t3);
        cList.addCategory("HW");
        cList.getCategory("HW").addTask(t4);
        cList.getCategory("HW").addTask(t5);

        for(int i = 0; i < Data.getTaskList().getSize(); i++){

            System.out.println("Task "+ Data.getTaskList().getTasks().get(i).getName() + ": " + Data.getTaskList().getTasks().get(i).getColor());
        }
        System.out.println('\n');

        cList.removeCategory("HW");
        cList.getCategory("Work").removeTask(t1);

        for(int i = 0; i < Data.getTaskList().getSize(); i++){

            System.out.println("Task "+ Data.getTaskList().getTasks().get(i).getName() + ": " + Data.getTaskList().getTasks().get(i).getColor());
        }
        System.out.println('\n');

/*

        if(Data.getBuiltDayList().successful)
            Data.printBDList();
        else
            System.out.println("Unsuccessful");

 */

    }
}
