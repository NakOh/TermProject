package com.termproject.termproject.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.termproject.termproject.R;
import com.termproject.termproject.manager.GameManager;

/**
 * Created by kk070 on 2015-12-02.
 */
public class Tile {
    private Bitmap tileBitmap;
    private int number;
    private boolean isMine = false;
    private boolean isShow;
    private int[] resource = {R.drawable.number0,R.drawable.number1,R.drawable.number2,R.drawable.number3,R.drawable.number4,R.drawable.number5,R.drawable.number6,R.drawable.number7,R.drawable.number8};


    public void setImage(Context context){
        if(isMine){
            tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mine);
        }else{
            tileBitmap = BitmapFactory.decodeResource(context.getResources(), resource[number]);
        }
    }

    public void setSize(int w, int h, int index){
        tileBitmap = Bitmap.createScaledBitmap(tileBitmap, w/index, h/index, true);
    }

    public void update(Canvas canvas, int x, int y){
        canvas.drawBitmap(tileBitmap, x, y, null);
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
}
