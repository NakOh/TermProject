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
    private boolean isItem = false;
    private boolean isClicked = false;
    private int x;
    private int y;
    private int w;
    private int h;
    private int index;
    private int[] resource = {R.drawable.number0, R.drawable.number1, R.drawable.number2, R.drawable.number3, R.drawable.number4, R.drawable.number5, R.drawable.number6, R.drawable.number7, R.drawable.number8};
    private int[] itemCounter = {0, 0, 0, 0, 0, 0, 0};

    public Tile(Context context) {
        defaultTile = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile);
        for(int i = 0; i < 7; i++){
            itemCounter[i] = 0;
        }
    }

    public void setImage(Context context) {
        if (isMine) {
            tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mine);
        } else if (isItem) {
            setIndex(randomRange(1, 6));
            if (getIndex() == 1 && itemCounter[1] < 2) {
                tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.anti_change);
                itemCounter[1]++;
            } else if (getIndex() == 2 && itemCounter[2] < 2) {
                tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.anti_time);
                itemCounter[2]++;
            } else if (getIndex() == 3 && itemCounter[3] < 2) {
                tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.onemore);
                itemCounter[3]++;
            } else if (getIndex() == 4 && itemCounter[4] < 2) {
                tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.glass);
                itemCounter[4]++;
            } else if (getIndex() == 5 && itemCounter[5] < 2) {
                tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.change);
                itemCounter[5]++;
            } else if (getIndex() == 6 && itemCounter[6] < 2) {
                tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.time);
                itemCounter[6]++;
            }
        } else {
            tileBitmap = BitmapFactory.decodeResource(context.getResources(), resource[number]);
        }
    }

    public void setSize(int w, int h) {
        this.setW(w);
        this.setH(h);
        tileBitmap = Bitmap.createScaledBitmap(tileBitmap, w, h, true);
        defaultTile = Bitmap.createScaledBitmap(defaultTile, w, h, true);
    }

    public void update(Canvas canvas, int x, int y) {
        this.setX(x);
        this.setY(y);
        //원래는 default 이미지가 보이다가 눌렸을 경우 자신의 BItmap을 보여줘야하는데, 작동을 하지 않아 일단 주석처리해둠
        if (isShow) {
            canvas.drawBitmap(tileBitmap, x, y, null);
        } else {
            canvas.drawBitmap(defaultTile, x, y, null);
        }
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

    public boolean isItem() {
        return isItem;
    }

    public void setIsItem(boolean isItem) {
        this.isItem = isItem;
    }

    private int randomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }



    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setIsClicked() {
        this.isClicked = true;
    }

    public boolean getIsClicked() {
        return isClicked;
    }
}
