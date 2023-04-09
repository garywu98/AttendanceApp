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
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.renderscript.ScriptGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.LogRecord;

public class BluetoothThread extends Thread {
    private BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothAdapter bluetoothAdapter;
    private final String TAG = "BluetoothThread";
    private final UUID ATTEND_UUID = UUID.fromString("e0cbf06c-cd8b-4647-bb8a-263b43f0f974");
    private InputStream inputStream;
    private OutputStream outputStream;
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
            tempSocket = device.createInsecureRfcommSocketToServiceRecord(ATTEND_UUID);
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


        boolean test = bluetoothAdapter.cancelDiscovery();
        System.out.println(test);
        int numBytes = 0;
        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            bluetoothSocket.connect();
            Log.d("Run", " Thread is connected");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e("ConnectionError", connectException.toString());

//            try {
//                Log.e(TAG, "could not connect ", connectException);
//                bluetoothSocket.close();
//            } catch (IOException closeException) {
//                Log.e(TAG, "Could not close the client socket", closeException);
//            }
        }

        String initialMessage = "*ID*";
        write(initialMessage.getBytes());

        while(true) {
            try {
                    streamBuffer = new byte[1024];
                    Log.d("Listening ", "BluetoothThread");
                    numBytes = inputStream.read(streamBuffer);
//                    String bufferString = new String(streamBuffer);
//                    Log.d("streamBuffer", Arrays.toString(streamBuffer));
//                    Message readMsg = handler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1,
//                            bufferString);
                int counter = 0;

                for(counter = 0; streamBuffer[counter] != 0; counter++);
                if(counter != 0) {
//                    String bufferString = new String(streamBuffer).substring(0, counter);
                    byte[] bufferClone = Arrays.copyOfRange(streamBuffer, 0, counter);
                    Log.d("streamBuffer", Arrays.toString(streamBuffer));
                    Message readMsg = handler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1,
                            bufferClone);
                    readMsg.setTarget(handler);
                    readMsg.sendToTarget();
                }

//                    readMsg.setTarget(handler);
//                    readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }

        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
//            Message writtenMsg = handler.obtainMessage(MessageConstants.MESSAGE_WRITE,
//                    -1, -1, streamBuffer);
//            writtenMsg.sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

//            Message writeErrorMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//            Bundle bundle = new Bundle();
//            bundle.putString("toast",
//                    "Couldn't send data to the other device");
//            writeErrorMsg.setData(bundle);
//            handler.sendMessage(writeErrorMsg);
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
