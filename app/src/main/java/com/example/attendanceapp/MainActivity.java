package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            TextView message = (TextView) findViewById(R.id.message);
            // Test this later
            message.setText(R.string.bluetooth_not_supported);
        }
        else if(!bluetoothAdapter.isEnabled()) {
            TextView message = (TextView) findViewById(R.id.message);
            message.setText(R.string.bluetooth_enable_message);
        }
    }
    private void isBluetoothEnabled() {
        if(bluetoothAdapter.isEnabled()) {
//            TextView message = (TextView) findViewById(R.id.message);
//            message.setText(R.string.bluetooth_enable_message);
        }
    }
}