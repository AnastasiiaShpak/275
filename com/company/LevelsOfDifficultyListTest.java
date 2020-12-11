/*
Testing of Levels of Difficulty class

CMPT275 Project
Group 21
 */


package com.company;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LevelsOfDifficultyListTest {

    @Test
    void addLevel() {
        //case1: successful case
        assertEquals(LevelsOfDifficultyList.addLevel("A", 5), 0);

        //case2: repeated name
        assertEquals(LevelsOfDifficultyList.addLevel("A", 4), -1);

        //case3: name length exceeds limit of 25
        assertEquals(LevelsOfDifficultyList.addLevel("aaaaaaaaaaaaaaaaaaaaaaaaaa", 4), -2);

        //case4: negative difficulty
        assertEquals(LevelsOfDifficultyList.addLevel("B", -1), -3);

        LevelsOfDifficultyList.removeLevel("A");
    }

    @Test
    void editLevelName() {
        LevelsOfDifficultyList.addLevel("A", 5);

        //case1: successful case
        assertEquals(LevelsOfDifficultyList.editLevelName("A", "B"), 0);

        //case2: name is not in a list
        assertEquals(LevelsOfDifficultyList.editLevelName("A", "B"), -1);

        //case3: name length exceeds limit of 25
        assertEquals(LevelsOfDifficultyList.editLevelName("B","aaaaaaaaaaaaaaaaaaaaaaaaaa"), -2);

        LevelsOfDifficultyList.removeLevel("B");
    }

    @Test
    void editLevelDifficulty() {
        LevelsOfDifficultyList.addLevel("A", 5);

        //case1: successful case
        assertEquals(LevelsOfDifficultyList.levels.size(), 1);
        assertEquals(LevelsOfDifficultyList.editLevelDifficulty("A", 6), 0);

        //case2: name is not in a list
        assertEquals(LevelsOfDifficultyList.levels.size(), 1);
        assertEquals(LevelsOfDifficultyList.editLevelDifficulty("B", 6), -1);

        //case3: negative difficulty
        assertEquals(LevelsOfDifficultyList.levels.size(), 1);
        assertEquals(LevelsOfDifficultyList.editLevelDifficulty("A",-1), -2);

        LevelsOfDifficultyList.removeLevel("A");
    }

    @Test
    void removeLevel() {
        LevelsOfDifficultyList.addLevel("A", 5);

        //case1: name is not in a list
        assertEquals(LevelsOfDifficultyList.levels.size(), 1);
        assertEquals(LevelsOfDifficultyList.removeLevel("B"), -1);

        //case2: successful case
        assertEquals(LevelsOfDifficultyList.removeLevel("A"), 0);
        assertEquals(LevelsOfDifficultyList.levels.size(), 0);

        LevelsOfDifficultyList.removeLevel("A");
    }

    @Test
    void getDifficulty() {
        LevelsOfDifficultyList.addLevel("A", 5);

        //case1: successful case
        assertEquals(LevelsOfDifficultyList.getDifficulty("A"), 5);

        //case2: level not in the list
        assertEquals(LevelsOfDifficultyList.getDifficulty("B"), -1);

        LevelsOfDifficultyList.removeLevel("A");
    }
}