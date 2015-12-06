package com.termproject.termproject.main;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.termproject.termproject.manager.GameManager;
import com.termproject.termproject.model.Tile;

/**
 * Created by kk070 on 2015-12-06.
 */
public class MainView extends View {
    private Context mContext = null;
    private Tile[] tile;
    private int w, h;
    private GameManager gameManager;

    public MainView(Context context) {
        super(context);
        this.mContext = context;
        //0 쉬움(5*5), 1 중간(7*7), 2 어려움(10*10)
        gameManager = new GameManager(0);

    }


    @Override
    protected void onDraw(Canvas canvas) {
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        this.w =w;
        this.h =h;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
