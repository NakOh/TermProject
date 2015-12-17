package com.termproject.termproject.main;

import com.termproject.termproject.main.MainView;

/**
 * Created by kk070 on 2015-12-02.
 */
public class DeviceService{
    static {
        System.loadLibrary("termproject");
    }

    public native int PiezoControl(int value);
    public native int FLEDControl(int led_num, String str);
    public native int FLEDControl2(int led1, int led2, int led3);
    public native int DotMatrixControl(String data);
    public native int TextLCDOut(String str, String str2);
    public native int IOCtlClear();
    public native int IOCtlReturnHome();
    public native int IOCtlDisplay(boolean bOn);
    public native int IOCtlCursor(boolean bOn);
    public native int IOCtlBlink(boolean bOn);
    public native int SegmentControl(int value);
    public native int SegmentIOControl(int value);
    public native int LEDControl(int value);

    //private GameManager gameManager;
    public boolean stop = false;
    public int flag;
    public int countDown = 0;
    public int segMessage;

    protected static final int MY_TURN = 100;
    protected static final int THREAD_FLAGS_PRINT = 0;

    public void run(){
        while(!stop){
            switch(flag){
                default:
                    SegmentIOControl(THREAD_FLAGS_PRINT);
                    // segMessage = 남은 지뢰. 순위. 찾은 지뢰
                    SegmentControl(segMessage);
                    break;
                case MY_TURN:
                    SegmentIOControl(THREAD_FLAGS_PRINT);
                    while(countDown > 0 && flag == MY_TURN) {
                        if(countDown == 0) {
                            countDown |= 0x01;
                        }
                        else {
                            countDown = countDown << 1;
                            if(countDown > 0x80) countDown = 0x01;
                        }
                        LEDControl(countDown);
                    }
            }
        }
    }

    public void getAttack() {
        FLEDControl2(255, 0, 0);
    }

    public void getDefense() {
        FLEDControl2(0, 0, 255);
    }
}
