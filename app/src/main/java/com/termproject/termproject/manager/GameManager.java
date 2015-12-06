package com.termproject.termproject.manager;

/**
 * Created by kk070 on 2015-12-06.
 */
public class GameManager {
    private int difficulty;

    public GameManager(int difficulty){
        this.difficulty = difficulty;
    }
    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
