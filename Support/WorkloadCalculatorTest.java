/*
Testing for workload calculator
Simple getters and setters are not covered
assignNameToLevel() is trivial and was not covered

CMPT275 Project
Group 21
 */

package Support;
import com.company.BuiltDay;
import com.company.BuiltDayList;
import com.company.Data;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class WorkloadCalculatorTest {
    BuiltDayList bdList = new BuiltDayList();
    Date dec1 = new Date(120, Calendar.DECEMBER, 1);
    Date dec2 = new Date(120, Calendar.DECEMBER, 2);

    WorkloadCalculatorTest(){
        Data.setBuiltDaysList(bdList);
    }

    @Test
    void getWorkload() {

        //case0: built days are empty
        assertEquals(WorkloadCalculator.getWorkload(), "");

        //case1: mono schedule: only one difficulty level throughout the whole schedule
        BuiltDay d1 = new BuiltDay(dec1);
        d1.setTotalHours(2);
        bdList.addBDay(d1);
        assertEquals(WorkloadCalculator.getWorkload(), "Workload: light");

        //case2:  half - half schedule
        d1.setTotalHours(10);
        BuiltDay d2 = new BuiltDay(dec2);
        d2.setTotalHours(2);
        BuiltDay d3 = new BuiltDay(dec2);
        d3.setTotalHours(10);
        bdList.addBDay(d3);
        bdList.addBDay(d2);
        assertEquals(WorkloadCalculator.getWorkload(), "Workload: ridiculous schedule with a few light days");

        //case3: no hard days and above
        d3.setTotalHours(0);
        d1.setTotalHours(2);
        bdList.getBuiltDays().remove(d2);
        d2.setTotalHours(5);
        bdList.addBDay(d2);

        assertEquals(WorkloadCalculator.getWorkload(), "Workload: not too hard");

        //case4: no normal days and below
        d1.setTotalHours(8);
        d2.setTotalHours(12);
        d3.setTotalHours(25); //corner case: above 24
        assertEquals(WorkloadCalculator.getWorkload(), "Workload: not easy at all");

        //case5: variety of days
        //covers only one case because the rest are trivial
        BuiltDay d4 = new BuiltDay(dec2);
        d4.setTotalHours(0);
        bdList.addBDay(d4);
        BuiltDay d5 = new BuiltDay(dec2);
        d5.setTotalHours(5);
        bdList.addBDay(d5);
        BuiltDay d6 = new BuiltDay(dec2);
        d6.setTotalHours(8);
        bdList.addBDay(d6);
        assertEquals(WorkloadCalculator.getWorkload(), "Workload: Easy-peasy on average, but big variety in days");

        //case6: 90% mono block, set days to hard (8 hours is corner case for hard days)
        d1.setTotalHours(8);
        d2.setTotalHours(8);
        d3.setTotalHours(8);
        d4.setTotalHours(8);
        d5.setTotalHours(8);
        d6.setTotalHours(8);
        BuiltDay d7 = new BuiltDay(dec2);
        d7.setTotalHours(8);
        bdList.addBDay(d7);
        BuiltDay d8 = new BuiltDay(dec2);
        d8.setTotalHours(8);
        bdList.addBDay(d8);
        BuiltDay d9 = new BuiltDay(dec2);
        d9.setTotalHours(8);
        bdList.addBDay(d9);
        BuiltDay d10 = new BuiltDay(dec2);
        d10.setTotalHours(0);
        bdList.addBDay(d10);
        assertEquals(WorkloadCalculator.getWorkload(), "Workload: mostly hard");
    }
}