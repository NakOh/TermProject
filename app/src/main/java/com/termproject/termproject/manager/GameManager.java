package com.termproject.termproject.manager;

import com.termproject.termproject.model.Tile;

/**
 * Created by kk070 on 2015-12-06.
 */
public class GameManager {
    private static GameManager instance;
    private int difficulty;
    private int index;
    private int findMine;
    private int findOtherMine;
    private int totalMine = 0;
    private boolean end = false;
    private boolean multi;
    private boolean server;
    private boolean myTurn ;
    private boolean first;
    private boolean wait;
    private Thread myThread;
    private Tile[][] tile;
    private int[][] queueTile;
    private int queueCounter = 0;
    private int queueSearcher = -1;

    public static GameManager getInstance(){
        if(instance == null){
            instance  = new GameManager();
        }
        return instance;
    }

    public void checkUpdate(int i, int j){
        tile[i][j].setIsShow(true);
        if (tile[i][j].isMine()) {
            //   mVibrator.vibrate(10); // 몇 콤보인지 확인하여 그에 따라 진동이 세지게 설정해야함
           setFindOtherMine(getFindOtherMine() + 1);
        } else if (tile[i][j].getNumber() == 0) {
            getQueueTile()[getQueueCounter()][1] = i;
            getQueueTile()[getQueueCounter()][2] = j;
            checkSide(getIndex());
        }
    }

    public void checkSide(int index) {
        int i, j;
        while (getQueueCounter() != getQueueSearcher()) {
            setQueueSearcher(getQueueSearcher() + 1);
            i = getQueueTile()[getQueueSearcher()][1];
            j = getQueueTile()[getQueueSearcher()][2];
            if (i + 1 < index - 1 && !(tile[i + 1][j].isShow()) && !(tile[i + 1][j].isMine())) {
                tile[i + 1][j].setIsShow(true);
                if (tile[i + 1][j].getNumber() == 0) {
                    setQueueCounter(getQueueCounter() + 1);
                    getQueueTile()[getQueueCounter()][1] = i + 1;
                    getQueueTile()[getQueueCounter()][2] = j;
                }
            }
            if (j + 1 < index - 1 && !(tile[i][j + 1].isShow()) && !(tile[i][j + 1].isMine())) {
                tile[i][j + 1].setIsShow(true);
                if (tile[i][j + 1].getNumber() == 0) {
                    setQueueCounter(getQueueCounter() + 1);
                    getQueueTile()[getQueueCounter()][1] = i;
                    getQueueTile()[getQueueCounter()][2] = j + 1;
                }
            }
            if (i - 1 > 0 && !(tile[i - 1][j].isShow()) && !(tile[i - 1][j].isMine())) {
                tile[i - 1][j].setIsShow(true);
                if (tile[i - 1][j].getNumber() == 0) {
                    setQueueCounter(getQueueCounter() + 1);
                    getQueueTile()[getQueueCounter()][1] = i - 1;
                    getQueueTile()[getQueueCounter()][2] = j;
                }
            }
            if (j - 1 > 0 && !(tile[i][j - 1].isShow()) && !(tile[i][j - 1].isMine())) {
                tile[i][j - 1].setIsShow(true);
                if (tile[i][j - 1].getNumber() == 0) {
                    setQueueCounter(getQueueCounter() + 1);
                    getQueueTile()[getQueueCounter()][1] = i;
                    getQueueTile()[getQueueCounter()][2] = j - 1;
                }
            }
        }
    }

    private GameManager(){

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int[][] getQueueTile() {
        return queueTile;
    }

    public void setQueueTile(int[][] queueTile) {
        this.queueTile = queueTile;
    }

    public int getQueueCounter() {
        return queueCounter;
    }

    public void setQueueCounter(int queueCounter) {
        this.queueCounter = queueCounter;
    }

    public int getQueueSearcher() {
        return queueSearcher;
    }

    public void setQueueSearcher(int queueSearcher) {
        this.queueSearcher = queueSearcher;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getFindOtherMine() {
        return findOtherMine;
    }

    public void setFindOtherMine(int findOtherMine) {
        this.findOtherMine = findOtherMine;
    }
}
