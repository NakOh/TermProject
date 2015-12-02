package com.termproject.termproject;

/**
 * Created by kk070 on 2015-12-02.
 */
public class DeviceActivity {
    static {
        System.loadLibrary("termproject");
    }

    public native int PiezoControl(int value);
    public native int FLEDControl(int led_num, String str);
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
}
