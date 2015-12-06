package com.termproject.termproject.manager;

/**
 * Created by kk070 on 2015-12-06.
 */
public class GameManager {
    private static GameManager instance;
    private int difficulty;

    public static GameManager getInstance(){
        if(instance == null){
            instance  = new GameManager(0);
        }
        return instance;
    }

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
