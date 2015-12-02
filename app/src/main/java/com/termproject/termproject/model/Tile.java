package com.termproject.termproject.model;

/**
 * Created by kk070 on 2015-12-02.
 */
public class Tile {
    private int number;
    private boolean isMine;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }
}
