package com.example.attendanceapp;


import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.renderscript.ScriptGroup;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.logging.LogRecord;

public class BluetoothThread extends Thread {
    private BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothAdapter bluetoothAdapter;
    private final String TAG = "BluetoothThread";
    private final UUID ATTEND_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private byte[] streamBuffer;
    private Handler handler;

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

    }

    @SuppressWarnings("MissingPermission")
    public BluetoothThread(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        bluetoothAdapter = adapter;
        bluetoothDevice = device;

        BluetoothSocket tempSocket = null;
        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;
        this.handler = handler;

        try {
            tempSocket = bluetoothDevice.createRfcommSocketToServiceRecord(ATTEND_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        bluetoothSocket = tempSocket;

        try {
            tempInputStream = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tempOutputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }
        inputStream = tempInputStream;
        outputStream = tempOutputStream;


    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void run() {
        Log.d("Run", " Thread is created");

        bluetoothAdapter.cancelDiscovery();
        streamBuffer = new byte[1024];
        int numBytes;

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            bluetoothSocket.connect();
            Log.d("Run", " Thread is connected");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.

            try {
                Log.e("","trying fallback...");

                bluetoothSocket = (BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
                bluetoothSocket.connect();

                Log.e("","Connected");
            }
            catch (Exception e2) {
                Log.e("", "Couldn't establish Bluetooth connection!");
            }

//            try {
//                Log.e(TAG, "could not connect ", connectException);
//                bluetoothSocket.close();
//            } catch (IOException closeException) {
//                Log.e(TAG, "Could not close the client socket", closeException);
//            }
            return;
        }

        while(true) {
            try {
                Log.d("Listening ", "BluetoothThread");
                numBytes = inputStream.read(streamBuffer);
                Message readMsg = handler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1,
                                                        streamBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }

        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);

            Message writtenMsg = handler.obtainMessage(MessageConstants.MESSAGE_WRITE,
                    -1, -1, streamBuffer);
            writtenMsg.sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

            Message writeErrorMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast",
                    "Couldn't send data to the other device");
            writeErrorMsg.setData(bundle);
            handler.sendMessage(writeErrorMsg);
        }
    }


    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

}
