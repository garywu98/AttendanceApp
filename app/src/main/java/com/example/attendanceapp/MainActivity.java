package com.example.attendanceapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
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
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothUtils bluetoothUtils;
    private Permissions perm;
    TextView textView;
    Button showDevicesBtn;

    // intent value for enabling bluetooth
    int REQUEST_ENABLE_BT = 1;
    public static String[] idList;

    @Override
    /*
        Sets up the content on the screen, calls initBluetooth()
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById((R.id.message));
    }

    /*
    Sets up the menu bar on the application
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Handles action when the user presses one of the buttons on the menu bar
    @SuppressLint("MissingPermission")
    public boolean onOptionsItemSelected(MenuItem item) {
        TextView textView = (TextView) findViewById((R.id.message));

        switch (item.getItemId()) {
            // brings up a listview of all available devices to connect to
            case R.id.menu_search_devices:

                discoverDevices();
                return true;
            case R.id.menu_enable_bluetooth:
                if (!mBluetoothAdapter.isEnabled()) {
                    //if bluetooth is not enabled, enable it
                    textView.setText(R.string.bluetooth_enable_message);
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    Log.d("Bluetooth ", " is now turned on");
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothUtils = new BluetoothUtils(mBluetoothAdapter);
        bluetoothUtils.setMainActivity(this);
        perm = new Permissions(getApplicationInfo().targetSdkVersion);




        if (mBluetoothAdapter == null) {
            //If bluetooth is not supported

            Log.d("Bluetooth ", " is not supported on this device");
            textView.setText(R.string.bluetooth_not_supported);
            finish();
        } else {
            Log.d("Bluetooth ", " turning on");

            if (!mBluetoothAdapter.isEnabled()) {
                //if bluetooth is not enabled, enable it
                textView.setText(R.string.bluetooth_enable_message);
            }
            else {
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(getString(R.string.devices_btn_prompt_1));
                ssb.setSpan(
                        new ImageSpan(this, R.drawable.baseline_devices_24),
                        ssb.length() - 1,
                        ssb.length(),
                        0
                );
                ssb.append(getString(R.string.devices_btn_prompt_2));
                textView.setText(ssb);
            }

        }

        if(perm != null) perm.checkAndRequestPermissions(this);
        else {
            Log.d("onStart ", "Perm is null");
//            finish();
        }

        super.onStart();
    }

    @SuppressLint("MissingPermission")
    public void discoverDevices() {
          bluetoothUtils.discoverDevices(this);
//        bluetoothUtils.discoverDevices(this, handler);
    }

    private void sendID(String id) {
        byte[] idMessage = id.getBytes();
        bluetoothUtils.write(idMessage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // checking if the bluetooth enable was granted or denied by the user
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView textView = (TextView) findViewById((R.id.message));

        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                //bluetooth was turned on
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(getString(R.string.devices_btn_prompt_1));
                ssb.setSpan(
                        new ImageSpan(this, R.drawable.baseline_devices_24),
                        ssb.length() - 1,
                        ssb.length(),
                        0
                );
                ssb.append(getString(R.string.devices_btn_prompt_2));
                textView.setText(ssb);
            }else{
                //bluetooth was not successfully turned on
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothUtils.destroy(this);
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy is called");
        super.onDestroy();
    }
}
