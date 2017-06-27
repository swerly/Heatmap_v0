package com.swerly.mywifiheatmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by sethw on 8/29/2016.
 */
public class BoundryButtonsSetup {
    BoundryActivity context;

    //main buttons/views
    ImageButton boundryNextButton;
    ImageButton expandSettings;
    ImageButton closeSettings;
    View nextButtonView;
    View nonExpandedSearchView;
    View expandedSearchView;

    //info screen
    ImageButton boundryInfoButton;
    InfoView boundryInfoView;
    SharedPreferences prefs = null;


    public BoundryButtonsSetup(BoundryActivity context){
        this.context = context;
    }
    public void setupButtons(){
        //expandSettings = (ImageButton) context.findViewById(R.id.expand_settings_button);
        boundryNextButton = (ImageButton) context.findViewById(R.id.boundry_next_button);
        closeSettings = (ImageButton) context.findViewById(R.id.expanded_settings_button);
        nextButtonView = context.findViewById(R.id.boundry_next_layout);
        nonExpandedSearchView = context.findViewById(R.id.boundry_info_settings_card);
        //expandedSearchView = context.findViewById(R.id.boundry_expanded_settings_panel);

        //info screen
        boundryInfoView = (InfoView) context.findViewById(R.id.boundry_info_view);
        boundryInfoView.setupViewElements(InfoView.InfoScreen.BOUNDRY);
        boundryInfoButton = (ImageButton) nonExpandedSearchView.findViewById(R.id.boundry_info_button);

        int sbHeight = getStatusBarHeight();
        nonExpandedSearchView.setY(sbHeight);
        /*expandedSearchView.setY(sbHeight);

        expandSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cx = (int) (closeSettings.getX()+closeSettings.getWidth()/2);
                int cy = (int) (closeSettings.getY()+closeSettings.getHeight()/2);

                float hypot = (float) Math.hypot(context.getWindow().getDecorView().getBottom(), context.getWindow().getDecorView().getRight());

                Animator anim = ViewAnimationUtils.createCircularReveal(expandedSearchView, cx, cy, 0, hypot);
                expandedSearchView.setVisibility(View.VISIBLE);
                anim.start();
            }
        });

        closeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cx = (int) (closeSettings.getX()+closeSettings.getWidth()/2);
                int cy = (int) (closeSettings.getY()+closeSettings.getHeight()/2);

                float hypot = (float) Math.hypot(context.getWindow().getDecorView().getBottom(), context.getWindow().getDecorView().getRight());

                Animator anim = ViewAnimationUtils.createCircularReveal(expandedSearchView, cx, cy, hypot, 0);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        expandedSearchView.setVisibility(View.INVISIBLE);
                    }
                });
                anim.start();
            }
        });
        */
        prefs = context.getSharedPreferences("com.swerly.animationstesting", Activity.MODE_PRIVATE);
        setupInfoScreen();
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupInfoScreen(){

        //setup the info button to open the info view
        boundryInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] center = new int[2];
                boundryInfoButton.getLocationOnScreen(center);
                int cx = (center[0]+boundryInfoButton.getWidth()/2);
                int cy = (center[1]+boundryInfoButton.getHeight()/2);
                boundryInfoView.animateOpen(cx, cy);
            }
        });
    }

    public boolean infoOpen(){
        return boundryInfoView.getVisibility() == View.VISIBLE ? true : false;
    }

    public void animateInfoClose(){
        boundryInfoView.animateCloseCenter();
    }

}
