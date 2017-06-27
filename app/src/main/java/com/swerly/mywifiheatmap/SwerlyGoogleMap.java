package com.swerly.mywifiheatmap;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by sethw on 8/9/2016.
 */
public class SwerlyGoogleMap {
    public static int USE_ZOOM_DEFAULT = -8;
    public static int USE_ZOOM_OUT = -99;

    private GoogleMap mMap;
    private MapsActivity context;

    public SwerlyGoogleMap(MapsActivity context){
        this.context = context;
        mMap = null;
    }

    public GoogleMap getGoogleMap(){
        return mMap;
    }
    public void setGoogleMap(GoogleMap map){
        this.mMap = map;
    }

    public void setLatLongView(float lat, float longi, float zoom){
        LatLng usa = new LatLng(lat, longi);
        mMap.clear();

        float zoomLevel;

        if (zoom == USE_ZOOM_DEFAULT)
            zoomLevel = mMap.getMaxZoomLevel()-2;
        else
            zoomLevel = mMap.getMinZoomLevel()+2;
        Log.d("mapZoomLevel", String.valueOf(zoomLevel));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usa, zoomLevel));
    }

    public float[] getCoordFromAddress(Context context, String address){
        Geocoder geocoder = new Geocoder(context);
        float latitude = -1;
        float longitude = -1;

        List<android.location.Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses.size() > 0) {
            latitude = (float) addresses.get(0).getLatitude();
            longitude = (float) addresses.get(0).getLongitude();
        }

        return new float[]{latitude, longitude};

    }

    public void screenshotToActivity(final int x, final int y){
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context);
                Intent intent = new Intent(context, BoundryActivity.class);//Convert to byte array

                //******************************************
                //add the bundle extras
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("screenshotByteArray", byteArray);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                //******************************************

                context.startActivity(intent, options.toBundle());
            }
        };
        mMap.snapshot(callback);
    }

    public void screenshotToZoom(){
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context);
                Intent intent = new Intent(context, PositionActivity.class);//Convert to byte array

                //******************************************
                //add the bundle extras
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("screenshotByteArray", byteArray);
                //******************************************

                context.startActivity(intent, options.toBundle());
            }
        };
        mMap.snapshot(callback);
    }
}
