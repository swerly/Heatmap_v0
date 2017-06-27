package com.swerly.mywifiheatmap;

import java.io.Serializable;

/**
 * Created by sethw on 8/11/2016.
 */
public class SwerlyLine implements Serializable{
    public float x;
    public float y;

    public SwerlyLine(float x, float y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "x: " + Float.toString(x) + "\t\ty " + Float.toString(y);
    }
}
