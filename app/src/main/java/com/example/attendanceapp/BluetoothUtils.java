package com.example.attendanceapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.List;

/*
    Written by Noah Johnson, Jocelyn Chen, Gary Wu, Elin Yang, and Laura Villarreal
     for CS4485.0w1, senior design project, started February 11 2023
    NetIDs: ntj200000, jpc180002, gyw200000, yxy190022, lmv180001
 */

/**
 * Gets the possible list of devices
 * Makes the phone running the app discoverable to other bluetooth devices
 */
public class BluetoothUtils {
    private BluetoothAdapter mBluetoothAdapter;
    private List<String> discoveredDevicesAdapter;

    // holds the discoverable devices
    private ArrayList<String> discoverableDeviceList = new ArrayList<>();
    MainActivity main = null;

    /**
     * Overloaded constructor
     * @param mBluetoothAdapter
     */
    BluetoothUtils(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public void setMainActivity(MainActivity main) {
        this.main = main;
    }

    /**
     * making our phone visible to other bluetooth devices
     * @param activity - grabbing the activity that is being run currently
     */
    @SuppressLint("MissingPermission")
    public void visible(MainActivity activity) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // makes phone discoverable for 180 seconds
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);

        activity.startActivityForResult(getVisible, 0);
    }

    /**
     * instance of BroadcastReceiver class
     * listens for potential devices to connect to
     */
    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {

        /**
         * when the broadcast receiver gets information regarding a bluetooth device,
         * this function determines what to do with that information
         * @param context
         * @param intent
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // if a remote device is discovered, display it if its non-null
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // filters out device names that contain the word 'null'
                if (device.getName() != null) {
                    // adds the device if it is non-null
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // when the receiver is finished and no devices were found, let the user know
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.size() == 0) {
                    discoveredDevicesAdapter.add("None Found");
                }
            }

            // invoke the BluetoothFragment that will display the discoverable devices
            FragmentManager fm = main.getSupportFragmentManager();
            BluetoothFragment fragment = new BluetoothFragment(discoveredDevicesAdapter, mBluetoothAdapter);
            FragmentTransaction ft = fm.beginTransaction();

            // add fragment to stack so we can get back to main activity from the fragment
            ft.addToBackStack("bluetoothFragment");
            int backStackEntryCount = fm.getBackStackEntryCount();
            Log.d("fragmentStack", String.valueOf(backStackEntryCount));
            ft.add(R.id.main_activity_container,fragment).commit();
        }
    };

    /**
     * called from MainActivity
     * starts up the broadcast receiver to look for devices
     * @param activity - context for MainActivity
     */
    @SuppressLint("MissingPermission")
    public void discoverDevices(MainActivity activity) {
        discoveredDevicesAdapter = new ArrayList<>();
        // makes our device visible to other devices
        visible(activity);

        // if we are not already searching for devices, then start the discovery of devices
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        if(discoveredDevicesAdapter.size() != 0) {
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            activity.registerReceiver(discoveryFinishReceiver, filter);
        }

    }

    /**
     * unregisters the broadcast receiver and stops discovery when the fragment is destroyed
     * @param activity
     */
    @SuppressLint("MissingPermission")
    protected void destroy(Activity activity) {
        try {
            activity.unregisterReceiver(discoveryFinishReceiver);
            mBluetoothAdapter.cancelDiscovery();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
