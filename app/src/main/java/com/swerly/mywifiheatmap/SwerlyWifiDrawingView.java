package com.swerly.mywifiheatmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by sethw on 8/15/2016.
 */
public class SwerlyWifiDrawingView extends View {
    public static int DRAWING_DISTANCE = 100;
    public static int NUMBER_LEVELS = 6;

    private SignalPixelHelper signalPixelHelper;
    private SwerlyPolygon userArea;
    private Paint mPaint;
    private float currentCenterX;
    private float currentCenterY;
    private Bitmap bitmapBuffer;
    private Bitmap emptyBitmap;
    private Canvas canvasBuffer;
    private boolean heatmapBitmapDrawnOn = false;
    private int levelCycle = 6;

    private Paint canvasPaint;
    private Paint redHeatmapPaint;
    private Paint orangeHeatmapPaint;
    private Paint yellowHeatmapPaint;
    private Paint greenHeatmapPaint;
    private Paint blueHeatmapPaint;
    private Paint latestPaint;

    private int level;

    public SwerlyWifiDrawingView(Context context) {
        this(context,null);
    }
    public SwerlyWifiDrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }
    public SwerlyWifiDrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }
    public SwerlyWifiDrawingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(25.0f);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setColor(Color.RED);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        initializeWiFiListener();
    }

    public void setLevelCycle(int level){this.levelCycle = level;}
    public int getLevelCycle(){return this.levelCycle;}

    @Override
    protected void onDraw(Canvas canvas) {

        if (heatmapBitmapDrawnOn) {
            canvas.drawBitmap(bitmapBuffer, 0, 0, canvasPaint);
        }

    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!heatmapBitmapDrawnOn){
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    int w = SwerlyWifiDrawingView.this.getWidth(), h = SwerlyWifiDrawingView.this.getHeight();
                    bitmapBuffer = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
                    canvasBuffer = new Canvas(bitmapBuffer);
                    emptyBitmap = Bitmap.createBitmap(bitmapBuffer.getWidth(), bitmapBuffer.getHeight(), bitmapBuffer.getConfig());
                }

                float x = event.getX();
                float y = event.getY();
                float[] roundedTouch = getRoundedTouch(x,y);
                x = roundedTouch[0];
                y = roundedTouch[1];
                currentCenterX = x;
                currentCenterY = y;
                boolean containsPoint = userArea.contains(x,y);
                Log.d("debugActionDown", "touch in area: " + Boolean.toString(containsPoint));
                Log.d("debugActionDown", "touch location: " + Float.toString(x) + "    " + Float.toString(y));
                if (containsPoint) {
                    heatmapBitmapDrawnOn = true;
                    //canvasBuffer.drawRect(currentCenterX, currentCenterY, currentCenterX+100, currentCenterY+100, latestPaint);
                    //signalPixelHelper.drawSquare(canvasBuffer, currentCenterX, currentCenterY);
                    signalPixelHelper.drawGradientCircle(canvasBuffer, level, currentCenterX, currentCenterY);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x1 = event.getX();
                float y1 = event.getY();
                float[] roundedTouch1 = getRoundedTouch(x1,y1);
                x1 = roundedTouch1[0];
                y1 = roundedTouch1[1];
                currentCenterX = x1;
                currentCenterY = y1;
                boolean containsPoint1 = userArea.contains(x1,y1);
                Log.d("debugActionDown", "touch in area: " + Boolean.toString(containsPoint1));
                Log.d("debugActionDown", "touch location: " + Float.toString(x1) + "    " + Float.toString(y1));
                if (containsPoint1) {
                    //canvasBuffer.drawRect(currentCenterX, currentCenterY, currentCenterX+100, currentCenterY+100, latestPaint);
                    //signalPixelHelper.drawSquare(canvasBuffer, currentCenterX, currentCenterY);
                    signalPixelHelper.drawGradientCircle(canvasBuffer, level, currentCenterX, currentCenterY);
                    invalidate();
                }
                break;
        }
        return true;
    }

    public void setUserArea(SwerlyPolygon polygon){
        userArea = polygon;
    }

    public void setSignalPixelHelper(ArrayList<SignalPixel> pixels){
        this.signalPixelHelper = new SignalPixelHelper(this, pixels);
    }

    //parameter pos:
    // float[4]{
    //     x0, y0, x1, y1 };
    public float getDistance(float[] pos){
        float xDiff = pos[2]-pos[0];
        float yDiff = pos[3]-pos[1];
        return (float) Math.sqrt((xDiff*xDiff)+(yDiff*yDiff));
    }

    public float[] getRoundedTouch(float x, float y){
        int xTemp = (int) x;
        int yTemp = (int) y;

        xTemp -= xTemp % 10;
        yTemp -= yTemp % 10;

        return new float[]{ (float) xTemp, (float) yTemp };
    }

    private void initializeWiFiListener(){

        String connectivity_context = Context.WIFI_SERVICE;
        final WifiManager wifi = (WifiManager) getContext().getSystemService(connectivity_context);

        /*if(!wifi.isWifiEnabled()){
            if(wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING){
                wifi.setWifiEnabled(true);
            }
        }*/

        getContext().registerReceiver(new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                WifiInfo info = wifi.getConnectionInfo();
                level = WifiManager.calculateSignalLevel(info.getRssi(), NUMBER_LEVELS) + 1;

                switch (level){
                    case 6:
                        latestPaint = redHeatmapPaint;
                        break;
                    case 5:
                        latestPaint = orangeHeatmapPaint;
                        break;
                    case 4:
                        latestPaint = yellowHeatmapPaint;
                        break;
                    case 3:
                        latestPaint = greenHeatmapPaint;
                        break;
                    case 2:
                        latestPaint = blueHeatmapPaint;
                        break;
                    case 1:
                        latestPaint = blueHeatmapPaint;
                        break;
                }
                Log.d("debugSettingText", "setting level: " + Integer.toString(level));
            }

        }, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    public SwerlyPolygon getPolygon(){
        return this.userArea;
    }

}
