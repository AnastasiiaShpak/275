/*
Level of difficulty represent correspondence of number of hours for task and name of difficulty
When level of difficulty if created, user is able to input name of difficulty instead of number of hours

CMPT275 Project
Group 21

 */
package com.company;
import java.util.Hashtable;

public class LevelsOfDifficultyList {
    private static Hashtable<String, Double> levels = new Hashtable<>();

    //-1 if level already exists
    //-2 if name is too long
    //-3 if difficulty is invalid
    // 0 if successful
    public static int addLevel(String levelName, double difficulty){
        if(levels.containsKey(levelName))
            return -1;
        else if(levelName.length() > 25)
            return -2;
        else if(difficulty < 0)
            return -3;

        levels.put(levelName, difficulty);
        return 0;
    }

    //-1 if level not found
    //-2 if new name is too long
    //0 if successful
    public static int editLevelName(String oldName, String newName){
        if(!levels.containsKey(oldName))
            return -1;
        else if(newName.length() > 25)
            return -2;

        levels.put(newName, levels.get(oldName));
        levels.remove(oldName);
        return 0;
    }

    //-1 if level not found
    //-2 if difficulty is invalid
    //0 if successful
    public static int editLevelDifficulty(String levelName, double newDifficulty){
        if(!levels.containsKey(levelName))
            return -1;
        else if(newDifficulty < 0)
            return -2;
        levels.put(levelName, newDifficulty);
        return 0;
    }

    //-1 if level not found
    //0 if successful
    public static int removeLevel(String levelName){
        if(!levels.containsKey(levelName))
            return -1;
        levels.remove(levelName);
        return 0;
    }

    //-1 if level is not found
    public static double getDifficulty(String levelName){
        if(!levels.containsKey(levelName))
            return -1;

        return levels.get(levelName);
    }
}
