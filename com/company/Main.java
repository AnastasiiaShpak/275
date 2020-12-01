package com.company;
import ScheduleBuilder.ScheduleBuilder;
import Support.WorkloadCalculator;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        //create dates
        Date date1 = new Date(120, Calendar.DECEMBER, 5);
        Date date2 = new Date(120, Calendar.DECEMBER, 19);

        Date date3 = new Date(120, Calendar.DECEMBER, 2);
        Date date4 = new Date(120, Calendar.DECEMBER, 16);

        Date date5 = new Date(120, Calendar.DECEMBER, 10);
        Date date6 = new Date(120, Calendar.DECEMBER, 30);

        Date date7 = new Date(120, Calendar.DECEMBER, 5);
        Date date8 = new Date(120, Calendar.DECEMBER, 30);

        Date date9 = new Date(120, Calendar.DECEMBER, 2);
        Date date10 = new Date(120, Calendar.DECEMBER, 30);

        Date date11 = new Date(120, Calendar.DECEMBER, 6);
        Date date12 = new Date(120, Calendar.DECEMBER, 13);
        Date date13 = new Date(120, Calendar.DECEMBER, 20);
        Date date14 = new Date(120, Calendar.DECEMBER, 27);

        //create tasks
        Task t1 = new Task("t1", date1, date2, 12, false);
        Data.getTaskList().addTask(t1);
        Task t2 = new Task("t2", date3, date4, 30, true);
        Data.getTaskList().addTask(t2);
        Task t3 = new Task("t3", date5, date6, 20, false);
        Data.getTaskList().addTask(t3);
        Task t4 = new Task("t4", date7, date8, 10, false);
        Data.getTaskList().addTask(t4);
        Task t5 = new Task("t5", date9, date10, 30, true);
        Data.getTaskList().addTask(t5);
        Task t6 = new Task("something", date11, date12, 3, false);
        Data.getTaskList().addTask(t6);

        //add days off
        Data.getDayOffList().addRepeatedDayOff(date11, 7);

        //calculate schedule
        ScheduleBuilder sb = new ScheduleBuilder();

        //get workload phrase
        String wl = WorkloadCalculator.getWorkload();
        System.out.println(wl);

        //print schedule
        if(Data.getBuiltDayList().successful)
            Data.printBDList();
        else
            System.out.println("Unsuccessful");
        System.out.println('\n');

        //print colors before categories
        for(int i = 0; i < Data.getTaskList().getSize(); i++){

            System.out.println("Task "+ Data.getTaskList().getTasks().get(i).getName() + ": " + Data.getTaskList().getTasks().get(i).getColor());
        }
        System.out.println('\n');

        //create categories
        Data.setCategoriesList(new CategoriesList());
        Data.getCategories().addCategory("C1");
        Data.getCategories().getCategory("C1").addTask(t3);
        Data.getCategories().getCategory("C1").addTask(t4);
        Data.getTaskList().removeTask(t3);
        Data.getCategories().getCategory("C1").addTask(t5);


        //print colors after categories
        for(int i = 0; i < Data.getTaskList().getSize(); i++){

            System.out.println("Task "+ Data.getTaskList().getTasks().get(i).getName() + ": " + Data.getTaskList().getTasks().get(i).getColor());
        }
        System.out.println('\n');

        //remove one category
        Data.getCategories().removeCategory("C1");
        Data.getCategories().getCategory("C2").removeTask(t1);

        //print categories after removal
        for(int i = 0; i < Data.getTaskList().getSize(); i++){

            System.out.println("Task "+ Data.getTaskList().getTasks().get(i).getName() + ": " + Data.getTaskList().getTasks().get(i).getColor());
        }
        System.out.println('\n');

    }
}
