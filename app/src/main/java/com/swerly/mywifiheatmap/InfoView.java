package com.swerly.mywifiheatmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by sethw on 9/16/2016.
 */
public class InfoView extends RelativeLayout {
    private Activity context;
    private TextView headerText;
    private TextView bodyText;
    private CheckBox showAtNewMap;
    private ImageButton nextButton;
    private InfoScreen screenType;

    private SharedPreferences prefs;

    public enum InfoScreen{
        MAP,
        ZOOM,
        BOUNDRY,
        HEATMAP
    }

    public InfoView(Context context) {
        super(context);
        this.context = (Activity) context;
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = (Activity) context;
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = (Activity) context;


    }

    public void setupViewElements(InfoScreen infoScreen){
        String headString;
        String bodyString;

        inflate(context,R.layout.info_layout,this);
        headerText = (TextView) findViewById(R.id.info_header_text);
        bodyText = (TextView) findViewById(R.id.info_body_text);
        showAtNewMap = (CheckBox) findViewById(R.id.show_info_checkbox);
        nextButton = (ImageButton) findViewById(R.id.info_next_button);

        switch (infoScreen){
            case MAP:
                headString = context.getString(R.string.map_instructions_header);
                bodyString = context.getString(R.string.map_instructions_body);
                break;
            case ZOOM:
                headString = context.getString(R.string.zoom_instructions_header);
                bodyString = context.getString(R.string.zoom_instructions_body);
                break;
            case BOUNDRY:
                headString = context.getString(R.string.boundry_instructions_header);
                bodyString = context.getString(R.string.boundry_instructions_body);
                break;
            case HEATMAP:
                headString = context.getString(R.string.heatmap_instructions_header);
                bodyString = context.getString(R.string.heatmap_instructions_body);
                break;
            default:
                headString = "Why is this string here";
                bodyString = "Please notify the developer of his incompetence";
                break;
        }

        headerText.setText(headString);
        bodyText.setText(bodyString);

        showAtNewMap.post(new Runnable() {
            @Override
            public void run() {
                prefs = context.getSharedPreferences("com.swerly.animationstesting", Activity.MODE_PRIVATE);
                showAtNewMap.setChecked(prefs.getBoolean("showTips", true));
            }
        });

        showAtNewMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs = context.getSharedPreferences("com.swerly.animationstesting", Activity.MODE_PRIVATE);
                prefs.edit().putBoolean("showTips", b);
            }
        });

        nextButtonSetup();

        //TODO: use preferences to set this checkbox
        //showAtNewMap.setChecked(true);
    }

    public void animateOpenCenter(){
        int cx = context.getWindow().getDecorView().getWidth() / 2;
        int cy = context.getWindow().getDecorView().getHeight() / 2;

        animateOpen(cx, cy);
    }

    public void animateOpen(final int cx, final int cy){
        final float hypot = (float) Math.hypot(context.getWindow().getDecorView().getBottom(), context.getWindow().getDecorView().getRight());
        //boolean check = prefs.getBoolean("mapInstructionsShow", true);

        post(new Runnable() {
            @Override
            public void run() {
                Animator anim = ViewAnimationUtils.createCircularReveal(InfoView.this, cx, cy, 0, hypot);
                //showInfoCheckbox.setChecked(check);
                InfoView.this.setVisibility(View.VISIBLE);
                anim.start();
                InfoView.this.bringToFront();
            }
        });


    }

    public void animateCloseCenter(){
        int cx = context.getWindow().getDecorView().getWidth()/2;
        int cy = context.getWindow().getDecorView().getHeight()/2;
        float hypot = (float) Math.hypot(context.getWindow().getDecorView().getBottom(), context.getWindow().getDecorView().getRight());

        Animator anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, hypot, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                InfoView.this.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }
    public void animateClose(){
        int[] center = new int[2];
        nextButton.getLocationOnScreen(center);
        int cx = (center[0]+nextButton.getWidth()/2);
        int cy = (center[1]);

        float hypot = (float) Math.hypot(context.getWindow().getDecorView().getBottom(), context.getWindow().getDecorView().getRight());

        Animator anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, hypot, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                InfoView.this.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    private void nextButtonSetup(){
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                animateClose();
            }
        });
    }

    public boolean isOpen(){
        return getVisibility() == View.VISIBLE ? true : false;
    }
}
