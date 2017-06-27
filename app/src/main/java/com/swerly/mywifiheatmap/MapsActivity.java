package com.swerly.mywifiheatmap;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String FINE_LOC_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOC_PERMISSION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String WRITE_EXT_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE = 0;

    private View mapFragmentContainer;
    private SupportMapFragment mapFragment;

    private MapsButtonsSetup mapsButtonsSetup;

    protected SwerlyGoogleMap swerlyMap;
    protected PermissionsChecker checker;

    private InfoView mapInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapFragmentContainer = findViewById(R.id.map_fragment_container);

        checker = new PermissionsChecker(this);
        swerlyMap = new SwerlyGoogleMap(this);

        mapsButtonsSetup = new MapsButtonsSetup(this);
        mapsButtonsSetup.runSetup();

        setupWindowAnimations();

        setupInfoView();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //create a temp map to modify here
        GoogleMap mMap = googleMap;

        //start setting up the map
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(true);

        swerlyMap.setGoogleMap(mMap);
        swerlyMap.setLatLongView( 37, -104, SwerlyGoogleMap.USE_ZOOM_OUT);
    }

    public void startPermissionsActivity(String[] permissionsToCheck){
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, permissionsToCheck);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //do something here if gps was enabled
    }

    @Override
    public void onBackPressed(){
        if (mapsButtonsSetup.searchOpen()){
            mapsButtonsSetup.animateSearchClose();
        } else if (mapsButtonsSetup.infoOpen()) {
            mapsButtonsSetup.animateInfoClose();
        } else {
            finish();
        }
    }

    private void setupWindowAnimations(){
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);

        Slide exitTrans = new Slide();
        exitTrans.setSlideEdge(Gravity.LEFT);
        exitTrans.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Animation fadeOut = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.fadeout);
                mapFragmentContainer.startAnimation(fadeOut);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mapFragmentContainer.setAlpha(0f);
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

        Slide reenterTrans = new Slide();
        reenterTrans.setSlideEdge(Gravity.LEFT);
        reenterTrans.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Animation fadeIn = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.fadein);
                mapFragmentContainer.startAnimation(fadeIn);
                mapFragmentContainer.setAlpha(1f);
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

    private void setupInfoView(){
        final SharedPreferences prefs =getSharedPreferences("com.swerly.animationstesting", Activity.MODE_PRIVATE);
        mapInfoView = (InfoView) findViewById(R.id.map_info_view);
        mapInfoView.post(new Runnable() {
            @Override
            public void run() {
                if (prefs.getBoolean("showTips", true) == true){
                    mapInfoView.animateOpenCenter();
                }
            }
        });
    }

}
