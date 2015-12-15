package com.termproject.termproject.manager;

import com.termproject.termproject.model.Tile;

/**
 * Created by kk070 on 2015-12-06.
 */
public class GameManager {
    private static GameManager instance;
    private int difficulty;
    private int findMine;
    private int totalMine = 0;
    private boolean end = false;
    private boolean multi;
    private boolean server;
    private boolean myTurn;
    private boolean wait = true;
    private Thread myThread;
    private Tile[][] tile;

    public static GameManager getInstance(){
        if(instance == null){
            instance  = new GameManager(0);
        }
        return instance;
    }

    public void checkUpdate(){

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

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public Tile[][] getTile() {
        return tile;
    }

    public void setTile(Tile[][] tile) {
        this.tile = tile;
    }

    public Thread getMyThread() {
        return myThread;
    }

    public void setMyThread(Thread myThread) {
        this.myThread = myThread;
    }
}
