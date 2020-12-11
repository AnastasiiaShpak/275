/*
Test for Category class, all methods are covered

CMPT275 Project
Group 21
 */

package com.company;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {
    private Date dec1 = new Date(120, Calendar.DECEMBER, 1);
    private Date dec2 = new Date(120, Calendar.DECEMBER, 2);
    private Color blue = Color.BLUE;

    @Test
    void Category(){
        //successful case
        //name was already validated by categoriesList class
        Category category = new Category("C");
        assertEquals(category.getName(), "C");
        assertEquals(category.getSize(), 0);
        assertNull(category.getColor());
    }

    @Test
    void setName(){
        Category category = new Category("C");
        Task t1 = new Task("A", dec1, dec2, 2, false);
        t1.setColor(blue);
        Task t2 = new Task("B", dec1, dec2, 2, false);
        category.addTask(t1);
        category.addTask(t2);

        //empty name check
        assertEquals(category.setName(""), -1);

        //edge of invalid task length - 26 characters
        assertEquals(category.setName("aaaaaaaaaaaaaaaaaaaaaaaaaa"), -1);

        //input the same name as category's name
        assertEquals(category.setName("C"), 0);
        assertEquals(t1.getCategory(), "C");
        assertEquals(t2.getCategory(), "C");
        assertEquals(t1.getColor(), blue);
        assertEquals(t2.getColor(), blue);

        //valid input
        assertEquals(category.setName("C2"), 0);
        assertEquals(t1.getCategory(), "C2");
        assertEquals(t2.getCategory(), "C2");
        assertEquals(t1.getColor(), blue);
        assertEquals(t2.getColor(), blue);
    }

    @Test
    void addTask() {
        Category category = new Category("C");

        //add first task (changes color of category)
        Task t1 = new Task("A", dec1, dec2, 2, false);
        t1.setColor(blue);
        assertEquals(category.addTask(t1), 0);
        assertEquals(category.getColor(), blue);
        assertEquals(category.getName(), "C");
        assertEquals(category.getSize(), 1);
        assertEquals(t1.getColor(), blue);

        //repeated task
        assertEquals(category.addTask(t1), -1);

        //in different category
        Task t2 = new Task("B", dec1, dec2, 2, false);
        t2.setCategory("C2");
        assertEquals(category.addTask(t2), -2);

        //add second task (changes color of task)
        t2.setCategory("");
        assertEquals(category.addTask(t2), 0);
        assertEquals(category.getColor(), blue);
        assertEquals(category.getName(), "C");
        assertEquals(category.getSize(), 2);
        assertEquals(t2.getColor(), blue);
    }

    @Test
    void removeTask() {
        Category category = new Category("C");

        //add first task (changes color of category)
        Task t1 = new Task("A", dec1, dec2, 2, false);
        t1.setColor(blue);
        Task t2 = new Task("B", dec1, dec2, 2, false);
        category.addTask(t1);
        category.addTask(t2);
        assertEquals(category.getSize(), 2);

        //remove one of many tasks
        category.removeTask(t2);
        assertEquals(t2.getCategory(), "");
        assertNotEquals(t2.getColor(), blue); //different color is generated
        assertEquals(category.getSize(), 1);
        assertEquals(category.getColor(), blue); //category color hasn't changed

        //remove last task
        category.removeTask(t1);
        assertEquals(t1.getCategory(), "");
        assertNotEquals(t1.getColor(), blue); //different color is generated
        assertEquals(category.getSize(), 0);
        assertEquals(category.getColor(), blue); //category color hasn't changed
    }

    @Test
    void clear() {
        Category category = new Category("C");

        category.clear();
        assertNull(category.getColor());
        assertEquals(category.getName(), "C");

        //add first task
        Task t1 = new Task("A", dec1, dec2, 2, false);
        t1.setColor(blue);
        category.addTask(t1);

        //clear category with tasks
        category.clear();
        assertNull(category.getColor());
        assertEquals(category.getName(), "C");
        assertEquals(category.getSize(), 0);
        assertNotEquals(t1.getColor(), blue);
        assertEquals(t1.getCategory(), "");
    }
}