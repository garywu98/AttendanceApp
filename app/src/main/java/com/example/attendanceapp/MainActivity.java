package com.example.attendanceapp;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
    Handles the Bluetooth operations on the main page, including connecting to bluetooth, connecting to another device,
    and error handling for when bluetooth is not detected.

    Written by Laura Villarreal and Elin Yang for CS4485.0w1, Android Attendance App, starting February 28, 2023.
    NetID: lmv180001, yxy190022
 */

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    Button showDevicesBtn;

    @Override
    /*
        Sets up the content on the screen, calls initBluetooth()
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();
    }

    /*
    Sets up the menu bar on the application
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handles action when the user presses one of the buttons on the menu bar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // custom toast will show up when clicking on the devices button
            case R.id.menu_search_devices:
                showCustomToast("Test message!", R.drawable.baseline_check_circle_24);
                return true;
            case R.id.menu_enable_bluetooth:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // creates a toast, inflates it, and sets the custom parameters for the
    // custom toast message
    private void showCustomToast(String message, @DrawableRes int image) {
        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.viewContainer));
        toast.setView(view);

        ImageView toastImage = (ImageView) view.findViewById((R.id.toastImage));
        toastImage.setImageResource(image);

        TextView toastMessage = view.findViewById(R.id.toastMessage);
        toastMessage.setText(message);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /*
    Determines which message to display to the user based on whether bluetooth is enabled on
    their device
     */
    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            TextView message = (TextView) findViewById(R.id.message);
            message.setText(R.string.bluetooth_not_supported);
        }
        else if(!bluetoothAdapter.isEnabled()) {
            TextView message = (TextView) findViewById(R.id.message);
            message.setText(R.string.bluetooth_enable_message);
        }
    }
}