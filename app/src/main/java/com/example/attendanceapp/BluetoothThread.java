package com.example.attendanceapp;


import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.logging.Handler;

public class BluetoothThread implements Runnable {

    @Override
    public void run() {
        Log.d("Here", " Thread is created");


    }
}
