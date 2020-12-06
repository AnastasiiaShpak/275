package com.company;
import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {
    private TaskList tList = new TaskList();
    private Date dec3 = new Date(120, Calendar.DECEMBER, 3);
    private Date dec4 = new Date(120, Calendar.DECEMBER, 4);
    private Date dec5 = new Date(120, Calendar.DECEMBER, 5);
    private Date dec6 = new Date(120, Calendar.DECEMBER, 6);
    private Date dec7 = new Date(120, Calendar.DECEMBER, 7);
    private Date dec10 = new Date(120, Calendar.DECEMBER, 10);
    private Date dec3_2021 = new Date(121, Calendar.DECEMBER, 3);
    private Date dec4_2021 = new Date(121, Calendar.DECEMBER, 4);


    @org.junit.jupiter.api.Test
    void getDuration(){
        Task task1 = new Task("A", dec5, dec6, 5, false);
        tList.addTask(task1);
        assertEquals(tList.getDuration(), 2);
    }

    @org.junit.jupiter.api.Test
    void getSize() {
        //check how size changes when tasks are added
        Task task1 = new Task("A", dec5, dec6, 5, false);
        Task task2 = new Task("B", dec10, dec10, 5, false);
        tList.addTask(task1);
        tList.addTask(task2);
        assertEquals(tList.getSize(), 2);

        //check if size changes there is unsuccessful attempt to add task
        Task task3 = new Task("C", dec6, dec5, 5, false);
        tList.addTask(task3);
        assertEquals(tList.getSize(), 2);
    }

    @org.junit.jupiter.api.Test
    void addTask() {
        Task task1 = new Task("A", dec6, dec5, 5, false);
        Task task2 = new Task("A", dec10, dec10, 5, false);

        //deadline before start
        assertEquals(tList.addTask(task1), -2);

        //update latest, earliest for the fist time
        task1.setDeadline(dec4_2021);
        task1.setStart(dec10);
        assertEquals(tList.addTask(task1), 0);
        assertEquals(tList.getEarliest(), dec10);
        assertEquals(tList.getLatest(), dec4_2021);

        //repeated name
        assertEquals(tList.addTask(task2), -1);

        //go beyond schedule limit
        task2.setName("B");
        task2.setStart(dec3);
        assertEquals(tList.addTask(task2), -3);

        //too long name
        task2.setStart(dec10);
        task2.setName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        assertEquals(tList.addTask(task2), -4);

        //update earliest for the second time
        task2.setName("B");
        task2.setStart(dec7);
        assertEquals(tList.addTask(task2), 0);
        assertEquals(tList.getEarliest(), dec7);
    }

    @org.junit.jupiter.api.Test
    void removeTask() {
        //Test1: remove the only task from list
        Task task1 = new Task("A", dec5, dec6, 5, false);
        tList.addTask(task1);
        assertEquals(tList.removeTask(task1), 0);
        assertEquals(tList.getSize(), 0);
        assertNull(tList.getEarliest());
        assertNull(tList.getLatest());
        assertEquals(tList.getDuration(), 0);


        //Test2: remove task that is not in a list
        tList.addTask(task1);
        Task task2 = new Task("B", dec5, dec10, 5, false);
        assertEquals(tList.removeTask(task2), -1);


        //Test3: remove one of many tasks in the list
        tList.addTask(task2);
        assertEquals(tList.removeTask(task2), 0);
        assertEquals(tList.getDuration(), 2);
        assertEquals(tList.getSize(), 1);
        assertEquals(tList.getEarliest(), dec5);
        assertEquals(tList.getLatest(), dec6);
    }

    @org.junit.jupiter.api.Test
    void editTask() {
        Task task1 = new Task("A", dec5, dec6, 5, false);
        Task task2 = new Task("B", dec5, dec6, 5, false);
        Task task3 = new Task("B", dec5, dec6, 5, false);
        Task task4 = new Task("D", dec5, dec6, 5, false);
        tList.addTask(task1);
        tList.addTask(task2);

        //repeated name
        assertEquals(tList.editTask(task1, task3), -1);
        assertEquals(tList.getSize(), 2);

        //deadline before start
        task3.setName("C");
        task3.setDeadline(dec3);
        assertEquals(tList.editTask(task2, task3), -2);
        assertEquals(tList.getSize(), 2);

        //goes beyond schedule limit
        task3.setDeadline(dec4_2021);
        task3.setStart(dec3);
        assertEquals(tList.editTask(task2, task3), -3);
        assertEquals(tList.getSize(), 2);

        //too long name
        task3.setStart(dec3);
        task3.setDeadline(dec10);
        task3.setName("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        assertEquals(tList.editTask(task2, task3), -4);
        assertEquals(tList.getSize(), 2);

        //task is not in a list
        task3.setName("C");
        assertEquals(tList.editTask(task3, task4), -5);
        assertEquals(tList.getSize(), 2);

        //latest and earliest date change, duration change
        assertEquals(tList.editTask(task2, task3), 0);
        assertEquals(tList.getLatest(), dec10);
        assertEquals(tList.getEarliest(), dec3);
        assertEquals(tList.getDuration(), 8);
        assertEquals(tList.getSize(), 2);
    }

    @org.junit.jupiter.api.Test
    void moveTask() {
        Task task1 = new Task("A", dec5, dec6, 5, false);
        Task task2 = new Task("B", dec5, dec4_2021, 5, false);
        Task task3 = new Task("C", dec5, dec4_2021, 5, false);
        tList.addTask(task1);
        tList.addTask(task2);

        //schedule becomes too long
        assertEquals(tList.moveTask(task2, 2), -1);

        //task is not in schedule
        assertEquals(tList.moveTask(task3, 1), -2);

        //move forward
       assertEquals(tList.moveTask(task1, 1), 0);
       assertEquals(tList.getSize(), 2);
       assertEquals(tList.getEarliest(), dec5);
       assertEquals(tList.getLatest(), dec4_2021);
       assertEquals(tList.getDuration(), 365); //test boundary for schedule duration limit
       assertEquals(task1.getStart(), dec6);
       assertEquals(task1.getDeadline(), dec7);

       //move backward + change in earliest and latest schedule dates
        assertEquals(tList.moveTask(task2, -1), 0);
        assertEquals(tList.getSize(), 2);
        assertEquals(tList.getEarliest(), dec4);
        assertEquals(tList.getLatest(), dec3_2021);
        assertEquals(tList.getDuration(), 365); //test boundary for schedule duration limit
        assertEquals(task2.getStart(), dec4);
        assertEquals(task2.getDeadline(), dec3_2021);
    }

    @org.junit.jupiter.api.Test
    void clear() {
        Task task1 = new Task("A", dec5, dec6, 5, false);
        Task task2 = new Task("B", dec5, dec10, 5, false);
        tList.addTask(task1);
        tList.addTask(task2);
        tList.clear();
        assertEquals(tList.getSize(), 0);
        assertNull(tList.getEarliest());
        assertNull(tList.getEarliest());
        assertEquals(tList.getDuration(), 0);
    }
}