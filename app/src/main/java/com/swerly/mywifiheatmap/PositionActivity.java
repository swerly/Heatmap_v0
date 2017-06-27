package com.swerly.mywifiheatmap;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;

public class PositionActivity extends AppCompatActivity {
    private Bitmap currentViewBitmap;
    protected SwerlyPhotoView photoView;
    private final Handler handler = new Handler();

    private View positionView;
    private ImageButton positionNextButton;
    private ImageButton positionInfoButton;

    private InfoView positionInfoView;
    private View positionNextButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        getSupportActionBar().hide();

        byte[] byteArray = getIntent().getByteArrayExtra("screenshotByteArray");
        currentViewBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        photoView = (SwerlyPhotoView) findViewById(R.id.position_photo_view);
        photoView.setImageBitmap(currentViewBitmap);

        positionView = findViewById(R.id.position_main_view);
        positionNextButton = (ImageButton) findViewById(R.id.position_next_button);
        positionInfoButton = (ImageButton) findViewById(R.id.position_info_button);
        positionInfoView = (InfoView) findViewById(R.id.position_info_view);
        positionInfoView.setupViewElements(InfoView.InfoScreen.ZOOM);
        positionNextButtonLayout = findViewById(R.id.position_next_button_layout);
        positionNextButtonLayout.setY(getStatusBarHeight());
        setupInfoScreen();
        setupNextButton();
        setupWindowAnimations();
    }

    @Override
    protected void onPause(){
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed(){
        if (positionInfoView.isOpen()) {
            positionInfoView.animateCloseCenter();
            Animation fadeIn = AnimationUtils.loadAnimation(PositionActivity.this, R.anim.fadein);
            positionNextButton.startAnimation(fadeIn);
            positionNextButton.setEnabled(true);
        } else {
            finish();
        }
    }

    private void setupInfoScreen(){
        positionInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] center = new int[2];
                view.getLocationInWindow(center);
                int x = center[0];
                int y = center[1];

                Animation fadeOut = AnimationUtils.loadAnimation(PositionActivity.this, R.anim.fadeout);
                positionNextButton.startAnimation(fadeOut);
                positionNextButton.setEnabled(false);
                positionInfoView.animateOpen(x,y);
            }
        });

        final SharedPreferences prefs =getSharedPreferences("com.swerly.animationstesting", Activity.MODE_PRIVATE);
        positionInfoView = (InfoView) findViewById(R.id.position_info_view);
        positionInfoView.post(new Runnable() {
            @Override
            public void run() {
                if (prefs.getBoolean("showTips", true) == true){
                    positionInfoView.animateOpenCenter();
                }
            }
        });
    }

    private void setupNextButton(){
        positionNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoView.setDrawingCacheEnabled(true);
                photoView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
                photoView.buildDrawingCache();
                if (photoView.getDrawingCache() != null){
                    Bitmap newViewBitmap = Bitmap.createBitmap(photoView.getDrawingCache());
                    photoView.setDrawingCacheEnabled(false);
                    photoView.destroyDrawingCache();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    newViewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(PositionActivity.this, BoundryActivity.class);
                    intent.putExtra("screenshotByteArray", byteArray);

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PositionActivity.this);

                    PositionActivity.this.startActivity(intent, options.toBundle());
                }
            }
        });
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
                Animation fadeOut = AnimationUtils.loadAnimation(PositionActivity.this, R.anim.fadeout);
                photoView.startAnimation(fadeOut);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                photoView.setAlpha(0f);
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
                Animation fadeIn = AnimationUtils.loadAnimation(PositionActivity.this, R.anim.fadein);
                photoView.startAnimation(fadeIn);
                photoView.setAlpha(1f);
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
                photoView.setAlpha(0f);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Animation fadeIn = AnimationUtils.loadAnimation(PositionActivity.this, R.anim.fadein);
                photoView.startAnimation(fadeIn);
                photoView.setAlpha(1f);
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
                Animation fadeOut = AnimationUtils.loadAnimation(PositionActivity.this, R.anim.fadeout);
                photoView.startAnimation(fadeOut);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                photoView.setAlpha(0f);
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
