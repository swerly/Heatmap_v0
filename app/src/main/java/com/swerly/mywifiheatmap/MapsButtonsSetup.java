package com.swerly.mywifiheatmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.Image;
import android.transition.Transition;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by sethw on 8/11/2016.
 */
public class MapsButtonsSetup {
    private MapsActivity mapsActivity;

    InfoView mapInfoView;

    ImageButton expandSearchButton;
    ImageButton searchLocationButton;
    ImageButton getLocationButton;
    ImageButton nextButton;
    View nextLayout;
    View expandedSearchView;
    View nonexpandedSearchView;

    ImageButton mapInfoButton;
    SharedPreferences prefs = null;

    EditText addressEditText;

    public MapsButtonsSetup(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    public boolean runSetup() {

        //main buttons/views
        expandSearchButton = (ImageButton) mapsActivity.findViewById(R.id.expand_search_button);
        searchLocationButton = (ImageButton) mapsActivity.findViewById(R.id.search_button);
        getLocationButton = (ImageButton) mapsActivity.findViewById(R.id.get_location_button);
        nextButton = (ImageButton) mapsActivity.findViewById(R.id.maps_next_button);
        nextLayout = mapsActivity.findViewById(R.id.maps_next_layout);
        expandedSearchView = mapsActivity.findViewById(R.id.expanded_search_view);
        nonexpandedSearchView = mapsActivity.findViewById(R.id.search_view);

        //info screen buttons/views
        mapInfoView = (InfoView) mapsActivity.findViewById(R.id.map_info_view);
        mapInfoView.setupViewElements(InfoView.InfoScreen.MAP);
        mapInfoButton = (ImageButton) nonexpandedSearchView.findViewById(R.id.map_info_button);

        int sbHeight = getStatusBarHeight();
        nonexpandedSearchView.setY(sbHeight);
        expandedSearchView.setY(sbHeight);

        expandSearchButtonSetup();
        addressButtonSetup();
        nextButtonSetup();
        locationButtonSetup();
        setupInfoScreen();
        return true;
    }

    private boolean expandSearchButtonSetup(){
        expandSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateSearchOpen();
            }
        });
        return true;
    }

    private boolean addressButtonSetup() {
        //get address edit text ready for manipulation / use
        addressEditText = (EditText) mapsActivity.findViewById(R.id.address_edit_text2);
        addressEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addressGO();
                    return true;
                } else {
                    return false;
                }
            }
        });

        searchLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressGO();
            }
        });

        return true;
    }

    private boolean nextButtonSetup() {
        Log.d("debugNextButton", "setting up button...");
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* don't need these permissons right now
                String[] permissionsToCheck = {
                        WRITE_EXT_PERMISSION
                };

                if (checker.lacksPermissions(permissionsToCheck)) {
                    startPermissionsActivity(permissionsToCheck);
                }
                */
                Log.d("debugNextButton", "in the onclick");



                showZoomDialog();

            }
        });

        return true;
    }

    private boolean locationButtonSetup() {
        Log.d("debugLocationButton", "setting up...");
        //create and set the onclick listener
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    locationClick();
                }
        });


        return true;

    }

    public void locationClick() {
        Log.d("debugLocationButton", "inside onClick...");
        //start checking for location permissions to see if we want to display the my location button
        //add permissions to this array to check for them
        String[] permissionsToCheck = {
                MapsActivity.FINE_LOC_PERMISSION,
                MapsActivity.COARSE_LOC_PERMISSION
        };

        //start asking for permissions if we don't already have them
        if (mapsActivity.checker.lacksPermissions(permissionsToCheck)) {
            Log.d("debugLocationButton", "trying to check permissions...");
            mapsActivity.startPermissionsActivity(permissionsToCheck);
        } else {
            //don't need to check for permissions, start trying to get gps lock

            //let user know about what we're doing
            Toast toast = Toast.makeText(mapsActivity, "Waiting for GPS...", Toast.LENGTH_LONG);
            toast.show();

            //start getting gps coordinates
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    //let user know about what we're doing
                    Toast toast = Toast.makeText(mapsActivity, "GPS found, adjusting map view", Toast.LENGTH_LONG);
                    toast.show();
                    float latitude = (float) location.getLatitude();
                    float longitude = (float) location.getLongitude();

                    mapsActivity.swerlyMap.setLatLongView(latitude, longitude, SwerlyGoogleMap.USE_ZOOM_DEFAULT);
                    Log.d("debugLocationButton", "we got the location!");
                    Log.d("debugLocationButton", location.toString());
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(mapsActivity, locationResult);
        }
    }

    public void animateSearchClose(){
        if (expandedSearchView.getVisibility() == View.VISIBLE) {
            int cx = (int) ((int) nonexpandedSearchView.getX() + expandSearchButton.getX() + expandSearchButton.getWidth() / 2);
            int cy = (int) ((int) nonexpandedSearchView.getY() + expandSearchButton.getY() + expandSearchButton.getHeight() / 2);

            float initialRadius = Math.abs(expandedSearchView.getX() - cx);

            Animator anim = ViewAnimationUtils.createCircularReveal(expandedSearchView, cx, cy, initialRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    expandedSearchView.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();

            InputMethodManager inputMethodManager = (InputMethodManager) mapsActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mapsActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void animateSearchOpen(){
        int cx = (int) ((int) nonexpandedSearchView.getX() + expandSearchButton.getX() +  expandSearchButton.getWidth()/2);
        int cy = (int) ((int) nonexpandedSearchView.getY() + expandSearchButton.getY() +  expandSearchButton.getHeight()/2);

        float finalRadius = Math.abs(expandedSearchView.getX() - cx);

        Animator anim = ViewAnimationUtils.createCircularReveal(expandedSearchView, cx, cy, 0, finalRadius);

        expandedSearchView.setVisibility(View.VISIBLE);
        anim.start();
    }

    public boolean searchOpen(){
        return expandedSearchView.getVisibility() == View.VISIBLE;
    }
    public boolean infoOpen(){ return mapInfoView.isOpen();}

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mapsActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mapsActivity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupInfoScreen(){
        mapInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] center = new int[2];
                view.getLocationOnScreen(center);
                int cx = (center[0]+view.getWidth()/2);
                int cy = (center[1]);
                mapInfoView.animateOpen(cx, cy);
            }
        });
        /*
        //setup checkbox and preference saving
        showInfoCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Do first run stuff here then set 'firstrun' as false
                // using the following line to edit/commit prefs
                Log.d("debugCheck", Boolean.toString(b));
                prefs.edit().putBoolean("mapInstructionsShow", b).commit();
            }
        });*/
    }

    private void showZoomDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mapsActivity);
        builder.setTitle(mapsActivity.getString(R.string.zoom_dialog_title));
        builder.setMessage(mapsActivity.getString(R.string.zoom_dialog_body));

        builder.setPositiveButton(mapsActivity.getString(R.string.zoom_dialog_confirm),
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mapsActivity.swerlyMap.screenshotToZoom();
                    }
                });
        builder.setNegativeButton(mapsActivity.getString(R.string.zoom_dialog_deny),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int x = nonexpandedSearchView.getRight();
                        int y = nonexpandedSearchView.getTop();
                        mapsActivity.swerlyMap.screenshotToActivity(x, y);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void addressGO(){
        animateSearchClose();

        //get the value from the edit text
        String address = addressEditText.getText().toString();

        if (!address.equals("")) {
            //get the coordinates from the address
            float[] coords = mapsActivity.swerlyMap.getCoordFromAddress(mapsActivity, address);
            float latitude = coords[0];
            float longitude = coords[1];

            Log.d("coordinates", Float.toString(coords[0]) + ", " + Float.toString(coords[1]));
            //set the view to the new coordinates
            mapsActivity.swerlyMap.setLatLongView(latitude, longitude, SwerlyGoogleMap.USE_ZOOM_DEFAULT);

            //hide keyboard
            // Check if no view has focus:
            View mapView = mapsActivity.getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) mapsActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mapView.getWindowToken(), 0);
        }
    }

    public void animateInfoClose(){
        mapInfoView.animateCloseCenter();
    }
}
