package com.example.attendanceapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothUtils {
        private BluetoothAdapter mBluetoothAdapter;
    private List<String> discoveredDevicesAdapter;
    // holds the discoverable devices
    private ArrayList<String> discoverableDeviceList = new ArrayList<>();
    MainActivity main = null;

    private BluetoothDevice testDevice;


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

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.d("discoveryFinishReceiver ", "here 1");
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("discoveryFinishReceiver ", "here 2");
                if (discoveredDevicesAdapter.size() == 0) {
                    discoveredDevicesAdapter.add("None Found");
                }
            }


            //display results
            for(String device : discoveredDevicesAdapter) {
                System.out.println("UUID");
                System.out.println(UUID.randomUUID());
            }

            FragmentManager fm = main.getSupportFragmentManager();
            BluetoothFragment fragment = new BluetoothFragment(discoveredDevicesAdapter, mBluetoothAdapter);
            fm.beginTransaction().add(R.id.main_activity_container,fragment).commit();

        }
    };

    @SuppressLint("MissingPermission")
    public void discoverDevices(MainActivity activity) {
        discoveredDevicesAdapter = new ArrayList<>();

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

//        createBluetoothThread();
    }

    public void createBluetoothThread() {
        testDevice = mBluetoothAdapter.getRemoteDevice("BE:AC:10:00:00:01");
        BluetoothThread thread = new BluetoothThread(testDevice, mBluetoothAdapter, new Handler());
        thread.run();
    }

//    public void devicesPaired() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//        ArrayList list = new ArrayList();
//
//        for (BluetoothDevice bt : pairedDevices) {
//            list.add(bt.getName());
//        }
//
//        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
//
//        //Front
////        lv.setAdapter(adapter);
//    }

    @SuppressLint("MissingPermission")
    protected void destroy(MainActivity activity) {
        activity.unregisterReceiver(discoveryFinishReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }
}
