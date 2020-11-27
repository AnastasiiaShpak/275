package com.company;

import java.util.Vector;

public class LevelsOfDifficultyList {
    private Vector<DiffLevel> levels;

    LevelsOfDifficultyList(){
        levels = new Vector<DiffLevel>();
    }

    //add task
    void addLevel(DiffLevel level){
        levels.add(level);
    }

    void removeLevel(DiffLevel level){
        levels.remove(level);
    }

    public Vector<DiffLevel> getLevels() {
        return levels;
    }

    public void setLevels(Vector<DiffLevel> levels) {
        this.levels = levels;
    }
}
