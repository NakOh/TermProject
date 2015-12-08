package com.termproject.termproject.manager;

/**
 * Created by kk070 on 2015-12-06.
 */
public class GameManager {
    private static GameManager instance;
    private int difficulty;
    private int findMine;
    private int totalMine = 0;
    private boolean end = false;

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

    public int getFindMine() {
        return findMine;
    }

    public void setFindMine(int findMine) {
        this.findMine = findMine;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getTotalMine() {
        return totalMine;
    }

    public void setTotalMine(int totalMine) {
        this.totalMine = totalMine;
    }
}
