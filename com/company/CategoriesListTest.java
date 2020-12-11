/*
Testing of Categories List class
Simple getters and setters are not covered

CMPT275 Project
Group 21
 */

package com.company;
import org.junit.jupiter.api.Test;
import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;

class CategoriesListTest {
    private CategoriesList cList = new CategoriesList();
    @Test
    void addCategory() {
        //case1: name length exceeds 25 characters
        assertEquals(cList.addCategory("aaaaaaaaaaaaaaaaaaaaaaaaaa"), -1);

        //case2: name is empty
        assertEquals(cList.addCategory(""), -1);

        //case3: successful case
        assertEquals(cList.addCategory("A"), 0);

        //case4: repeated name
        assertEquals(cList.addCategory("A"), -2);

        //case5: number of categories exceed limit of 50
        for(int i = 0; i < 49; i++)
            assertEquals(cList.addCategory(Integer.toString(i)), 0);
        assertEquals(cList.addCategory("51"), -3);
        cList.categories.removeAllElements();
    }

    @Test
    void removeCategory() {
        assertEquals(cList.addCategory("A"), 0);

        //case1: category is not in the list
        assertEquals(cList.removeCategory("B"), -1);

        //case2: remove empty category
        assertEquals(cList.removeCategory("A"), 0);

        //remove category with task
        assertEquals(cList.addCategory("A"), 0);
        Task t = new Task("A", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 2, false);
        cList.getCategory("A").addTask(t);
        assertEquals(cList.removeCategory("A"), 0);
    }

    @Test
    void getCategory() {
        //case1: category is not in the list
        assertNull(cList.getCategory("A"));

        //case2: successful case
        cList.addCategory("A");
        assertEquals(cList.getCategory("A"), cList.categories.elementAt(0));
    }
}