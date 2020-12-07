/*
Schedule Builder
Takes TaskList as input and calculates a schedule where number of working hours is equal every day (or as close to equal as possible
Takes into account days off from DaysList on Data class and priorities of tasks
Saves result to BuiltDaysList in Data class

CMPT275 Project
Group 21
 */
package ScheduleBuilder;
import com.company.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ScheduleBuilder {
    double totalHours;
    double idealHoursPerDay;
    int duration;
    Vector<Zone> zonesByPriority = new Vector<>();
    Vector<Zone> zonesByDates = new Vector<>();
    Vector<Date> criticalDays = new Vector<>();
    BuiltDayList bdList = new BuiltDayList();

    //constructor
    public ScheduleBuilder() {}

    public void balanceTasks(){
        if (!validateInput()) {
            Data.getBuiltDayList().successful = false;
            return;
        }
        setTotalHours();
        this.duration = Data.getTaskList().getDuration();
        idealHoursPerDay = totalHours / (duration - Data.getDayOffList().getSize());
        constructCriticalDays();
        constructZones();
        BalanceZones();
        // printZones();
        constructOutput();
    }

    //represents period od time between any start date and deadline
    //all days in one zone have the same tasks and work load
    static class Zone {
        int zoneOrderInTime;
        int duration;
        int numOfDaysOff = 0;
        int zoneIndex;
        double imbalance;


        Vector<Connection> connections;
        Hashtable<String, Double> capacity = new Hashtable<>();

        Zone() {
        }
    }

    //connection is an attribute of zone
    //list of tasks that other zones share with this zone and their capacities
    static class Connection {
        int zoneIndex;
        int dateIndex;
        String taskName;
        double imbalanceDifference;
        double taskCapacity;

        Connection(int dateIndex, String taskName, double imbalanceDifference, double taskCapacity) {
            this.dateIndex = dateIndex;
            this.taskName = taskName;
            this.imbalanceDifference = imbalanceDifference;
            this.taskCapacity = taskCapacity;
        }
    }

    //add hours of all tasks
    void setTotalHours() {
        for (int i = 0; i < Data.getTaskList().getSize(); i++) {
            totalHours += Data.getTaskList().getTasks().get(i).getHours();
        }
    }

    void constructCriticalDays(){
        for(int i = 0; i < Data.getTaskList().getSize(); i++){
            addCriticalDate(Data.getTaskList().getTasks().get(i).getStart());
            addCriticalDate(new Date(Data.getTaskList().getTasks().get(i).getDeadline().getTime() + TimeUnit.DAYS.toMillis(1))); //add day after deadline
        }
    }

    void addCriticalDate(Date d){
        if(criticalDays.size() == 0){
            criticalDays.add(d);
        }else{
            if(!criticalDays.contains(d)){
                if(criticalDays.get(criticalDays.size() -1).compareTo(d) <= 0){
                    criticalDays.add(d);
                }else {
                    for (int i = 0; i < criticalDays.size(); i++) {
                        if (d.compareTo(criticalDays.get(i)) < 0) {
                            criticalDays.insertElementAt(d, i);
                            break;
                        }
                    }
                }
            }
        }
    }

    //create zones between start dates and deadlines
    //share hours for each task equally between days where task is present
    //calculate imbalance of each zone from how far it is from ideal
    //takes into account days off from DaysList in Data class
    void constructZones() {
        Date current;
        Zone zone;
        //new
        for (int i = 0; i < criticalDays.size() - 1; i++) { //for each critical date
            zone = new Zone(); //make zone
            zone.zoneOrderInTime = i; //set order

            Date start = criticalDays.get(i);
            Date deadline = criticalDays.get(i + 1);

            //calculate number of days off in the zone
            for (int j = 0; j < Data.getDayOffList().getSize(); j++) {
                Date limited = Data.getDayOffList().getDays().get(j);
                if (limited.compareTo(start) >= 0 && limited.compareTo(deadline) < 0)
                    zone.numOfDaysOff++;
            }

            //zone duration
            current = criticalDays.get(i);
            zone.duration = Data.difference(start, deadline) - 1;

            if (zone.numOfDaysOff == zone.duration) {
                zonesByDates.add(zone);
                zone.imbalance = 0;
                zone.zoneIndex = -1;
                continue;
            }

            //set capacity of each task
            double totalAvgHours = 0;
            double avgHours;
            for (int j = 0; j < Data.getTaskList().getSize(); j++) {
                if (Data.getTaskList().getTasks().get(j).getStart().compareTo(current) <= 0 && Data.getTaskList().getTasks().get(j).getDeadline().compareTo(current) >= 0) {
                    int daysOffCount = getNumOfDaysOffInTask(Data.getTaskList().getTasks().get(j));
                    avgHours = Data.getTaskList().getTasks().get(j).getHours() / (Data.getTaskList().getTasks().get(j).getDuration() - daysOffCount);
                    zone.capacity.put(Data.getTaskList().getTasks().get(j).getName(), avgHours * (zone.duration - zone.numOfDaysOff));
                    totalAvgHours += avgHours;
                }
            }
            BigDecimal im = new BigDecimal((totalAvgHours - idealHoursPerDay) * (zone.duration - zone.numOfDaysOff)).setScale(2, RoundingMode.HALF_EVEN);
            zone.imbalance = im.doubleValue();
            //insert
            zonesByDates.add(zone);
        }


        for (Zone z : zonesByDates) {
            if (z.numOfDaysOff == z.duration) {
                z.zoneIndex = -1;
                continue;
            }

            z.connections = vectorOfConnections(z);
            //construct list of zones sorted by priority
            insertZone(z, 0, zonesByPriority.size() - 1);
        }

        //set index values in connections
        for (Zone z : zonesByPriority) {
            if (z.numOfDaysOff == z.duration)
                continue;
            for (Connection c : z.connections)
                c.zoneIndex = zonesByDates.get(c.dateIndex).zoneIndex;
        }

        //set indexes in prioritizes zones vector
        for (int i = 0; i < zonesByPriority.size(); i++) {
            zonesByPriority.get(i).zoneIndex = i;
        }
    }

    int getNumOfDaysOffInTask(Task t) {
        int numOdDaysOff = 0;
        for (int i = 0; i < t.getDuration(); i++)
            if (Data.getDayOffList().getDays().contains(new Date(t.getStart().getTime() + TimeUnit.DAYS.toMillis(i)))) {
                numOdDaysOff++;
            }
        return numOdDaysOff;
    }

    //if days off are cover fully any task return false
    //otherwise return true
    boolean validateInput() {
        //go through every task
        for (int i = 0; i < Data.getTaskList().getSize(); i++) {
            int duration = Data.difference(Data.getTaskList().getTasks().get(i).getStart(), Data.getTaskList().getTasks().get(i).getDeadline());
            Date currentDate = new Date(Data.getTaskList().getTasks().get(i).getStart().getTime());
            //go through every day that has current task
            boolean possible = false;
            for (int j = 0; j < duration; j++) {
                if (!Data.getDayOffList().getDays().contains(currentDate)) {
                    possible = true;
                    break;
                }
                currentDate.setTime(currentDate.getTime() + TimeUnit.DAYS.toMillis((1)));
            }
            if (!possible) {
                return false;
            }
        }
        return true;
    }

    //insert zones to zonesByPriority list
    //zones with less number of connections are in the beginning of the list
    //if zones have equal number of connection, they are prioritized by imbalance
    void insertZone(Zone zone, int start, int end) {
        if (start == end) {
            if (zone.connections.size() < zonesByPriority.get(start).connections.size()) { //insert if number of connections is smaller in zone to insert
                zonesByPriority.insertElementAt(zone, start);
            } else if (zone.connections.size() == zonesByPriority.get(start).connections.size()) {//if number of connections is the same in both zones
                if (Math.abs(zone.imbalance) > Math.abs(zonesByPriority.get(start).imbalance)) { //sort by imbalance
                    zonesByPriority.insertElementAt(zone, start);
                } else {
                    zonesByPriority.insertElementAt(zone, start + 1);
                }
            } else {
                zonesByPriority.add(zone);
            }

        } else if (start > end) {
            zonesByPriority.insertElementAt(zone, start);
        } else {
            int mid = (start + end) / 2;

            if (zone.connections.size() == zonesByPriority.get(mid).connections.size()) { // if middle zone has the same number of connections...
                if (Math.abs(zone.imbalance) == Math.abs(zonesByPriority.get(mid).imbalance))//...and imbalance - insert
                    zonesByPriority.insertElementAt(zone, mid);
                else if (Math.abs(zone.imbalance) > Math.abs(zonesByPriority.get(mid).imbalance))
                    insertZone(zone, start, mid);
                else
                    insertZone(zone, mid + 1, end);
            } else if (zone.connections.size() < zonesByPriority.get(mid).connections.size()) {
                insertZone(zone, start, mid);
            } else {
                insertZone(zone, mid + 1, end);
            }
        }
    }

    void BalanceZones() {
        for (Zone zone : zonesByPriority) {
            MaximumTransferForOne(zone);
        }
        for (int i = zonesByPriority.size() - 1; i >= 0; i--) {
            ShareImbalanceBetweenConnections(zonesByPriority.get(i));
        }
    }

    //transfer hours from and to the zone through connections to bring imbalance as close to 0 as possible
    //does not increase imbalance of zones with less number of connections
    void MaximumTransferForOne(Zone zoneToBalance) {
        //if already in balance
        if (zoneToBalance.imbalance == 0) {
            return;
        }

        double initialImbalance = zoneToBalance.imbalance;
        for (Connection connection : zoneToBalance.connections) {
            if (zoneToBalance.imbalance == 0)
                break;

            //if connection has only one connection, skip
            if (zonesByDates.get(connection.dateIndex).connections.size() == 1) {
                continue;
            }

            //if the same number of connection, equalizing transfer
            if (zonesByDates.get(connection.dateIndex).connections.size() == zoneToBalance.connections.size()) {
                if (initialImbalance > 0) {
                    EqualizedTransfer(zonesByDates.get(connection.dateIndex), zoneToBalance, connection.taskName);
                } else
                    EqualizedTransfer(zoneToBalance, zonesByDates.get(connection.dateIndex), connection.taskName);
            }

            //if connection has more connections, balance current
            if (zonesByDates.get(connection.dateIndex).connections.size() > zoneToBalance.connections.size()) {
                if (initialImbalance > 0) {
                    BalanceFrom(zonesByDates.get(connection.dateIndex), zoneToBalance, connection.taskName);
                } else
                    BalanceTo(zoneToBalance, zonesByDates.get(connection.dateIndex), connection.taskName);
            }

            //if connection has less connections, balance connection
            if (zonesByDates.get(connection.dateIndex).connections.size() < zoneToBalance.connections.size()) {
                if (initialImbalance > 0) {
                    BalanceTo(zonesByDates.get(connection.dateIndex), zoneToBalance, connection.taskName);
                } else
                    BalanceFrom(zoneToBalance, zonesByDates.get(connection.dateIndex), connection.taskName);
            }

            if (initialImbalance * zoneToBalance.imbalance <= 0) {
                break;
            }
        }
    }

    //make transfer from zone "from" to zone "to" through connection "task" to bring imbalance in zone "to" as close to 0 as possible
    void BalanceTo(Zone to, Zone from, String task) {
        if (from.capacity.get(task) <= 0)
            return;

        if (to.imbalance == 0)
            return;

        double valueToTransfer;
        if (-to.imbalance > from.capacity.get(task))
            valueToTransfer = from.capacity.get(task);
        else
            valueToTransfer = -to.imbalance;
        if (valueToTransfer <= 0)
            return;

        from.capacity.put(task, from.capacity.get(task) - valueToTransfer);
        from.imbalance -= valueToTransfer;
        to.capacity.put(task, to.capacity.get(task) + valueToTransfer);
        to.imbalance += valueToTransfer;
        //System.out.println("Balance to Transfer " + valueToTransfer + " from zone " + (from.zoneOrderInTime + 1) + " to zone " + (to.zoneOrderInTime + 1) + " via " + task + "\n");
    }

    //make transfer from zone "from" to zone "to" through connection "task" to bring imbalance in zone "from" as close to 0 as possible
    void BalanceFrom(Zone to, Zone from, String task) {
        if (from.capacity.get(task) <= 0)
            return;

        if (from.imbalance == 0)
            return;

        double valueToTransfer;
        if (from.imbalance >= from.capacity.get(task)) {
            valueToTransfer = from.capacity.get(task);
        } else {
            valueToTransfer = from.imbalance;
        }

        if (valueToTransfer <= 0)
            return;

        from.capacity.put(task, from.capacity.get(task) - valueToTransfer);
        from.imbalance -= valueToTransfer;
        to.capacity.put(task, to.capacity.get(task) + valueToTransfer);
        to.imbalance += valueToTransfer;

        // System.out.println("Balance from Transfer " + valueToTransfer + " from zone " + (from.zoneOrderInTime + 1) + " to zone " + (to.zoneOrderInTime + 1) + " via " + task + "\n");
    }

    //make transfer from zone "from" to zone "to" through connection "task" to bring imbalance as close to equal as possible in 2 zones
    void EqualizedTransfer(Zone to, Zone from, String task) {
        //if "from" zone doesn't have hours to transfer
        if (from.capacity.get(task) <= 0)
            return;

        double valueToTransfer;
        double idealImbalance = Math.abs((to.imbalance + from.imbalance) / 2.0);
        double requiredCapacity = Math.abs(from.imbalance) - idealImbalance;

        if (from.capacity.get(task) >= requiredCapacity) {
            valueToTransfer = requiredCapacity;
        } else {
            valueToTransfer = from.capacity.get(task);
        }

        from.capacity.put(task, from.capacity.get(task) - valueToTransfer);
        from.imbalance -= valueToTransfer;
        to.capacity.put(task, to.capacity.get(task) + valueToTransfer);
        to.imbalance += valueToTransfer;

        //System.out.println("Equalized Transfer " + valueToTransfer + " from zone " + (from.zoneOrderInTime + 1) + " to zone " + (to.zoneOrderInTime + 1) + " via " + task + "\n");
    }

    //if zone or at least one of its connected zones are imbalanced, share this imbalance between all zones
    void ShareImbalanceBetweenConnections(Zone zone) {
        if (Math.abs(zone.imbalance) == 0) //return if balanced
            return;

        if (zone.connections.size() == 0) //return if no connections
            return;

        double totalImbalance = zone.imbalance;
        double totalDuration = zone.duration - zone.numOfDaysOff;
        int previousIndex = -1;

        //calculate total imbalance and total duration of connected zones + current one
        for (int i = 0; i < zone.connections.size(); i++) {
            if (zone.connections.get(i).dateIndex == previousIndex) {
                continue;
            } else {
                if (zone.imbalance / (zone.duration - zone.numOfDaysOff) > zonesByDates.get(zone.connections.get(i).dateIndex).imbalance / (zonesByDates.get(zone.connections.get(i).dateIndex).duration - zonesByDates.get(zone.connections.get(i).dateIndex).numOfDaysOff)) {
                    if (zone.capacity.get(zone.connections.get(i).taskName) == 0)
                        continue;
                } else {
                    if (zonesByDates.get(zone.connections.get(i).dateIndex).capacity.get(zone.connections.get(i).taskName) == 0)
                        continue;
                }
            }
            //if not drained
            previousIndex = zone.connections.get(i).dateIndex;
            totalImbalance += zonesByDates.get(zone.connections.get(i).dateIndex).imbalance;
            totalDuration += zonesByDates.get(zone.connections.get(i).dateIndex).duration - zonesByDates.get(zone.connections.get(i).dateIndex).numOfDaysOff;
        }

        double imbalanceForDay = totalImbalance / totalDuration;

        //go through list of connections
        for (int i = 0; i < zone.connections.size(); i++) {
            if (zone.imbalance > 0) {
                if (zone.capacity.get(zone.connections.get(i).taskName) == 0)
                    continue;
            } else {
                if (zonesByDates.get(zone.connections.get(i).dateIndex).capacity.get(zone.connections.get(i).taskName) == 0)
                    continue;
            }

            //ideal hours to transfer for connected zone (base on duration)
            double hoursToTransfer = (zonesByDates.get(zone.connections.get(i).dateIndex).duration - zonesByDates.get(zone.connections.get(i).dateIndex).numOfDaysOff) * imbalanceForDay - zonesByDates.get(zone.connections.get(i).dateIndex).imbalance;

            //if no balance yet
            if (hoursToTransfer != 0) {
                //if value is positive
                String taskName = zone.connections.get(i).taskName;
                int connectionIndex = zone.connections.get(i).dateIndex;

                if (hoursToTransfer > 0) {
                    //check capacity of current zone
                    if (zone.capacity.get(taskName) < hoursToTransfer) {
                        //update value
                        hoursToTransfer = zone.capacity.get(taskName);
                    }
                } else { //if value is negative
                    //check capacity of connection
                    if (zonesByDates.get(connectionIndex).capacity.get(taskName) < -hoursToTransfer) {
                        //update value
                        hoursToTransfer = -zonesByDates.get(zone.connections.get(i).dateIndex).capacity.get(taskName);
                    }
                }
                if (hoursToTransfer != 0)
                    Transfer(zonesByDates.get(connectionIndex), zone, taskName, hoursToTransfer);
            }
        }
    }

    //transfer hours from one zone to another via given task
    //update imbalance and capacity of each zone
    //value to transfer has to be greater or equal to capacity in "from" zone
    void Transfer(Zone to, Zone from, String task, double valueToTransfer) {
        //System.out.println("Transfer " + valueToTransfer + " from " + (from.zoneOrderInTime + 1) + " to " + (to.zoneOrderInTime + 1) + " via " + task);
        to.imbalance += valueToTransfer;
        to.capacity.put(task, to.capacity.get(task) + valueToTransfer);
        from.imbalance -= valueToTransfer;
        from.capacity.put(task, from.capacity.get(task) - valueToTransfer);
    }

    //construct connections from one zone
    Vector<Connection> vectorOfConnections(Zone zone) {
        Vector<Connection> connections = new Vector<>();
        Set<String> tasks = zone.capacity.keySet(); //get list of tasks in current zone
        for (Zone fellowZone : zonesByDates) { // go through all zones
            if (zone == fellowZone) //cannot be connected to itself
                continue;
            double imbalanceDifference = Math.abs(zone.imbalance - fellowZone.imbalance);
            for (String task : tasks) { //go through each task in current zone
                if (fellowZone.capacity.containsKey(task)) { //if task is in both zones make new connection
                    //add to sorted vector
                    if (connections.size() == 0) //if no connections yet
                        connections.add(new Connection(fellowZone.zoneOrderInTime, task, imbalanceDifference, fellowZone.capacity.get(task)));
                    else { //if there is connection list
                        boolean inserted = false;
                        for (int i = 0; i < connections.size(); i++) { //go through connections list to insert sorted
                            if (imbalanceDifference == connections.get(i).imbalanceDifference) {//if zone imbalance difference in current connection and new connection are equal
                                if (fellowZone.capacity.get(task) >= connections.get(i).taskCapacity) { //if task capacity is bigger in fellow zone than in current connection
                                    connections.insertElementAt(new Connection(fellowZone.zoneOrderInTime, task, imbalanceDifference, fellowZone.capacity.get(task)), i);
                                    inserted = true;
                                    break;
                                }
                            } else if (imbalanceDifference > connections.get(i).imbalanceDifference) { //if zone imbalance difference in new connection is bigger than in current connection
                                connections.insertElementAt(new Connection(fellowZone.zoneOrderInTime, task, imbalanceDifference, fellowZone.capacity.get(task)), i);
                                inserted = true;
                                break;
                            }
                        } //end go through connections list
                        if (!inserted) {
                            connections.add(new Connection(fellowZone.zoneOrderInTime, task, imbalanceDifference, fellowZone.capacity.get(task)));
                        }
                    } //end if there is connection list
                } //end make new connection
            } //end go through each task in current zone
        }//end go through zones
        return connections;
    }

    //save result in BuiltDayList in Data class
    //position order of tasks base on task priorities
    void constructOutput() {
        Set<String> tasks;

        //construct output for each zone
        for (Zone z : zonesByDates) {
            double totalPrioritizedHrs = 0;
            double totalNonPrioritizedHrs = 0;
            tasks = z.capacity.keySet();

            //calculate total capacities of prioritized and non-prioritized tasks in the zone
            for (String t : tasks) {
                if (z.capacity.get(t) == 0) {
                    continue;
                }
                for (int i = 0; i < Data.getTaskList().getSize(); i++) {
                    //when found
                    if (Data.getTaskList().getTasks().get(i).getName().equals(t)) {
                        if (Data.getTaskList().getTasks().get(i).getPriority()) {
                            totalPrioritizedHrs += z.capacity.get(t);
                        } else {
                            totalNonPrioritizedHrs += z.capacity.get(t);
                        }
                        break;
                    }
                }
            }

            double dayCapacity = (totalNonPrioritizedHrs + totalPrioritizedHrs) / (z.duration - z.numOfDaysOff);
            if (totalNonPrioritizedHrs == 0 && totalPrioritizedHrs == 0) {
                for (int i = 0; i < z.duration; i++) {
                    Date currentDate = new Date(Data.getTaskList().getEarliest().getTime() + TimeUnit.DAYS.toMillis(bdList.getSize()));
                    BuiltDay day = new BuiltDay(currentDate);
                    day.setTotalHours(0);
                    bdList.addBDay(day);
                }
            }else if(totalNonPrioritizedHrs == 0 || totalPrioritizedHrs == 0){
                AddMonoBlock(z, 0, totalPrioritizedHrs + totalNonPrioritizedHrs, dayCapacity);
            } else {
                AddMonoBlock(z, 1, totalPrioritizedHrs, dayCapacity);
                if (totalPrioritizedHrs % dayCapacity != 0) {
                    AddMixedBlock(z, dayCapacity, totalPrioritizedHrs, totalNonPrioritizedHrs);
                }
                AddMonoBlock(z, -1, totalNonPrioritizedHrs, dayCapacity);
            }
        }
        while(bdList.getSize() < Data.getTaskList().getDuration()) {
            Date currentDate = new Date(Data.getTaskList().getEarliest().getTime() + TimeUnit.DAYS.toMillis(bdList.getSize()));
            BuiltDay day = new BuiltDay(currentDate);
            day.setTotalHours(0);
            bdList.addBDay(day);
        }
        bdList.totalHours = totalHours;
        Data.setBuiltDaysList(bdList);
    }

    //add sequence of days with only one type of tasks (prioritized or non prioritized) for one zone
    //taskType: 1 when prioritized, -1 when non prioritized, 0 for mono zone
    void AddMonoBlock(Zone z, int taskType, double totalCapacity, double dayCapacity) {
        BuiltDay day;
        BuiltTask task;
        Set<String> tasks;
        Date currentDate;
        double blockDuration = Math.floor(totalCapacity / dayCapacity); //3.6
        for (int i = 0; i < blockDuration; i++) {
            currentDate = new Date(Data.getTaskList().getEarliest().getTime() + TimeUnit.DAYS.toMillis(bdList.getSize()));
            day = new BuiltDay(currentDate);
            //if it's a day off
            if (Data.getDayOffList().getDays().contains(currentDate)) {
                day.setTotalHours(0);
                bdList.addBDay(day);
                blockDuration++;
                continue;
            }

            tasks = z.capacity.keySet();
            for (String t : tasks) {
                if (z.capacity.get(t) == 0) {
                    continue;
                }
                double percentage;
                if (taskType == 1) {
                    for (int j = 0; j < Data.getTaskList().getSize(); j++) {
                        if (Data.getTaskList().getTasks().get(j).getName().equals(t) && Data.getTaskList().getTasks().get(j).getPriority()) {
                            percentage = 100 * z.capacity.get(t) / (totalCapacity/dayCapacity) / Data.getTaskList().getTasks().get(j).getHours();
                            task = new BuiltTask(t, z.capacity.get(t) / (totalCapacity/dayCapacity), percentage, Data.getTaskList().getTasks().get(j).getColor());
                            day.addBTask(task);
                            break;
                        }
                    }

                }else if(taskType == -1){
                    for (int j = 0; j < Data.getTaskList().getSize(); j++) {
                        if (Data.getTaskList().getTasks().get(j).getName().equals(t) && !Data.getTaskList().getTasks().get(j).getPriority()) {
                            percentage = 100 * z.capacity.get(t) / (totalCapacity/dayCapacity) / Data.getTaskList().getTasks().get(j).getHours();
                            task = new BuiltTask(t, z.capacity.get(t) / (totalCapacity/dayCapacity), percentage, Data.getTaskList().getTasks().get(j).getColor());
                            day.addBTask(task);
                            break;
                        }
                    }

                }else{
                    for (int j = 0; j < Data.getTaskList().getSize(); j++) {
                        if (Data.getTaskList().getTasks().get(j).getName().equals(t)){
                            percentage = 100 * z.capacity.get(t) / (z.duration - z.numOfDaysOff) / Data.getTaskList().getTasks().get(j).getHours();
                            task = new BuiltTask(t, z.capacity.get(t) / (z.duration - z.numOfDaysOff), percentage, Data.getTaskList().getTasks().get(j).getColor());
                            day.addBTask(task);
                            break;
                        }
                    }

                }
            }
            bdList.addBDay(day);
        }
    }

    //add day to built days list that contains both prioritized and non prioritized tasks for one zone
    void AddMixedBlock(Zone z, double dayCapacity, double totalPrioritizedHrs, double totalNonPrioritizedHrs){
        BuiltDay day;
        BuiltTask task;
        Set<String> tasks;
        Date currentDate;
        boolean added = false;

        while(!added){
            currentDate = new Date(Data.getTaskList().getEarliest().getTime() + TimeUnit.DAYS.toMillis(bdList.getSize()));
            day = new BuiltDay(currentDate);
            if (Data.getDayOffList().getDays().contains(currentDate)) {
                day.setTotalHours(0);
            } else {
                added = true;
                double pFraction = totalPrioritizedHrs / dayCapacity - Math.floor(totalPrioritizedHrs / dayCapacity);
                double nFraction = 1 - pFraction;
                tasks = z.capacity.keySet();

                // add prioritized tasks to day
                for (String t : tasks) {
                    if (z.capacity.get(t) == 0) {
                        continue;
                    }
                    double percentage;

                    //go through each task
                    for (int j = 0; j < Data.getTaskList().getSize(); j++) {
                        if (Data.getTaskList().getTasks().get(j).getName().equals(t) && Data.getTaskList().getTasks().get(j).getPriority()) {
                            percentage = Math.round(100 * z.capacity.get(t) / (totalPrioritizedHrs/dayCapacity)* pFraction / Data.getTaskList().getTasks().get(j).getHours());
                            task = new BuiltTask(t, z.capacity.get(t) /(totalPrioritizedHrs/dayCapacity)* pFraction, percentage, Data.getTaskList().getTasks().get(j).getColor());
                            day.addBTask(task);
                            break;
                        }
                    }
                }

                // add non prioritized tasks to day
                for (String t : tasks) {
                    if (z.capacity.get(t) == 0) {
                        continue;
                    }
                    double percentage;
                    for (int j = 0; j < Data.getTaskList().getSize(); j++) {
                        if (Data.getTaskList().getTasks().get(j).getName().equals(t) && !Data.getTaskList().getTasks().get(j).getPriority()) {
                            percentage = Math.round(100 * z.capacity.get(t) / (totalNonPrioritizedHrs/dayCapacity) * nFraction / Data.getTaskList().getTasks().get(j).getHours());
                            task = new BuiltTask(t, z.capacity.get(t) /(totalNonPrioritizedHrs/dayCapacity) * nFraction, percentage, Data.getTaskList().getTasks().get(j).getColor());
                            day.addBTask(task);
                            break;
                        }
                    }//end task search
                }// end non prioritized
            }//end if not a day off
            bdList.addBDay(day);
        }//end while loop
    }
/*
    void printZones(){
        System.out.println("Total hours: "+ totalHours);
        System.out.println("Duration: "+ duration);
        System.out.println("Ideal Hours per day: "+ idealHoursPerDay + '\n');
        System.out.println("\n");
        for(Zone zone: zonesByDates){
            System.out.println("  zone " + (zone.zoneOrderInTime + 1) + ": duration = " + zone.duration);
            System.out.println("  Index = " + zone.zoneIndex);
            //System.out.println("  Hours per day: " + zone.hrsPerDay);
            System.out.println("  Imbalance:  " + String.format("%.2f",zone.imbalance));
            System.out.print("   ");
            System.out.println(zone.capacity);
            System.out.println('\n');
        }
    }


 */
   /*
    private void printCriticalDays(){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        for(int i = 0; i < criticalDays.size(); i++){
            System.out.println(simpleDateFormat.format(criticalDays.get(i)));
        }
    }
     */
}