package com.swerly.mywifiheatmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by sethw on 8/11/2016.
 */
public class SwerlyBoundryView extends ImageView {
    public static int DISTANCE_THRESHOLD = 75;
    public static int DISTANCE_TO_START_THRESHOLD = 150;
    private static int PSUEDO_NAN = -6969;

    private SwerlyLineList lineList;

    private final Paint mPaint;
    private float latestX = PSUEDO_NAN;
    private float latestY = PSUEDO_NAN;
    private float holdLatestX = PSUEDO_NAN;
    private float holdLatestY = PSUEDO_NAN;
    private float distStartMesX = 0;
    private float distStartMesY = 0;
    private float currentDist = 0;
    private boolean pathClosed = false;

    public SwerlyBoundryView(Context context) {
        this(context,null);
    }

    public SwerlyBoundryView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwerlyBoundryView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public SwerlyBoundryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(25.0f);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        boolean latestNaN = (latestX == PSUEDO_NAN) && (latestY == PSUEDO_NAN);
        boolean pathExists = lineList == null ? false : lineList.linesExist();

        if (pathExists){
            if(lineList.isPathClosed()){
                pathClosed = true;
                Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                fillPaint.setStyle(Paint.Style.FILL);
                fillPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.areaBoundryFill));
                canvas.drawPath(lineList.getAsPath(), fillPaint);
                canvas.drawPath(lineList.getAsPath(), mPaint);
                return;
            }
        }
        if (!latestNaN && !pathExists){
            canvas.drawLine(distStartMesX, distStartMesY, latestX, latestY, mPaint);
        } else if (!latestNaN && pathExists){
            Path drawPath = lineList.getAsPath();
            float distanceToStart = getDistanceToStart(latestX, latestY, lineList.getStartPoint().x, lineList.getStartPoint().y);
            if (distanceToStart < DISTANCE_TO_START_THRESHOLD) {
                latestX = lineList.getStartPoint().x;
                latestY = lineList.getStartPoint().y;
            }
            drawPath.lineTo(latestX, latestY);
            canvas.drawPath(drawPath, mPaint);
        } else if (latestNaN && !pathExists){
            //do nothing
        } else {
            Path drawPath = lineList.getAsPath();
            canvas.drawPath(drawPath, mPaint);
        }
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                boolean pathExists = lineList == null ? false : lineList.linesExist();
                if (pathExists){
                    SwerlyLine endingPathPoints = lineList.getLastLineEnd();
                    distStartMesX = endingPathPoints.x;
                    distStartMesY = endingPathPoints.y;
                } else {
                    distStartMesX = event.getX();
                    distStartMesY = event.getY();
                }

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                latestX = event.getX();
                latestY = event.getY();

                //check if the path length is greater than the drawing threshold
                float[] points = new float[]{
                        distStartMesX, distStartMesY, latestX, latestY
                };
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                latestX = event.getX();
                latestY = event.getY();

                float[] points1 = new float[]{
                        distStartMesX, distStartMesY,
                        latestX, latestY
                };
                currentDist = getDistance(points1);
                if (currentDist > DISTANCE_THRESHOLD) {
                    if ( lineList != null && lineList.linesExist()) {
                        float distanceToStart = getDistanceToStart(latestX, latestY, lineList.getStartPoint().x, lineList.getStartPoint().y);
                        if (distanceToStart < DISTANCE_TO_START_THRESHOLD) {
                            latestX = lineList.getStartPoint().x;
                            latestY = lineList.getStartPoint().y;
                        }
                    }

                    holdLatestX = latestX;
                    holdLatestY = latestY;

                    //TODO: add to path list
                    if (lineList == null){
                        lineList = new SwerlyLineList(new SwerlyLine(distStartMesX, distStartMesY));
                    }
                    if(!pathClosed)
                        lineList.addLine(new SwerlyLine(latestX, latestY));
                }

                latestX = PSUEDO_NAN;
                latestY = PSUEDO_NAN;

                invalidate();
                break;
        }

        return true;
    }

    //parameter pos:
    // float[4]{
    //     x0, y0, x1, y1 };
    public float getDistance(float[] pos){
        float xDiff = pos[2]-pos[0];
        float yDiff = pos[3]-pos[1];
        return (float) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
    }

    private float getDistanceToStart(float lx, float ly, float ex, float ey){
        float xDiff = lx - ex;
        float yDiff = ly - ey;
        float distanceToStart = (float)Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
        return distanceToStart;
    }

    public boolean hasLinesToDraw(){
        return lineList == null ? false : lineList.linesExist();
    }

    public void undoLine(){
        lineList.removeLatest();
        if(!lineList.linesExist()){
            lineList = null;
        }
        pathClosed = false;
        invalidate();
    }

    public boolean isPathComplete(){ return lineList == null ? false : lineList.isPathClosed();}

    public SwerlyLineList getLineList(){return this.lineList;}
}


