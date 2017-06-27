package com.swerly.mywifiheatmap;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by sethw on 8/16/2016.
 */
public class SignalPixelHelper {
    private ArrayList<SignalPixel> pixelsInBoundry;
    private SwerlyWifiDrawingView context;
    private SwerlyPolygon polygon;
    private Paint redHeatmapPaint;
    private Paint orangeHeatmapPaint;
    private Paint yellowHeatmapPaint;
    private Paint greenHeatmapPaint;
    private Paint blueHeatmapPaint;

    public SignalPixelHelper(SwerlyWifiDrawingView context){this(context, new ArrayList<SignalPixel>());}

    public SignalPixelHelper(SwerlyWifiDrawingView context, ArrayList<SignalPixel> list){
        this.pixelsInBoundry = list;
        this.context = context;
        this.polygon = context.getPolygon();
        setupPaints();
    }

    private void setupPaints(){
        redHeatmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redHeatmapPaint.setStyle(Paint.Style.FILL);
        redHeatmapPaint.setColor(context.getResources().getColor(R.color.redHeatmap));

        orangeHeatmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        orangeHeatmapPaint.setStyle(Paint.Style.FILL);
        orangeHeatmapPaint.setColor(context.getResources().getColor(R.color.orangeHeatmap));

        yellowHeatmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowHeatmapPaint.setStyle(Paint.Style.FILL);
        yellowHeatmapPaint.setColor(context.getResources().getColor(R.color.yellowHeatmap));

        greenHeatmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenHeatmapPaint.setStyle(Paint.Style.FILL);
        greenHeatmapPaint.setColor(context.getResources().getColor(R.color.greenHeatmap));

        blueHeatmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blueHeatmapPaint.setStyle(Paint.Style.FILL);
        blueHeatmapPaint.setColor(context.getResources().getColor(R.color.blueHeatmap));
    }

    public void addPointInBoundry(float x, float y){ pixelsInBoundry.add(new SignalPixel(x, y));}

    public void setPointsInBoundry(ArrayList<SignalPixel> points){ this. pixelsInBoundry = points;}

    public int pointInBoundry(float x, float y){
        SignalPixel tempPixel;
        for (int i = 0; i< pixelsInBoundry.size(); i++){
            tempPixel = pixelsInBoundry.get(i);
            if (x == tempPixel.x && y == tempPixel.y)
                return i;
        }
        return -1;
    }

    public void drawSquare(Canvas canvas, float x, float y){
        float loopStartX = x - 40;
        float loopStartY = y - 40;
        float loopX = loopStartX;
        float loopY = loopStartY;
        int idx;

        for (int i = 0; i<10; i++){
            for (int j = 0; j<10; j++){
                idx = this.pointInBoundry(loopX, loopY);
                if (idx != -1){
                    canvas.drawRect(loopX, loopY, loopX+10, loopY+10, redHeatmapPaint);
                }
                loopX+=10;
            }
            loopX=loopStartX;
            loopY+=10;
        }
    }

    public Paint getPaintFromLevel(int level){
        switch(level){
            case 6:
                return redHeatmapPaint;
            case 5:
                return orangeHeatmapPaint;
            case 4:
                return yellowHeatmapPaint;
            case 3:
                return greenHeatmapPaint;
            case 2:
                return blueHeatmapPaint;
            default:
                return blueHeatmapPaint;
        }
    }

