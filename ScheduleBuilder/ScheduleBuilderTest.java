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
    private Date nov30 = new Date(120, Calendar.NOVEMBER, 30);
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
    void balanceTasks(){
        //invalid input
        dList.addDayOff(dec1);
        sb.balanceTasks();
        assertFalse(Data.getBuiltDayList().successful);

        //valid input
        dList.removeDay(dec1);
        sb.balanceTasks();
        assertTrue(Data.getBuiltDayList().successful);
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
        sb.setTotalHours();
        sb.duration = Data.getTaskList().getDuration();
        sb.idealHoursPerDay = sb.totalHours / (sb.duration - Data.getDayOffList().getSize());
        sb.constructCriticalDays();
        sb.constructZones();

        //imbalance = 0
        ScheduleBuilder.Zone z0 = new ScheduleBuilder.Zone();
        z0.imbalance = 0;
        sb.zonesByDates.add(z0);

        sb.MaximumTransferForOne(z0);
        assertEquals(z0.imbalance, 0);
    }

    @Test
    void maximumTransferForOne2() {
        //c1: has only 1 connection, imbalance > 0
        //c2: has more connections, imbalance > 0
        //c3: has less connections, imbalance > 0
        //positive imbalance in zones
        dList.removeDay(dec2);
        dList.removeDay(dec4);
        tList.moveTask(t2, -1);
        Task t3 = new Task("C", dec2, dec3, 2, false);
        tList.addTask(t3);
        Task t4 = new Task("D", dec3, dec4, 2, false);
        tList.addTask(t4);
        Task t5 = new Task("E", dec3, dec4, 2, false);
        tList.addTask(t5);

        sb.setTotalHours();
        sb.duration = Data.getTaskList().getDuration();
        sb.idealHoursPerDay = sb.totalHours / (sb.duration - Data.getDayOffList().getSize());
        sb.constructCriticalDays();
        sb.constructZones();
        sb.MaximumTransferForOne(sb.zonesByDates.get(1));

        assertEquals(sb.zonesByDates.get(0).capacity.get("A"), 1);
        assertEquals(sb.zonesByDates.get(1).capacity.get("A"), 1);
        assertEquals(sb.zonesByDates.get(1).capacity.get("B"), 0.5);
        assertEquals(sb.zonesByDates.get(1).capacity.get("C"), 1);
    }

    @Test
    void maximumTransferForOne3() {
        //c1: has the same number of connections and balanced
        //positive imbalance in zones
        dList.removeDay(dec2);
        dList.removeDay(dec4);
        Task t3 = new Task("C", dec2, dec3, 2, false);
        tList.addTask(t3);
        sb.setTotalHours();
        sb.duration = Data.getTaskList().getDuration();
        sb.idealHoursPerDay = sb.totalHours / (sb.duration - Data.getDayOffList().getSize());
        sb.constructCriticalDays();
        sb.constructZones();
        sb.MaximumTransferForOne(sb.zonesByDates.get(1));

        assertEquals(sb.zonesByDates.get(0).capacity.get("A"), 1);
        assertEquals(sb.zonesByDates.get(1).capacity.get("A"), 1);
    }

    @Test
    void maximumTransferForOne4() {
        //c1: has the more connections connections imbalance is negative
        dList.removeDay(dec2);
        dList.removeDay(dec4);
        Task t3 = new Task("C", dec2, dec3, 0.5, false);
        tList.addTask(t3);
        sb.setTotalHours();
        sb.duration = Data.getTaskList().getDuration();
        sb.idealHoursPerDay = sb.totalHours / (sb.duration - Data.getDayOffList().getSize());
        sb.constructCriticalDays();
        sb.constructZones();
        sb.MaximumTransferForOne(sb.zonesByDates.get(0));

        assertEquals(sb.zonesByDates.get(0).capacity.get("A"), 1.12);
        assertEquals(sb.zonesByDates.get(1).capacity.get("A"), 0.88);
    }

    @Test
    void maximumTransferForOne5() {
        //c1: has the more connections connections imbalance is negative
        dList.removeDay(dec2);
        dList.removeDay(dec4);
        Task t3 = new Task("C", dec2, dec3, 0.5, false);
        tList.addTask(t3);
        Task t4 = new Task("D", dec1, dec2, 2, false);
        tList.addTask(t4);
        sb.setTotalHours();
        sb.duration = Data.getTaskList().getDuration();
        sb.idealHoursPerDay = sb.totalHours / (sb.duration - Data.getDayOffList().getSize());
        sb.constructCriticalDays();
        sb.constructZones();
        sb.MaximumTransferForOne(sb.zonesByDates.get(1));

        assertEquals(sb.zonesByDates.get(1).capacity.get("A"), 1);
        assertEquals(sb.zonesByDates.get(1).capacity.get("C"), 0);
        assertEquals(sb.zonesByDates.get(1).capacity.get("D"), 1);
    }

    @Test
    void balanceTo() {
        //when zone "to" has positive imbalance
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 10));
        z1.capacity.put("A", 10.0);
        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -2, 10));
        z2.capacity.put("A", 10.0);
        sb.BalanceTo(z1, z2, "A");
        assertEquals(z1.imbalance, 5);
        assertEquals(z2.imbalance, 7); //z2 now contains all imbalance
        assertEquals(z1.capacity.get("A"), 10);
        assertEquals(z2.capacity.get("A"), 10);

        //when zone "to" has negative imbalance and capacity in "from" is not enough to cover it
        z1.imbalance = -5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 13, 2));
        z1.capacity.put("A", 2.0);
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -13, 2));
        z2.capacity.put("A", 2.0);
        sb.BalanceTo(z1, z2, "A");
        assertEquals(z1.imbalance, -3);
        assertEquals(z2.imbalance, 5);
        assertEquals(z1.capacity.get("A"), 4);
        assertEquals(z2.capacity.get("A"), 0);

        //when zone "to" has negative imbalance and capacity in "from" covers imbalance
        z1.imbalance = -5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 13, 7));
        z1.capacity.put("A", 2.0);
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -13, 2));
        z2.capacity.put("A", 7.0);
        sb.BalanceTo(z1, z2, "A");
        assertEquals(z1.imbalance, 0);
        assertEquals(z2.imbalance, 2);
        assertEquals(z1.capacity.get("A"), 7);
        assertEquals(z2.capacity.get("A"), 2);

        //when "from" capacity is not positive
        z1.imbalance = -5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 13, 0));
        z1.capacity.put("A", 2.0);
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -13, 2));
        z2.capacity.put("A", 0.0);
        sb.BalanceTo(z1, z2, "A");
        assertEquals(z1.imbalance, -5);
        assertEquals(z2.imbalance, 7);
        assertEquals(z1.capacity.get("A"), 2);
        assertEquals(z2.capacity.get("A"), 0);

        //when "to" imbalance is zero
        z1.imbalance = 0;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 13, 0));
        z1.capacity.put("A", 2.0);
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -13, 2));
        z2.capacity.put("A", 2.0);
        sb.BalanceTo(z1, z2, "A");
        assertEquals(z1.imbalance, 0);
        assertEquals(z2.imbalance, 7);
        assertEquals(z1.capacity.get("A"), 2);
        assertEquals(z2.capacity.get("A"), 2);
    }

    @Test
    void balanceFrom() {
        //when zone "from" has negative imbalance
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 10));
        z1.capacity.put("A", 10.0);
        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = -7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -2, 10));
        z2.capacity.put("A", 10.0);
        sb.BalanceFrom(z1, z2, "A");
        assertEquals(z1.imbalance, 5);
        assertEquals(z2.imbalance, -7); //z2 now contains all imbalance
        assertEquals(z1.capacity.get("A"), 10);
        assertEquals(z2.capacity.get("A"), 10);

        //when zone "from" has positive imbalance and capacity in "from" is not enough to cover it
        z1.imbalance = -5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 13, 2));
        z1.capacity.put("A", 2.0);
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -13, 2));
        z2.capacity.put("A", 2.0);
        sb.BalanceTo(z1, z2, "A");
        assertEquals(z1.imbalance, -3);
        assertEquals(z2.imbalance, 5);
        assertEquals(z1.capacity.get("A"), 4);
        assertEquals(z2.capacity.get("A"), 0);

        //when zone "from" has positive imbalance and capacity in "from" covers imbalance
        z1.imbalance = -5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 13, 7));
        z1.capacity.put("A", 2.0);
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -13, 2));
        z2.capacity.put("A", 7.0);
        sb.BalanceFrom(z1, z2, "A");
        assertEquals(z1.imbalance, 2);
        assertEquals(z2.imbalance, 0);
        assertEquals(z1.capacity.get("A"), 9);
        assertEquals(z2.capacity.get("A"), 0);

        //when "from" imbalance is 0
        z1.imbalance = -5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 13, 7));
        z1.capacity.put("A", 2.0);
        z2.imbalance = 0;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -13, 2));
        z2.capacity.put("A", 7.0);
        sb.BalanceFrom(z1, z2, "A");
        assertEquals(z1.imbalance, -5);
        assertEquals(z2.imbalance, 0);
        assertEquals(z1.capacity.get("A"), 2);
        assertEquals(z2.capacity.get("A"), 7);

        //when "from" capacity is 0
        z1.imbalance = -5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 13, 7));
        z1.capacity.put("A", 2.0);
        z2.imbalance = 6;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -13, 2));
        z2.capacity.put("A", 0.0);
        sb.BalanceFrom(z1, z2, "A");
        assertEquals(z1.imbalance, -5);
        assertEquals(z2.imbalance, 6);
        assertEquals(z1.capacity.get("A"), 2);
        assertEquals(z2.capacity.get("A"), 0);
    }

    @Test
    void equalizedTransfer() {
        //when "from" capacity is 0
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 0));
        z1.capacity.put("A", 2.0);
        sb.zonesByDates.add(z1);
        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(0, "A", -2, 2));
        z2.capacity.put("A", 0.0);
        sb.zonesByDates.add(z2);
        sb.EqualizedTransfer(z1, z2, "A");
        assertEquals(z1.imbalance, 5);
        assertEquals(z2.imbalance, 7); //z2 now contains all imbalance
        assertEquals(z1.capacity.get("A"), 2);
        assertEquals(z2.capacity.get("A"), 0);

        //when capacity in "from" zone is enough to make imbalance equal in 2 zones
        z1.imbalance = 5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 10));
        z1.capacity.put("A", 10.0);
        sb.zonesByDates.add(z1);
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(0, "A", -2, 10));
        z2.capacity.put("A", 10.0);
        sb.zonesByDates.add(z2);
        sb.EqualizedTransfer(z1, z2, "A");
        assertEquals(z1.imbalance, 6);
        assertEquals(z2.imbalance, 6); //z2 now contains all imbalance
        assertEquals(z1.capacity.get("A"), 11);
        assertEquals(z2.capacity.get("A"), 9);

        //when capacity in "from" zone is not enough to make imbalance equal in 2 zones
        z1.imbalance = 3;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 6, 2));
        z1.capacity.put("A", 2.0);
        sb.zonesByDates.add(z1);
        z2.imbalance = 9;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(0, "A", -6, 2));
        z2.capacity.put("A", 2.0);
        sb.zonesByDates.add(z2);
        sb.EqualizedTransfer(z1, z2, "A");
        assertEquals(z1.imbalance, 5);
        assertEquals(z2.imbalance, 7);
        assertEquals(z1.capacity.get("A"), 4);
        assertEquals(z2.capacity.get("A"), 0);
    }

    @Test
    void ShareImbalanceBetweenConnections(){
       //zone to balance
        //has 2 connections
        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = 2;
        z2.zoneOrderInTime = 1;
        z2.zoneIndex = 2;
        z2. duration = 1;
        z2.capacity.put("A", 1.0);
        z2.capacity.put("B", 1.0);
        z2.capacity.put("N", 0.0);
        sb.zonesByDates.add(z2);

        //connection 1: does not require transfer
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 2;
        z1.zoneOrderInTime = 0;
        z1.zoneIndex = 0;
        z1.duration = 2;
        z1.capacity.put("A", 3.0);
        z1.capacity.put("N", 0.0);
        sb.zonesByDates.add(z1);

        //connection 2: requires transfer
        //capacity is enough to transfer
        ScheduleBuilder.Zone z3 = new ScheduleBuilder.Zone();
        z3.imbalance = 2;
        z3.zoneIndex = 1;
        z3.zoneOrderInTime = 2;
        z3.capacity.put("B", 3.0);
        z3.duration = 3;
        sb.zonesByDates.add(z3);

        ScheduleBuilder.Zone z4 = new ScheduleBuilder.Zone();
        z4.imbalance = 0;
        z4.zoneIndex = 1;
        z4.zoneOrderInTime = 2;
        z4.capacity.put("Z", 3.0);
        z4.duration = 3;
        sb.zonesByDates.add(z4);

        z1.connections = sb.vectorOfConnections(z1);
        z2.connections = sb.vectorOfConnections(z2);
        z3.connections = sb.vectorOfConnections(z3);

        sb.ShareImbalanceBetweenConnections(z2);
        assertEquals(z2.imbalance, 1);
        assertEquals(z1.imbalance, 2);
        assertEquals(z3.imbalance, 3);

        //what imbalance is0
        sb.ShareImbalanceBetweenConnections(z4);
        assertEquals(z4.imbalance, 0);
    }

    @Test
    void ShareImbalanceBetweenConnections2(){
        //zone to balance
        //has 2 connections
        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = 4;
        z2.zoneOrderInTime = 1;
        z2.zoneIndex = 2;
        z2. duration = 1;
        z2.capacity.put("A", 0.0);
        z2.capacity.put("B", 1.0);
        sb.zonesByDates.add(z2);

        //connection 1: requires transfer
        //capacity is 0
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 1;
        z1.zoneOrderInTime = 0;
        z1.zoneIndex = 0;
        z1.duration = 2;
        z1.capacity.put("A", 3.0);
        sb.zonesByDates.add(z1);

        //connection 2: requires transfer
        //capacity is not enough to balance
        ScheduleBuilder.Zone z3 = new ScheduleBuilder.Zone();
        z3.imbalance = 2;
        z3.zoneIndex = 1;
        z3.zoneOrderInTime = 2;
        z3.capacity.put("B", 3.0);
        z3.duration = 4;
        sb.zonesByDates.add(z3);

        z1.connections = sb.vectorOfConnections(z1);
        z2.connections = sb.vectorOfConnections(z2);
        z3.connections = sb.vectorOfConnections(z3);

        sb.ShareImbalanceBetweenConnections(z2);
        assertEquals(z2.imbalance, 3);
        assertEquals(z1.imbalance, 1);
        assertEquals(z3.imbalance, 3);
    }

    @Test
    void ShareImbalanceBetweenConnections3(){
        //zone to balance
        //has 2 connections
        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = -4;
        z2.zoneOrderInTime = 1;
        z2.zoneIndex = 2;
        z2. duration = 1;
        z2.capacity.put("A", 0.0);
        z2.capacity.put("B", 1.0);
        sb.zonesByDates.add(z2);

        //connection 1: requires transfer
        //capacity is 0
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = -1;
        z1.zoneOrderInTime = 0;
        z1.zoneIndex = 0;
        z1.duration = 2;
        z1.capacity.put("A", 0.0);
        sb.zonesByDates.add(z1);

        //connection 2: requires transfer
        //capacity is not enough to balance
        ScheduleBuilder.Zone z3 = new ScheduleBuilder.Zone();
        z3.imbalance = -2;
        z3.zoneIndex = 1;
        z3.zoneOrderInTime = 2;
        z3.capacity.put("B", 1.0);
        z3.duration = 4;
        sb.zonesByDates.add(z3);

        z1.connections = sb.vectorOfConnections(z1);
        z2.connections = sb.vectorOfConnections(z2);
        z3.connections = sb.vectorOfConnections(z3);

        sb.ShareImbalanceBetweenConnections(z2);
        assertEquals(z2.imbalance, -3);
        assertEquals(z1.imbalance, -1);
        assertEquals(z3.imbalance, -3);
    }

    @Test
    void transfer(){
        //no corner cases
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 5;
        z1.connections = new Vector<>();
        z1.connections.add(new ScheduleBuilder.Connection(1, "A", 2, 10));
        z1.capacity.put("A", 10.0);
        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = 7;
        z2.connections = new Vector<>();
        z2.connections.add(new ScheduleBuilder.Connection(1, "A", -2, 10));
        z2.capacity.put("A", 10.0);
        sb.Transfer(z1, z2, "A",8.0);
        assertEquals(z1.imbalance, 13);
        assertEquals(z2.imbalance, -1); //z2 now contains all imbalance
        assertEquals(z1.capacity.get("A"), 18);
        assertEquals(z2.capacity.get("A"), 2);
    }

    @Test
    void vectorOfConnections() {
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.imbalance = 5;
        z1.zoneOrderInTime = 0;
        z1.zoneIndex = 0;
        z1.capacity.put("A", 3.0);
        z1.capacity.put("B", 2.0);
        sb.zonesByDates.add(z1);

        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.imbalance = -3;
        z2.zoneOrderInTime = 1;
        z2.zoneIndex = 2;
        z2.capacity.put("B", 1.0);
        z2.capacity.put("C", 1.0);
        sb.zonesByDates.add(z2);

        ScheduleBuilder.Zone z3 = new ScheduleBuilder.Zone();
        z3.imbalance = -2;
        z3.zoneIndex = 1;
        z3.zoneOrderInTime = 2;
        z3.capacity.put("C", 3.0);
        sb.zonesByDates.add(z3);

        ScheduleBuilder.Zone z4 = new ScheduleBuilder.Zone();
        z4.imbalance = 0;
        z4.zoneIndex = 3;
        z4.zoneOrderInTime = 3;
        z4.capacity.put("D", 3.0);
        z4.capacity.put("C", 3.0);
        sb.zonesByDates.add(z4);

        ScheduleBuilder.Zone z5 = new ScheduleBuilder.Zone();
        z5.imbalance = -2;
        z5.zoneIndex = 3;
        z5.zoneOrderInTime = 4;
        z5.capacity.put("D", 4.0);
        sb.zonesByDates.add(z5);

        ScheduleBuilder.Zone z6 = new ScheduleBuilder.Zone();
        z6.imbalance = 0;
        z6.zoneIndex = 5;
        z6.zoneOrderInTime = 5;
        z6.capacity.put("E", 4.0);
        sb.zonesByDates.add(z6);

        // zone with 1 connection
        z1.connections = sb.vectorOfConnections(z1);
        assertEquals(z1.connections.size(), 1);
        assertEquals(z1.connections.get(0).taskName, "B");
        assertEquals(z1.connections.get(0).imbalanceDifference, 8);
        assertEquals(z1.connections.get(0).taskCapacity, 1.0);
        assertEquals(z1.connections.get(0).dateIndex, 1);

        //zone with multiple connections
        //connections are prioritized by imbalance
        //task name, imbalance difference, task capacity and index are set correctly
        z2.connections = sb.vectorOfConnections(z2);
        assertEquals(z2.connections.size(), 3);
        assertEquals(z2.connections.get(0).taskName, "B");
        assertEquals(z2.connections.get(0).imbalanceDifference, 8);
        assertEquals(z2.connections.get(0).taskCapacity, 2.0);
        assertEquals(z2.connections.get(0).dateIndex, 0);

        //zone with multiple connections
        //connections are sorted by imbalance
        //if imbalance is equal, connections are sorted by task capacity
        z4.connections = sb.vectorOfConnections(z4);
        assertEquals(z4.connections.size(), 3);
        assertEquals(z4.connections.get(0).taskName, "C");
        assertEquals(z4.connections.get(0).dateIndex, 1);

        assertEquals(z4.connections.get(1).taskName, "D");
        assertEquals(z4.connections.get(1).dateIndex, 4);

        assertEquals(z4.connections.get(2).taskName, "C");
        assertEquals(z4.connections.get(2).dateIndex, 2);

        //zone with 0 connections
        z4.connections = sb.vectorOfConnections(z5);
        assertNull(z5.connections);
    }

    @Test
    void constructOutput() {
        tList.removeTask(t2);
        dList.removeDay(dec4);
        dList.removeDay(dec2);
        Task t3 = new Task("B", dec1, dec2, 1, true);
        tList.addTask(t3);
        dList.addDayOff(dec3);
        Task t4 = new Task("C", dec4, dec5, 1, true);
        tList.addTask(t4);
        Task t5 = new Task("D", dec4, dec5, 0, true);
        tList.addTask(t5);
        dList.addDayOff(dec5);

        //capacity of one task is 0, day off is last day
        //zone 1: both prioritized and non prioritized tasks are present
        //zone 2: empty zone
        //zone 3: only one type of task, one task has 0 capacity
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.zoneOrderInTime = 0;
        z1.numOfDaysOff = 0;
        z1.zoneIndex = 0;
        z1.duration = 2;
        z1.capacity.put("A", 2.0);
        z1.capacity.put("B", 1.0);
        sb.zonesByDates.add(z1);

        ScheduleBuilder.Zone z2 = new ScheduleBuilder.Zone();
        z2.zoneOrderInTime = 1;
        z2.numOfDaysOff = 1;
        z2.zoneIndex = 2;
        z2.duration = 1;
        sb.zonesByDates.add(z2);

        ScheduleBuilder.Zone z3 = new ScheduleBuilder.Zone();
        z3.zoneOrderInTime = 2;
        z3.numOfDaysOff = 1;
        z3.zoneIndex = 1;
        z3.duration = 2;
        z3.capacity.put("C", 1.0);
        z3.capacity.put("D", 0.0);
        sb.zonesByDates.add(z3);

        sb.constructOutput();
        //check result of every day
        //day 1: 2 tasks: prioritized and non prioritized
        assertEquals(sb.bdList.getBuiltDays().size(), 5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getTotalHours(), 1.5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getSize(), 2);
        assertEquals(sb.bdList.getBuiltDays().get(0).getDate(), dec1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getName(), "B");
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getPercentage(), 100);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getColor(), t3.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getHours(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getName(), "A");
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getPercentage(), 25);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getColor(), t1.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getHours(), 0.5);
        //day 2: 1 non prioritized task
        assertEquals(sb.bdList.getBuiltDays().get(1).getTotalHours(), 1.5);
        assertEquals(sb.bdList.getBuiltDays().get(1).getSize(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(1).getDate(), dec2);
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getName(), "A");
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getPercentage(), 75);
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getColor(), t1.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getHours(), 1.5);
        //day 3: day off covers the while zone
        assertEquals(sb.bdList.getBuiltDays().get(2).getTotalHours(), 0);
        assertEquals(sb.bdList.getBuiltDays().get(2).getSize(), 0);
        assertEquals(sb.bdList.getBuiltDays().get(2).getDate(), dec3);
        assertEquals(sb.bdList.getBuiltDays().get(2).getBuiltTasks().size(), 0);
        //day 4: 1 prioritized task
        assertEquals(sb.bdList.getBuiltDays().get(3).getTotalHours(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(3).getSize(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(3).getDate(), dec4);
        assertEquals(sb.bdList.getBuiltDays().get(3).getBuiltTasks().get(0).getName(), "C");
        assertEquals(sb.bdList.getBuiltDays().get(3).getBuiltTasks().get(0).getPercentage(), 100);
        assertEquals(sb.bdList.getBuiltDays().get(3).getBuiltTasks().get(0).getColor(), t4.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(3).getBuiltTasks().get(0).getHours(), 1);
        //day 5: day off is in the end of zone

        assertEquals(sb.bdList.getBuiltDays().get(4).getTotalHours(), 0);
        assertEquals(sb.bdList.getBuiltDays().get(4).getSize(), 0);
        assertEquals(sb.bdList.getBuiltDays().get(4).getDate(), dec5);
        assertEquals(sb.bdList.getBuiltDays().get(4).getBuiltTasks().size(), 0);


    }

    @Test
    void addMonoBlock(){
        //prioritized block, zone contains both prioritized and non prioritized tasks
        Task t3 = new Task("C", dec1, dec2, 2, true);
        tList.addTask(t3);
        dList.removeDay(dec2);
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.zoneOrderInTime = 0;
        z1.numOfDaysOff = 0;
        z1.zoneIndex = 2;
        z1.duration = 2;
        z1.capacity.put("A", 1.0);
        z1.capacity.put("C", 2.0);
        sb.zonesByDates.add(z1);
        sb.AddMonoBlock(z1, 1, 2, 1.5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getDate(), dec1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getTotalHours(), 1.5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getSize(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getHours(), 1.5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getColor(), t3.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getName(), "C");
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getPercentage(), 75);
    }

    @Test
    void addMonoBlock2(){
        //multiple tasks to add, one day off in a block, non prioritized tasks
        Task t3 = new Task("C", dec1, dec3, 2, false);
        tList.addTask(t3);
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.zoneOrderInTime = 0;
        z1.numOfDaysOff = 1;
        z1.zoneIndex = 2;
        z1.duration = 2;
        z1.capacity.put("A", 1.0);
        z1.capacity.put("C", 2.0);
        sb.zonesByDates.add(z1);
        sb.AddMonoBlock(z1, -1, 3, 1.5);
        assertEquals(sb.bdList.getBuiltDays().size(), 3);
        assertEquals(sb.bdList.getBuiltDays().get(0).getDate(), dec1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getTotalHours(), 1.5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getSize(), 2);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getHours(), 0.5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getColor(), t1.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getName(), "A");
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getPercentage(), 25);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getHours(), 1.0);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getColor(), t3.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getName(), "C");
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getPercentage(), 50);
    }

    @Test
    void addMonoBlock3(){
        //task to add has 0 capacity
        Task t3 = new Task("C", dec1, dec2, 2, true);
        tList.addTask(t3);
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.zoneOrderInTime = 0;
        z1.numOfDaysOff = 1;
        z1.duration = 2;
        sb.zonesByDates.add(z1);
        sb.AddMonoBlock(z1, 1, 0, 1.5);
        assertEquals(sb.bdList.getBuiltDays().size(), 0);
    }

    @Test
    void addMonoBlock4(){
        //multiple tasks to add, task type is 0, one task capacity is 0, no days off
        Task t3 = new Task("C", dec1, dec3, 3, true);
        tList.addTask(t3);
        Task t4 = new Task("D", dec1, dec3, 0, true);
        tList.addTask(t4);
        dList.removeDay(dec2);
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.zoneOrderInTime = 0;
        z1.numOfDaysOff = 0;
        z1.zoneIndex = 2;
        z1.duration = 3;
        z1.capacity.put("C", 3.0);
        z1.capacity.put("D", 0.0);
        sb.zonesByDates.add(z1);
        sb.AddMonoBlock(z1, 0, 3, 1);
        assertEquals(sb.bdList.getBuiltDays().size(), 3);
        assertEquals(sb.bdList.getBuiltDays().get(0).getDate(), dec1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getTotalHours(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getSize(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getHours(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getColor(), t3.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getName(), "C");

        assertEquals(sb.bdList.getBuiltDays().get(1).getDate(), dec2);
        assertEquals(sb.bdList.getBuiltDays().get(1).getTotalHours(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(1).getSize(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getHours(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getColor(), t3.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getName(), "C");
    }

    @Test
    void addMixedBlock() {
        //no days off, both tasks capacity is positive
        Task t3 = new Task("C", dec1, dec3, 1, true);
        tList.addTask(t3);
        dList.removeDay(dec2);
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.zoneOrderInTime = 0;
        z1.numOfDaysOff = 1;
        z1.zoneIndex = 2;
        z1.duration = 2;
        z1.capacity.put("A", 2.0);
        z1.capacity.put("C", 1.0);
        sb.zonesByDates.add(z1);
        sb.AddMixedBlock(z1, 1.5, 1, 2);
        assertEquals(sb.bdList.getBuiltDays().size(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getDate(), dec1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getTotalHours(), 1.5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getSize(), 2);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getHours(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getColor(), t3.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getName(), "C");
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(0).getPercentage(), 100);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getHours(), 0.5);
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getColor(), t1.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getName(), "A");
        assertEquals(sb.bdList.getBuiltDays().get(0).getBuiltTasks().get(1).getPercentage(), 25);
    }

    @Test
    void addMixedBlock2() {
        //days off is present, one task has 0 capacity
        Task t3 = new Task("C", dec1, dec3, 0, true);
        tList.addTask(t3);
        dList.removeDay(dec2);
        dList.addDayOff(dec1);
        ScheduleBuilder.Zone z1 = new ScheduleBuilder.Zone();
        z1.zoneOrderInTime = 0;
        z1.numOfDaysOff = 1;
        z1.zoneIndex = 2;
        z1.duration = 2;
        z1.capacity.put("A", 2.0);
        z1.capacity.put("C", 0.0);
        sb.zonesByDates.add(z1);
        sb.AddMixedBlock(z1, 2, 0, 2);
        assertEquals(sb.bdList.getBuiltDays().size(), 2);
        assertEquals(sb.bdList.getBuiltDays().get(0).getDate(), dec1);
        assertEquals(sb.bdList.getBuiltDays().get(0).getTotalHours(), 0);
        assertEquals(sb.bdList.getBuiltDays().get(0).getSize(), 0);

        assertEquals(sb.bdList.getBuiltDays().get(1).getDate(), dec2);
        assertEquals(sb.bdList.getBuiltDays().get(1).getTotalHours(), 2);
        assertEquals(sb.bdList.getBuiltDays().get(1).getSize(), 1);
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getHours(), 2);
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getColor(), t1.getColor());
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getName(), "A");
        assertEquals(sb.bdList.getBuiltDays().get(1).getBuiltTasks().get(0).getPercentage(), 100);




    }
}

