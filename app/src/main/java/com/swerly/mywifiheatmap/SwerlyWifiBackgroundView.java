package com.swerly.mywifiheatmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by sethw on 8/15/2016.
 */
public class SwerlyWifiBackgroundView extends ImageView {
    private SwerlyLineList swerlyLineList;

    public SwerlyWifiBackgroundView(Context context) {this(context,null);}
    public SwerlyWifiBackgroundView(Context context, AttributeSet attrs) {this(context, attrs, -1);}
    public SwerlyWifiBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }
    public SwerlyWifiBackgroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setLineList(SwerlyLineList lineList){ this.swerlyLineList = lineList;}
    public SwerlyLineList getLineList(){return swerlyLineList;}

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Path drawPath = swerlyLineList.getAsPath();
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(25.0f);
        strokePaint.setStrokeCap(Paint.Cap.SQUARE);
        strokePaint.setColor(Color.RED);
        Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(ContextCompat.getColor(this.getContext(), R.color.areaBoundryFill));
        canvas.drawPath(drawPath, fillPaint);
        canvas.drawPath(drawPath, strokePaint);
    }
}
