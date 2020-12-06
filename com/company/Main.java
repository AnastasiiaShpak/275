package com.company;
import ScheduleBuilder.ScheduleBuilder;
import Support.WorkloadCalculator;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        //create dates
        Date date1 = new Date(120, Calendar.DECEMBER, 5);
        Date date2 = new Date(120, Calendar.DECEMBER, 6);

        Date date3 = new Date(120, Calendar.DECEMBER, 6);
        Date date4 = new Date(120, Calendar.DECEMBER, 7);

        Date date5 = new Date(120, Calendar.DECEMBER, 7);
        Date date6 = new Date(120, Calendar.DECEMBER, 8);

        Date date7 = new Date(120, Calendar.DECEMBER, 8);
        Date date8 = new Date(120, Calendar.DECEMBER, 9);

        Date date9 = new Date(120, Calendar.DECEMBER, 9);
        Date date10 = new Date(120, Calendar.DECEMBER, 10);

        Date date11 = new Date(120, Calendar.DECEMBER, 5);
        Date date12 = new Date(120, Calendar.DECEMBER, 10);

        Date date13 = new Date(120, Calendar.DECEMBER, 5);
        Date date14 = new Date(120, Calendar.DECEMBER, 7);

        //create tasks
        Task t1 = new Task("A", date1, date2, 2, false);
        Data.getTaskList().addTask(t1);
        Task t2 = new Task("B", date3, date4, 2, true);
        Data.getTaskList().addTask(t2);
        Task t3 = new Task("C", date5, date6, 2, false);
        Data.getTaskList().addTask(t3);
        Task t4 = new Task("D", date7, date8, 2, false);
        Data.getTaskList().addTask(t4);

        ScheduleBuilder sb = new ScheduleBuilder();
        sb.balanceTasks();
        if(Data.getBuiltDayList().successful) {
            //get workload phrase
            String wl = WorkloadCalculator.getWorkload();
            System.out.println(wl);
            Data.printBDList();
        }else
            System.out.println("Unsuccessful");
        System.out.println('\n');


        Data.getDayOffList().addRepeatedDayOff(date1, 2);


        //create categories
        Data.setCategoriesList(new CategoriesList());
        Data.getCategories().addCategory("C1");
        Data.getCategories().getCategory("C1").addTask(t3);
        Data.getCategories().getCategory("C1").addTask(t4);
        Data.getCategories().getCategory("C1").addTask(t2);

        Data.getTaskList().moveTask(t2, 2);
        Data.getTaskList().moveTask(t3, -2);
        Data.getTaskList().removeTask(t4);
        Data.getTaskList().removeTask(t2);
        Data.getTaskList().editTask(t3, new Task("C", date5, date6, 2, true));


        sb = new ScheduleBuilder();
        if(Data.getBuiltDayList().successful) {
            //get workload phrase
            String wl = WorkloadCalculator.getWorkload();
            System.out.println(wl);
            Data.printBDList();
        }else
            System.out.println("Unsuccessful");
        System.out.println('\n');

    }
}
