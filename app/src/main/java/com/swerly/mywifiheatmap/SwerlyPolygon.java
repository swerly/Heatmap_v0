package com.swerly.mywifiheatmap;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by sethw on 8/15/2016.
 */
public class SwerlyPolygon {
    // Polygon coodinates.
    private float[] polyY, polyX;
    private Context context;

    // Number of sides in the polygon.
    private int polySides;

    /**
     * Default constructor.
     * @param px Polygon y coods.
     * @param py Polygon x coods.
     * @param ps Polygon sides count.
     */
    public SwerlyPolygon(Context context, float[] px, float[] py, int ps )
    {
        this.context = context;
        polyX = px;
        polyY = py;
        polySides = ps;
    }

    //public ArrayList<SignalPixel> getPixelList(){ return pointsInBoundry; }

    public ArrayList<SignalPixel> getPointsInBoundry(){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float xDim = metrics.widthPixels;
        float yDim = metrics.heightPixels;

        ArrayList<SignalPixel> listOfSignalPixels = new ArrayList<>();

        for (int i = 0; i < xDim; i+=10){
            for (int j = 0; j < yDim; j+=10){
                if (contains(i,j)){
                    listOfSignalPixels.add(new SignalPixel(i,j));
                }
            }
        }
        return listOfSignalPixels;
    }
    /**
     * Checks if the Polygon contains a point.
     * @see "http://alienryderflex.com/polygon/"
     * @param x Point horizontal pos.
     * @param y Point vertical pos.
     * @return Point is in Poly flag.
     */
    public boolean contains( float x, float y )
    {
        boolean c = false;
        int i, j;

        for (i = 0, j = polySides - 1; i < polySides; j = i++) {
            if (((polyY[i] > y) != (polyY[j] > y))
                    && (x < (polyX[j] - polyX[i]) * (y - polyY[i]) / (polyY[j] - polyY[i]) + polyX[i]))
                c = !c;
        }
        return c;
    }

}
