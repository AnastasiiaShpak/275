package ScheduleBuilder;
import com.company.Data;
import com.company.DayList;
import com.company.Task;
import com.company.TaskList;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleBuilderTest {
    private TaskList tList = new TaskList();
    private DayList dList = new DayList();
    private ScheduleBuilder sb = new ScheduleBuilder();
    private Date dec1 = new Date(120, Calendar.DECEMBER, 1);
    private Date dec2 = new Date(120, Calendar.DECEMBER, 2);
    private Date dec3 = new Date(120, Calendar.DECEMBER, 3);
    private Date dec4 = new Date(120, Calendar.DECEMBER, 4);
    private Date dec5 = new Date(120, Calendar.DECEMBER, 5);
    private Task t1 = new Task("A", dec1, dec2, 2, false);
    private Task t2 = new Task("B", dec3, dec4, 2, false);

    ScheduleBuilderTest(){
        tList.addTask(t1);
        tList.addTask(t2);
        Data.setTaskList(tList);
        dList.addDayOff(dec2);
        dList.addDayOff(dec4);
        Data.setDayOffList(dList);
    }

    @Test
    void setTotalHours() {
        //tested only once because total hours are set only once in schedule builder
        sb.setTotalHours();
        assertEquals(sb.totalHours, 4);
    }

    @Test
    void constructCriticalDays(){
        //corner case included - when start of a task goes right after deadline of another task
        sb.constructCriticalDays();
        assertEquals(sb.criticalDays.get(0), dec1);
        assertEquals(sb.criticalDays.get(1), dec3);
        assertEquals(sb.criticalDays.get(2), dec5);
    }

    @Test
    void constructZones() {
        //prerequisite for zones construction
        sb.setTotalHours();
        sb.duration = Data.getTaskList().getDuration();
        sb.idealHoursPerDay = sb.totalHours / (sb.duration - Data.getDayOffList().getSize());
        sb.constructCriticalDays();

        sb.constructZones();
        assertEquals(sb.zonesByDates.size(), 2);

        //check first zone
        assertEquals(sb.zonesByDates.get(0).zoneOrderInTime, 0);
        assertEquals(sb.zonesByDates.get(0).duration, 2);
        assertEquals(sb.zonesByDates.get(0).numOfDaysOff, 1);
        assertEquals(sb.zonesByDates.get(0).capacity.get("A"), 2);
        assertEquals(sb.zonesByDates.get(0).capacity.size(), 1);
        assertEquals(sb.zonesByDates.get(0).connections.size(), 0);
        assertEquals(sb.zonesByDates.get(0).imbalance, 0);
        assertEquals(sb.zonesByDates.get(0).zoneIndex, 0);

        //check last zone
        assertEquals(sb.zonesByDates.get(1).zoneOrderInTime, 1);
        assertEquals(sb.zonesByDates.get(1).duration, 2);
        assertEquals(sb.zonesByDates.get(1).numOfDaysOff, 1);
        assertEquals(sb.zonesByDates.get(1).capacity.get("B"), 2);
        assertEquals(sb.zonesByDates.get(1).capacity.size(), 1);
        assertEquals(sb.zonesByDates.get(1).connections.size(), 0);
        assertEquals(sb.zonesByDates.get(1).imbalance, 0);
        assertEquals(sb.zonesByDates.get(1).zoneIndex, 1);
    }

    @Test
    void getNumOfDaysOffInTask(){
        assertEquals(sb.getNumOfDaysOffInTask(t1), 1);
        assertEquals(sb.getNumOfDaysOffInTask(t2), 1);
    }

    @Test
    void validateInput() {
        assertTrue(sb.validateInput());
        dList.addDayOff(dec1);
        assertFalse(sb.validateInput());
        dList.removeDay(dec1);
    }

    @Test
    void insertZone() {
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 2));

        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = 5;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 2));
        z2.connections.add(new ScheduleBuilder.Connection(1, "B", 2, 2));

        ScheduleBuilder.Zone z3 = new ScheduleBuilder.Zone();
        z3.imbalance = -6;
        z3.connections = new Vector<>();
        z3.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 2));

        sb.zonesByPriority = new Vector<>();
        //add first zone
        sb.insertZone(z1, 0, -1);
        assertEquals(sb.zonesByPriority.size(), 1);
        assertEquals(sb.zonesByPriority.get(0), z1);

        //check that zone with more connections placed in th end of vector
        sb.insertZone(z2, 0, 0);
        assertEquals(sb.zonesByPriority.size(), 2);
        assertEquals(sb.zonesByPriority.get(0), z1);
        assertEquals(sb.zonesByPriority.get(1), z2);

        //check if number of connections is the same, zone with greater absolute value of imbalance is placed in front
        sb.insertZone(z3, 0, 1);
        assertEquals(sb.zonesByPriority.size(), 3);
        assertEquals(sb.zonesByPriority.get(0), z3);
        assertEquals(sb.zonesByPriority.get(1), z1);
        assertEquals(sb.zonesByPriority.get(2), z2);
    }

    @Test
    void BalanceZones(){
        tList.addTask(new Task("C", dec2, dec3, 4, false));
        sb.setTotalHours();
        sb.duration = Data.getTaskList().getDuration();
        sb.idealHoursPerDay = sb.totalHours / (sb.duration - Data.getDayOffList().getSize());
        sb.constructCriticalDays();
        sb.constructZones();
        sb.BalanceZones();
        //imbalance in each zone is 0
        assertEquals(sb.zonesByDates.get(0).imbalance, -2);
        assertEquals(sb.zonesByDates.get(1).imbalance, 0); //day off
        assertEquals(sb.zonesByDates.get(2).imbalance, 2);
        assertEquals(sb.zonesByDates.get(3).imbalance, 0); //day off

        //capacity of each zone is equal
        assertEquals(sb.zonesByDates.get(0).capacity.get("A"), 2); //the only day task A can be done
        assertEquals(sb.zonesByDates.get(1).capacity.size(), 0);
        assertEquals(sb.zonesByDates.get(2).capacity.get("C"), 4); //the only day task C can be done
        assertEquals(sb.zonesByDates.get(2).capacity.get("B"), 2); //the only day task B can be done
        assertEquals(sb.zonesByDates.get(3).capacity.size(), 0);
    }

    @Test
    void maximumTransferForOne() {
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 0;

        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = 5;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 2));
        z2.connections.add(new ScheduleBuilder.Connection(1, "B", 2, 2));

        ScheduleBuilder.Zone z3 = new ScheduleBuilder.Zone();
        z3.imbalance = -6;
        z3.connections = new Vector<>();
        z3.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 2));

        //maximum transfer for zone without connections
        sb.MaximumTransferForOne(sb.zonesByDates.get(0));
        assertEquals(sb.zonesByDates.get(0).imbalance, -2);

        sb.MaximumTransferForOne(sb.zonesByDates.get(0));
        assertEquals(sb.zonesByDates.get(0).imbalance, -2);

    }

    @Test
    void balanceTo() {
    }

    @Test
    void balanceFrom() {
    }

    @Test
    void equalizedTransfer() {
    }

    @Test
    void transfer() {
    }

    @Test
    void vectorOfConnections() {
    }

    @Test
    void constructOutput() {
    }

    @Test
    void addMonoBlock() {
    }

    @Test
    void addMixedBlock() {
    }
}