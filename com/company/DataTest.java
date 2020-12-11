package com.company;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DataTest {
    Date dec1 = new Date(120, Calendar.DECEMBER, 1);
    Date dec2 = new Date(120, Calendar.DECEMBER, 2);
    Date dec3 = new Date(120, Calendar.DECEMBER, 3);
    @Test
    void difference() {
        //when first date comes after second date
        assertEquals(Data.difference(dec2, dec1), -1);
        //when first and second dates are equal
        assertEquals(Data.difference(dec1, dec1), 1);
        //when the second date comes right after the first date
        assertEquals(Data.difference(dec1, dec2), 2);
        //otherwise
        assertEquals(Data.difference(dec1, dec3), 3);
    }
}