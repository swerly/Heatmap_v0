package com.swerly.mywifiheatmap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class WifiMappingActivity extends AppCompatActivity {

    SwerlyWifiBackgroundView backgroundImageView;
    SwerlyWifiDrawingView drawingView;
    Bitmap currentViewBitmap;
    SwerlyLineList lineList;
    View heatmapContainer;

    InfoView heatmapInfoView;
    ImageButton heatmapInfoButton;
    View nextButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_mapping);

        getSupportActionBar().hide();

        backgroundImageView = (SwerlyWifiBackgroundView) findViewById(R.id.wifi_background_image_view);
        drawingView = (SwerlyWifiDrawingView) findViewById(R.id.wifi_drawing_view);

        byte[] byteArray = getIntent().getByteArrayExtra("screenshotByteArray");
        currentViewBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        backgroundImageView.setImageBitmap(currentViewBitmap);

        lineList = (SwerlyLineList) getIntent().getSerializableExtra("swerlyLineList");
        backgroundImageView.setLineList(lineList);

        float[][] points = lineList.getPoints();
        SwerlyPolygon sPolygon = new SwerlyPolygon(this, points[0], points[1], lineList.getNumLines());
        drawingView.setUserArea(sPolygon);
        drawingView.setSignalPixelHelper(sPolygon.getPointsInBoundry());

        heatmapContainer = findViewById(R.id.heatmap_container);
        setupWindowAnimations();

        heatmapInfoButton = (ImageButton) findViewById(R.id.heatmap_info_button);
        heatmapInfoView = (InfoView) findViewById(R.id.heatmap_info_view);
        heatmapInfoView.setupViewElements(InfoView.InfoScreen.HEATMAP);
        setupInfoScreen();

        nextButtonLayout = findViewById(R.id.heatmap_next_button_layout);
        final SharedPreferences prefs =getSharedPreferences("com.swerly.animationstesting", Activity.MODE_PRIVATE);
        heatmapInfoView = (InfoView) findViewById(R.id.heatmap_info_view);
        heatmapInfoView.post(new Runnable() {
            @Override
            public void run() {
                if (prefs.getBoolean("showTips", true) == true){
                    heatmapInfoView.animateOpenCenter();
                }
            }
        });


    }

    @Override
    public void onBackPressed(){
        if (heatmapInfoView.isOpen()) {
            heatmapInfoView.animateCloseCenter();
        } else {
            finish();
        }
    }

    private void setupWindowAnimations() {
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);

        //exit
        Slide exitTrans = new Slide();
        exitTrans.setSlideEdge(Gravity.LEFT);
        exitTrans.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Animation fadeOut = AnimationUtils.loadAnimation(WifiMappingActivity.this, R.anim.fadeout);
                heatmapContainer.startAnimation(fadeOut);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                heatmapContainer.setAlpha(0f);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        getWindow().setExitTransition(exitTrans);

        //reenter
        Slide reenterTrans = new Slide();
        reenterTrans.setSlideEdge(Gravity.LEFT);
        reenterTrans.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Animation fadeIn = AnimationUtils.loadAnimation(WifiMappingActivity.this, R.anim.fadein);
                heatmapContainer.startAnimation(fadeIn);
                heatmapContainer.setAlpha(1f);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        getWindow().setReenterTransition(reenterTrans);

        //enter
        Slide enterTrans = new Slide();
        enterTrans.setSlideEdge(Gravity.RIGHT);
        enterTrans.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                heatmapContainer.setAlpha(0f);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Animation fadeIn = AnimationUtils.loadAnimation(WifiMappingActivity.this, R.anim.fadein);
                heatmapContainer.startAnimation(fadeIn);
                heatmapContainer.setAlpha(1f);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        getWindow().setEnterTransition(enterTrans);

        //return
        Slide returnTrans = new Slide();
        returnTrans.setSlideEdge(Gravity.RIGHT);
        returnTrans.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Animation fadeOut = AnimationUtils.loadAnimation(WifiMappingActivity.this, R.anim.fadeout);
                heatmapContainer.startAnimation(fadeOut);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                heatmapContainer.setAlpha(0f);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        getWindow().setReturnTransition(returnTrans);
    }

    private void setupInfoScreen(){
        heatmapInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] center = new int[2];
                view.getLocationInWindow(center);
                int x = center[0];
                int y = center[1];
                heatmapInfoView.animateOpen(x,y);
            }
        });
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
