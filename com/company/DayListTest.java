/*
Testing of DayList class
simple getters and setters, one line methods are not covered by tasking

CMPT275 Project
Group 21
 */

package com.company;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DayListTest {
    DayList dList = new DayList();
    Date dec1 = new Date(120, Calendar.DECEMBER, 1);
    Date dec2 = new Date(120, Calendar.DECEMBER, 2);
    Date dec10 = new Date(120, Calendar.DECEMBER, 10);

    @Test
    void addDayOff() {
        //date 1 is added
        dList.addDayOff(dec1);
        assertEquals(dList.getSize(), 1);

        //repeated date is not added
        dList.addDayOff(dec1);
        assertEquals(dList.getSize(), 1);
    }

    @Test
    void addRepeatedDayOff() {
        Data.getTaskList().earliest = dec1;
        Data.getTaskList().latest = dec10;
        //cycle is negative, date is added anyways but cycle is ignored
        dList.addRepeatedDayOff(dec2, -2);
        assertEquals(dList.getSize(), 1);

        //cycle is 0,  date is added anyways but cycle is ignored
        dList.addRepeatedDayOff(dec1, 0);
        assertEquals(dList.getSize(), 2);

        //cycle is positive, dates are added until they go outside earliest and latest schedule dates
        dList.addRepeatedDayOff(dec2, 2);
        assertEquals(dList.getSize(), 6);
    }

}