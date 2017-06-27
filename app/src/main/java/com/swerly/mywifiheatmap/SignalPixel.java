package com.swerly.mywifiheatmap;

/**
 * Created by sethw on 8/15/2016.
 */
public class SignalPixel {

    public int level = 0;
    public boolean setFirstColor = false;
    public float x;
    public float y;

    public SignalPixel(float x, float y){
        this.x = x;
        this.y = y;
    }
}