    public void drawGradientCircle(Canvas canvas, int level, float x, float y){
        int holdLevel = level;

        ringOneDraw(canvas, level, holdLevel, x, y);
        ringTwoDraw(canvas, level, holdLevel, x, y);
        holdLevel = nextLevel(holdLevel);
        ringThreeDraw(canvas, level, holdLevel, x, y);
        holdLevel = nextLevel(holdLevel);
        ringFourDraw(canvas, level, holdLevel, x, y);
        holdLevel = nextLevel(holdLevel);

        if (level == 6){
            ringFiveOnlyWithSix(canvas, level, holdLevel, x, y);
            holdLevel = nextLevel(holdLevel);
            ringSixDraw(canvas, level, holdLevel, x, y);
        } else {
            ringFiveDraw(canvas, level, holdLevel, x, y);
        }

        /*
        switch (level){
            case 6:
                ringOneDraw(canvas, level, holdLevel, x, y);
                ringTwoDraw(canvas, level, holdLevel, x, y);
                ringThreeDraw(canvas, level, holdLevel-1, x, y);
                ringFourDraw(canvas, level, holdLevel-2, x, y);
                ringFiveDraw(canvas, level, holdLevel-3, x, y);
                break;
            case 5:
                ringOneDraw(canvas, level, holdLevel, x, y);
                ringTwoDraw(canvas, level, holdLevel, x, y);
                ringThreeDraw(canvas, level, holdLevel-1, x, y);
                ringFourDraw(canvas, level, holdLevel-2, x, y);
                ringFiveDraw(canvas, level, holdLevel-3, x, y);
                break;
            case 4:
                break;
            case 3:
                break;
            case 2:
                break;
            case 1:
                break;
            default:
                break;
        }*/

        /*
        ringOneDraw(canvas, level, holdLevel, x, y);
        holdLevel = nextLevel(holdLevel);
        ringTwoDraw(canvas, level, holdLevel, x, y);
        holdLevel = nextLevel(holdLevel);
        ringThreeDraw(canvas, level, holdLevel, x, y);
        holdLevel = nextLevel(holdLevel);
        ringFourDraw(canvas, level, holdLevel, x, y);
        holdLevel = nextLevel(holdLevel);
        ringFiveDraw(canvas, level, holdLevel, x, y);
        */
    }

    public void ringOneDraw(Canvas canvas, int centerLevel, int level, float x, float y){
        updateDrawPoints(canvas, centerLevel, level, x, y, DrawingInstructions.RING_ONE_INSTRUCTIONS);
    }

    public void ringTwoDraw(Canvas canvas, int centerLevel, int level, float x, float y){
        updateDrawPoints(canvas, centerLevel, level, x, y, DrawingInstructions.RING_TWO_INSTRUCTIONS);
    }

    public void ringThreeDraw(Canvas canvas, int centerLevel, int level, float x, float y){
        updateDrawPoints(canvas, centerLevel, level, x, y, DrawingInstructions.RING_THREE_INSTRUCTIONS);
    }

    public void ringFourDraw(Canvas canvas, int centerLevel, int level, float x, float y){
        updateDrawPoints(canvas, centerLevel, level, x, y, DrawingInstructions.RING_FOUR_INSTRUCTIONS);
    }

    public void ringFiveDraw(Canvas canvas, int centerLevel, int level, float x, float y){
        updateDrawPoints(canvas, centerLevel, level, x, y, DrawingInstructions.RING_FIVE_INSTRUCTIONS);
    }

    public void ringFiveOnlyWithSix(Canvas canvas, int centerLevel, int level, float x, float y){
        updateDrawPoints(canvas, centerLevel, level, x, y, DrawingInstructions.RING_FIVE_ONLY_WITH_SIX);
    }

    public void ringSixDraw(Canvas canvas, int centerLevel, int level, float x, float y){
        updateDrawPoints(canvas, centerLevel, level, x, y, DrawingInstructions.RING_SIX_INSTRUCTIONS);
    }

    private void updateDrawPoints(Canvas canvas, int centerLevel, int ringLevel, float x, float y, float[][] points){
        int idx, curPixLevel, drawLevel;
        SignalPixel curPix;
        float xHold = x, yHold = y;

        for (int i = 0; i < points.length; i++) {
            xHold += points[i][0];
            yHold += points[i][1];

            idx = this.pointInBoundry(xHold, yHold);
            if (idx != -1) {
                curPix = pixelsInBoundry.get(idx);
                curPixLevel = curPix.level;

                ////drawing logic////
                if (curPixLevel == 0 || curPixLevel < ringLevel) {
                    pixelsInBoundry.get(idx).level = ringLevel;
                    canvas.drawRect(xHold, yHold, xHold + 10, yHold + 10, getPaintFromLevel(ringLevel));
                } else if (curPixLevel > centerLevel && (ringLevel == 6 || ringLevel == 5)) {
                    pixelsInBoundry.get(idx).level = ringLevel;
                    canvas.drawRect(xHold, yHold, xHold + 10, yHold + 10, getPaintFromLevel(centerLevel));
                }
                ////end drawing logic////
            }
        }
    }

    private int nextLevel(int level){
        return level == 0 ? 0 : level-1;
    }
}
