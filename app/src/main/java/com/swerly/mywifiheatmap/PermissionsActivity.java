package com.swerly.mywifiheatmap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by sethw on 8/10/2016.
 */
public class PermissionsActivity extends AppCompatActivity {
    public static final int PERMISSIONS_GRANTED = 0;
    public static final int PERMISSIONS_DENIED = 1;

    private static final String PACKAGE_URL_SCHEME = "package:";
    private static final String PERMISSIONS = "PERMISSIONS";
    private static final int PERMISSION_REQUEST_CODE = 0;

    private PermissionsChecker checker;
    private boolean requiresCheck;

    public static void startActivityForResult(Activity activity, int requestCode, String[] permissions){
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() == null || !getIntent().hasExtra(PERMISSIONS)) {
            throw new RuntimeException("This Activity needs to be launched using the static startActivityForResult() method.");
        }

        setContentView(R.layout.activity_permissions);

        checker = new PermissionsChecker(this);
        requiresCheck = true;
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(requiresCheck){
            String[] permissions = getPermissions();

            if(checker.lacksPermissions(permissions)){
                requestPermissions(permissions);
            } else {
                allPermissionsGranted();
            }
        }
    }

    private String[] getPermissions(){
        return getIntent().getStringArrayExtra(PERMISSIONS);
    }

    private void allPermissionsGranted(){
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    private void requestPermissions(String[] permissions){
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)){
            requiresCheck = true;
            allPermissionsGranted();
        } else {
            requiresCheck = false;
            String deniedPermission = null;
            for (int i = 0; i<grantResults.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    Log.d("debugGrantResults", Integer.toString(i));
                    Log.d("debugPermissionResult", permissions[i]);
                    deniedPermission = permissions[i];
                }
            }

            showMissingPermissionsDialog(deniedPermission);
        }
    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults){
        for (int grantResult : grantResults){
            if(grantResult == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    private void showMissingPermissionsDialog(String deniedPermission){
        if (deniedPermission == null)
            Log.d("permission dialog debug", "permission is null");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PermissionsActivity.this);
        dialogBuilder.setTitle(R.string.permission_denied_title);
        dialogBuilder.setMessage(getDeniedMessage(deniedPermission));
        dialogBuilder.setNegativeButton(R.string.continue_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });
        dialogBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        dialogBuilder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    private String getDeniedMessage(String permission){
        if (permission == null)
            Log.d("permission debug", "permission is null");
        switch(permission){
            case android.Manifest.permission.ACCESS_FINE_LOCATION:
                return getString(R.string.fine_location_denied);
            case android.Manifest.permission.ACCESS_COARSE_LOCATION:
                return getString(R.string.fine_location_denied);
            default:
                Log.d("debug", "perm denied switch defualt");
                return "wat";
        }
    }
}
