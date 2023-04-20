package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


/*
    Written by Noah Johnson, Jocelyn Chen, Gary Wu, Elin Yang, and Laura Villarreal
     for CS4485.0w1, senior design project, started February 11 2023
    NetIDs: ntj200000, jpc180002, gyw200000, yxy190022, lmv180001
 */

/**
    * Handles the Bluetooth operations on the main page, including connecting to bluetooth, connecting to another device,
    * and error handling for when bluetooth is not detected.
 */
public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothUtils bluetoothUtils;

    //bluetooth permissions
    private Permissions perm;
    TextView instructionText;

    // intent value for enabling bluetooth
    int REQUEST_ENABLE_BT = 1;

    /**
     * Sets up the content on the screen, calls initBluetooth()
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instructionText = (TextView) findViewById((R.id.message));
    }

    /**
     * Sets up the menu bar on the application
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles action when the user presses one of the buttons on the menu bar
     * @param item The menu item that was selected.
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public boolean onOptionsItemSelected(MenuItem item) {
        TextView textView = (TextView) findViewById((R.id.message));

        switch (item.getItemId()) {
            // brings up the fragment with all available devices to connect to
            case R.id.menu_search_devices:
                bluetoothUtils.discoverDevices(this);
                return true;
            //turns on bluetooth for the device
            case R.id.menu_enable_bluetooth:
                //if bluetooth is not enabled, enable it
                if (!mBluetoothAdapter.isEnabled()) {
                    textView.setText(R.string.bluetooth_enable_message);
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * requests permissions for bluetooth
     * display instructions to user for running the app and connecting to Attend desktop app
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothUtils = new BluetoothUtils(mBluetoothAdapter);
        bluetoothUtils.setMainActivity(this);

        //get the permissions for the respected device's version
        perm = new Permissions(getApplicationInfo().targetSdkVersion);

        //If bluetooth is not supported on the device
        if (mBluetoothAdapter == null) {
            instructionText.setText(R.string.bluetooth_not_supported);
            finish();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                //if bluetooth is not enabled, request user to enable it
                instructionText.setText(R.string.bluetooth_enable_message);
            }
            else {
                // sets the instruction text with an icon to show which button to press
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(getString(R.string.devices_btn_prompt_1));
                ssb.setSpan(
                        new ImageSpan(this, R.drawable.baseline_devices_24),
                        ssb.length() - 1,
                        ssb.length(),
                        0
                );

                //prompts the user to press the devices button to connect phone to device
                ssb.append(getString(R.string.devices_btn_prompt_2));
                instructionText.setText(ssb);
            }

        }

        //request user to allow the specific permissions to use bluetooth
        if(perm != null) perm.checkAndRequestPermissions(this);

        super.onStart();
    }

    /**
     * requests permissions
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * checking if the bluetooth enable was granted or denied by the user
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView textView = (TextView) findViewById((R.id.message));

        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                //notify user that bluetooth was turned on
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
    }
}
