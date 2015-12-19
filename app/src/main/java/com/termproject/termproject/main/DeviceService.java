package com.termproject.termproject.main;

import android.text.format.Time;

import com.termproject.termproject.manager.GameManager;

/**
 * Created by kk070 on 2015-12-02.
 */
public class DeviceService {

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

    private GameManager gameManager;
    public LEDControl ledControl;
    protected static final int MY_TURN = 100;
    protected static final int THREAD_FLAGS_PRINT = 0;

    public DeviceService() {
        ledControl = new LEDControl();
        gameManager= GameManager.getInstance();
    }

    public void LEDControl() {
        ledControl.start();
    }

    class LEDControl extends Thread {
        int LedData;
        int compare[] = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};
        int i = 0;
        int preResult = 0;

        public LEDControl() {
            LedData = 0;
            LEDControl(LedData);
        }

        @Override
        public void run() {
            while (true) {
                if (gameManager.isMyTurn()) {
                    int result;
                    Time t = new Time();
                    t.set(System.currentTimeMillis());
                    result = t.hour * 10000 + t.minute * 100 + t.second;
                    try {
                        this.wait(500);
                    } catch (Exception e) {

                    }
                    if (preResult == 0) {
                        preResult = result;
                    }

                    if (result - preResult >= 1) {
                        if (i == 8) {
                            gameManager.setYesCombo(false);
                            i = 0;
                        }
                        if ((i - 1) < 0) {
                            //i가 0일때
                            gameManager.setYesCombo(true);
                            LedData &= ~(compare[7]);
                            LedData |= compare[i]; //켜기
                        } else {
                            LedData &= ~(compare[i - 1]); //끄기
                            LedData |= compare[i]; //켜기
                        }
                        i++;
                        LEDControl(LedData);
                        preResult = result;
                    }
                }else{
                    //내 턴이 끝나면 초기화!
                    LedData = 0;
                    LEDControl(LedData);
                }
            }
        }
    }

    public void getAttack() {
        FLEDControl2(255, 0, 0); // red
    }

    public void getDefense() {
        FLEDControl2(0, 0, 255); // blue
    }

    public void getHelp() {
        FLEDControl2(0, 255, 0); // green
    }

    public void run() {
        while (!stop) {
            switch (flag) {
                default:
                    SegmentIOControl(THREAD_FLAGS_PRINT);
                    // segMessage = 남은 지뢰. 순위. 찾은 지뢰
                    SegmentControl(segMessage);
                    break;
                case MY_TURN:
                    SegmentIOControl(THREAD_FLAGS_PRINT);
                    while (countDown > 0 && flag == MY_TURN) {
                        if (countDown == 0) {
                            countDown |= 0x01;
                        } else {
                            countDown = countDown << 1;
                            if (countDown > 0x80) countDown = 0x01;
                        }
                        LEDControl(countDown);
                    }
            }
        }
    }

}
