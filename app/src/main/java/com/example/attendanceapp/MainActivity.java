package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothUtils bluetoothUtils;
    private Permissions perm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        try {
//            BluetoothThread thread = new BluetoothThread();
//
//            Thread bThread = new Thread(thread);
//            bThread.start();
//        } catch (Exception e) {
//
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothUtils = new BluetoothUtils(mBluetoothAdapter);
        perm = new Permissions(getApplicationInfo().targetSdkVersion);

        if (mBluetoothAdapter == null) {
            //If bluetooth is not supported
            Log.d("Bluetooth ", " is not supported on this device");
            finish();
        } else {
            int REQUEST_ENABLE_BT = 1;

            Log.d("Bluetooth ", " turning on");

            if (!mBluetoothAdapter.isEnabled()) {
                //if bluetooth is not enabled, enable it
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                Log.d("Bluetooth ", " is now turned on");
            }
        }

        if(perm != null) perm.checkAndRequestPermissions(this);
        else {
            Log.d("onStart ", "Perm is null");
//            finish();
        }

        super.onStart();
    }

    public void discoverDevices(View view) {
        bluetoothUtils.discoverDevices(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, permissions[i], Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothUtils.destroy(this);
    }
}
