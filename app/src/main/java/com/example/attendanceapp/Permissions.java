package com.example.attendanceapp;

import android.Manifest;
import android.app.Activity;
import android.os.Build;

import androidx.core.app.ActivityCompat;
/*
    Written by Noah Johnson, Jocelyn Chen, Gary Wu, Elin Yang, and Laura Villarreal
     for CS4485.0w1, senior design project, started February 11 2023
    NetIDs: ntj200000, jpc180002, gyw200000, yxy190022, lmv180001
 */
public class Permissions {
    private int targetSdkVersion;
    private static int REQUEST_ID_MULTIPLE_PERMISSIONS = 225;

    Permissions(int targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
    }

    private String[] getRequiredPermissions() {
//        int targetSdkVersion = getApplicationInfo().targetSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && targetSdkVersion >= Build.VERSION_CODES.S) {
            return new String[]{android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT};
        } else {
            return new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADMIN};
        }

//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetSdkVersion >= Build.VERSION_CODES.Q) {
//            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
//        } else return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    public boolean checkAndRequestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, getRequiredPermissions(), REQUEST_ID_MULTIPLE_PERMISSIONS);

        return true;
    }
}
