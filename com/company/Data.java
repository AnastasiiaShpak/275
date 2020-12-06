/*
Data class
Contains all data shared by packages and support functions used by multiple packages
All data is static and public

CMPT275
Group 21
 */

package com.company;
import java.util.Date;

public class Data {
    private static TaskList tList = new TaskList();
    private static DayList dList = new DayList();
    private static LevelsOfDifficultyList lList = new LevelsOfDifficultyList();
    private static CategoriesList cList = new CategoriesList();
    private static BuiltDayList bdList = new BuiltDayList();

    public static void setTaskList(TaskList tl){
        tList = tl;
    }
    public static void setDayOffList(DayList dl){
        dList = dl;
    }
    public static void setLevelsList(LevelsOfDifficultyList ll){
        lList = ll;
    }
    public static void setCategoriesList(CategoriesList cl){
        cList = cl;
    }
    public static void setBuiltDaysList(BuiltDayList bdl){
        bdList = bdl;
    }
    public static TaskList getTaskList(){
        return tList;
    }
    public static DayList getDayOffList(){
        return dList;
    }
    public static LevelsOfDifficultyList getLevels(){
        return lList;
    }
    public static CategoriesList getCategories(){
        return cList;
    }
    public static BuiltDayList getBuiltDayList(){
        return bdList;
    }
    //support function for TaskList and ScheduleBuilder
    //finds difference between 2 dates in days (start and end dates included)
    public static int difference(Date smallest, Date biggest){
        long s = smallest.getTime();
        long b = biggest.getTime();
        long diffTime = b - s;
        return (int) (diffTime / (1000 * 60 * 60 * 24)) + 1;
    }
    public static void printBDList(){
        for(int i = 0; i < bdList.getSize(); i++){
            System.out.println(bdList.getBuiltDays().get(i).getDate());
            for(int j = 0; j < bdList.getBuiltDays().get(i).getBuiltTasks().size(); j++){
                //System.out.println(" Task " + (j + 1));
                System.out.println(bdList.getBuiltDays().get(i).getBuiltTasks().get(j).getName());
                System.out.println("   hours: " + String.format("%.2f", bdList.getBuiltDays().get(i).getBuiltTasks().get(j).getHours()));
                System.out.println("   percentage: " + String.format("%.2f", bdList.getBuiltDays().get(i).getBuiltTasks().get(j).getPercentage()));
                System.out.println("   color: " + bdList.getBuiltDays().get(i).getBuiltTasks().get(j).getColor());

            }
            System.out.println("total hours: " + String.format("%.2f", bdList.getBuiltDays().get(i).getTotalHours()));
            System.out.println("\n");
        }
    }
}
