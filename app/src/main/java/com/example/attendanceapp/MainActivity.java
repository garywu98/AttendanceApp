package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private List<String> discoveredDevicesAdapter;

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

    @Override
    protected void onStart() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        discoveredDevicesAdapter = new ArrayList<>();

        if (mBluetoothAdapter == null) {
            //If bluetooth is not supported
            Log.d("Bluetooth ", " is not supported on this device");
            //finish();
        } else {
            int REQUEST_ENABLE_BT = 1;

            Log.d("Bluetooth ", " turning on");

            if (!mBluetoothAdapter.isEnabled()) {
                //if bluetooth is not enabled, enable it

                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    //no permission to connect to bluetooth
                    //return;
                }

                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                Log.d("Bluetooth ", " is now turned on");
            }
        }

        discoverDevices();

        for (String name : discoveredDevicesAdapter) {
            Log.d("Devices ", name);
        }

        super.onStart();
    }

    public void visible() {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivityForResult(getVisible, 0);
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("discoveryFinishReceiver ", "invoked");
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.size() == 0) {
                    discoveredDevicesAdapter.add("None Found");
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    public void discoverDevices() {
        Log.d("discoverDevices ", "invoked");

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        Log.d("discoverDevices ", "here");

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);
    }

    public void devicesPaired() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
        }

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        //Front
//        lv.setAdapter(adapter);
    }

}