package com.swerly.mywifiheatmap;

import android.graphics.Path;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sethw on 8/11/2016.
 */
public class SwerlyLineList implements Serializable{
    private ArrayList<SwerlyLine> lines;
    private SwerlyLine first;
    private int numLines;

    public SwerlyLineList(SwerlyLine startingPoint){
        this(new ArrayList<SwerlyLine>(), startingPoint); }

    public SwerlyLineList(ArrayList<SwerlyLine> lines, SwerlyLine startingPoint){
        this.lines = lines;
        first = startingPoint;
    }

    public ArrayList<SwerlyLine> getLines(){
        return this.lines;
    }

    public boolean linesExist(){
        //return lines != null ? !lines.isEmpty() : false;
            if (lines == null) return false;
            if (lines.isEmpty()) return false;
            return true;
    }
    public SwerlyLine getStartPoint(){ return first;}

    public SwerlyLine getFirstLineEnd(){
        return lines.get(0);
    }

    public SwerlyLine getLastLineEnd(){
        return lines.get(lines.size()-1);
    }

    public Path getAsPath(){
        Path returnPath = new Path();
        float startX = first.x;
        float startY = first.y;
        int i = 0;

        returnPath.moveTo(startX, startY);

        for (SwerlyLine lineEndPoint : lines){
            returnPath.lineTo(lineEndPoint.x, lineEndPoint.y);
            i++;
        }
        return returnPath;
    }



    public void addLine(SwerlyLine line){
        lines.add(line);
        numLines+=1;
    }

    public void removeLatest(){
        lines.remove(lines.size()-1);
        numLines-=1;
    }

    public boolean isPathClosed(){
        return (getLastLineEnd().x == getStartPoint().x) && (getLastLineEnd().y == getStartPoint().y);
    }

    public int getNumLines(){ return numLines;}

    public float[][] getPoints(){
        float[][] points = new float[2][numLines];

        for (int i = 0; i < getNumLines(); i++){
            points[0][i] = lines.get(i).x;
            points[1][i] = lines.get(i).y;
        }
        return points;
    }
}
