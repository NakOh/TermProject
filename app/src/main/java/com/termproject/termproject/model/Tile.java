package com.termproject.termproject.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.termproject.termproject.R;

/**
 * Created by kk070 on 2015-12-02.
 */
public class Tile {
    private Bitmap tileBitmap;
    private Bitmap defaultTile;
    private int number;
    private boolean isMine = false;
    private boolean isShow = false;
    private int x;
    private int y;
    private int w;
    private int h;
    private int[] resource = {R.drawable.number0,R.drawable.number1,R.drawable.number2,R.drawable.number3,R.drawable.number4,R.drawable.number5,R.drawable.number6,R.drawable.number7,R.drawable.number8};


    public Tile(Context context){
        defaultTile = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile);
    }
    public void setImage(Context context){
        if(isMine){
            tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mine);
        }else{
            tileBitmap = BitmapFactory.decodeResource(context.getResources(), resource[number]);
        }
    }

    public void setSize(int w, int h){
        this.setW(w);
        this.setH(h);
        tileBitmap = Bitmap.createScaledBitmap(tileBitmap, w, h, true);
        defaultTile = Bitmap.createScaledBitmap(defaultTile, w, h, true);
    }

    public void update(Canvas canvas, int x, int y){
        this.setX(x);
        this.setY(y);
        //if(isShow) {
            canvas.drawBitmap(tileBitmap, x, y, null);
        //}else{
         //   canvas.drawBitmap(defaultTile, x, y, null);
       // }
    }

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

    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
