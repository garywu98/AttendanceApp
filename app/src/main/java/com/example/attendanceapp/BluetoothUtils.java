package com.example.attendanceapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothUtils {
    private BluetoothAdapter mBluetoothAdapter;
    private List<String> discoveredDevicesAdapter;
    // holds the discoverable devices
    private ArrayList<String> discoverableDeviceList = new ArrayList<>();
    MainActivity main = null;

    private BluetoothThread btThread;
    private Handler handler;


    BluetoothUtils(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public void setMainActivity(MainActivity main) {
        this.main = main;
    }

    @SuppressLint("MissingPermission")
    public void visible(MainActivity activity) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);

        activity.startActivityForResult(getVisible, 0);
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("discoveryFinishReceiver ", "invoked");
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getName() != null) {
                    Log.d("discoveryFinishReceiver ", "here 1");
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("discoveryFinishReceiver ", "here 2");
                if (discoveredDevicesAdapter.size() == 0) {
                    discoveredDevicesAdapter.add("None Found");
                }
            }

//            FragmentManager fm = main.getSupportFragmentManager();
//            BluetoothFragment fragment = new BluetoothFragment(discoveredDevicesAdapter);
//            fm.beginTransaction().add(R.id.main_activity_container,fragment).commit();
            FragmentManager fm = main.getSupportFragmentManager();
            BluetoothFragment fragment = new BluetoothFragment(discoveredDevicesAdapter, mBluetoothAdapter, handler);
            FragmentTransaction ft = fm.beginTransaction();

            // add to stack so we can get back to main activity from the fragment
            ft.addToBackStack("bluetoothFragment");
            int backStackEntryCount = fm.getBackStackEntryCount();
            Log.d("fragmentStack", String.valueOf(backStackEntryCount));
            ft.add(R.id.main_activity_container,fragment).commit();
        }
    };

    @SuppressLint("MissingPermission")
    public void discoverDevices(MainActivity activity, Handler handler) {
        discoveredDevicesAdapter = new ArrayList<>();
        this.handler = handler;
        visible(activity);

        Log.d("discoverDevices ", "invoked");

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(discoveryFinishReceiver, filter);

        Log.d("discoverDevices ", "here");

        // Register for broadcasts when discovery has finished
        if(discoveredDevicesAdapter.size() != 0) {
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            activity.registerReceiver(discoveryFinishReceiver, filter);
        }

    }

    public void write(byte[] id) {
        btThread.write(id);
    }
    
    @SuppressLint("MissingPermission")
    protected void destroy(Activity activity) {
        Log.e("Destroy ", "Destroyed");
        try {
            activity.unregisterReceiver(discoveryFinishReceiver);
            mBluetoothAdapter.cancelDiscovery();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
