package com.swerly.mywifiheatmap;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created by sethw on 8/11/2016.
 */
public class BoundryActivity extends AppCompatActivity {
    private Bitmap currentViewBitmap;
    private SwerlyBoundryView boundryImageView;
    private BoundryButtonsSetup bSetup;

    InfoView boundaryInfoView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boundry);

        getSupportActionBar().hide();
        setupWindowAnimations();

        boundryImageView = (SwerlyBoundryView) findViewById(R.id.boundry_image_view);


        byte[] byteArray = getIntent().getByteArrayExtra("screenshotByteArray");
        Log.d("debugBoundryBitmap", Integer.toString(byteArray.length));
        currentViewBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        boundryImageView.setImageBitmap(currentViewBitmap);

        bSetup = new BoundryButtonsSetup(this);
        bSetup.setupButtons();

        setupWindowAnimations();
        setupNextUndoButton();
        setupInfoView();
    }

    @Override
    public void onBackPressed(){
        if (bSetup.infoOpen()) {
            bSetup.animateInfoClose();
        } else {
            finish();
        }
    }

    private void setupWindowAnimations() {
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);

        //enter
        Slide enterTrans = new Slide();
        enterTrans.setSlideEdge(Gravity.RIGHT);
        enterTrans.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                boundryImageView.setAlpha(0f);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Animation fadeIn = AnimationUtils.loadAnimation(BoundryActivity.this, R.anim.fadein);
                boundryImageView.startAnimation(fadeIn);
                boundryImageView.setAlpha(1f);
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
                Animation fadeOut = AnimationUtils.loadAnimation(BoundryActivity.this, R.anim.fadeout);
                boundryImageView.startAnimation(fadeOut);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                boundryImageView.setAlpha(0f);
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


        //exit
        Slide exitTrans = new Slide();
        exitTrans.setSlideEdge(Gravity.LEFT);
        exitTrans.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Animation fadeOut = AnimationUtils.loadAnimation(BoundryActivity.this, R.anim.fadeout);
                boundryImageView.startAnimation(fadeOut);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                boundryImageView.setAlpha(0f);
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
                Animation fadeIn = AnimationUtils.loadAnimation(BoundryActivity.this, R.anim.fadein);
                boundryImageView.startAnimation(fadeIn);
                boundryImageView.setAlpha(1f);
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

    }

    private void setupNextUndoButton(){
        final ImageButton boundryUndoButton = (ImageButton) findViewById(R.id.boundry_undo_button);
        ImageButton boundryNextButton = (ImageButton) findViewById(R.id.boundry_next_button);

        boundryUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boundryImageView.hasLinesToDraw()){
                    boundryImageView.undoLine();
                }
            }
        });

        boundryNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(boundryImageView.isPathComplete()) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    currentViewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(BoundryActivity.this, WifiMappingActivity.class);
                    intent.putExtra("screenshotByteArray", byteArray);
                    intent.putExtra("swerlyLineList", boundryImageView.getLineList());

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(BoundryActivity.this);

                    Log.d("debugPostionNext", "starting boundry activity");
                    BoundryActivity.this.startActivity(intent, options.toBundle());
                } else {
                    Toast.makeText(BoundryActivity.this, "Path is not closed", Toast.LENGTH_LONG)
                            .show();
                }
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

    private void setupInfoView(){
        final SharedPreferences prefs =getSharedPreferences("com.swerly.animationstesting", Activity.MODE_PRIVATE);
        boundaryInfoView = (InfoView) findViewById(R.id.boundry_info_view);
        boundaryInfoView.post(new Runnable() {
            @Override
            public void run() {
                if (prefs.getBoolean("showTips", true) == true){
                    boundaryInfoView.animateOpenCenter();
                }
            }
        });
    }
}
