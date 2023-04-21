package com.example.attendanceapp;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
/*
    Written by Noah Johnson, Jocelyn Chen, Gary Wu, Elin Yang, and Laura Villarreal
     for CS4485.0w1, senior design project, started February 11 2023
    NetIDs: ntj200000, jpc180002, gyw200000, yxy190022, lmv180001
 */

/**
 * creates connection with the Attend app
 * sending and reading data from our app to the Attend app
 * BluetoothThread called from the BluetoothFragment Activity
 */
public class BluetoothThread extends Thread {
    private BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothAdapter bluetoothAdapter;
    private final String TAG = "BluetoothThread";

    // the UUID provided is the UUID for the Attend desktop app
    private final UUID ATTEND_UUID = UUID.fromString("e0cbf06c-cd8b-4647-bb8a-263b43f0f974");
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] streamBuffer;

    // empty byte array sent to the handler in case of an error
    private byte[] errorBuffer = {};
    private Handler handler;

    // constants provided to the handler in order to indicate what type of message is being sent
    public interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int CONNECT_ERROR = 1;

    }

    /**
     * constructor attempting to create a socket for connection with the Attend desktop app
     * @param device - bluetooth device selected from the fragment's recyclerview
     * @param adapter - adapter for the recyclerview
     * @param handler - handler defined in BluetoothFragment
     */
    @SuppressWarnings("MissingPermission")
    public BluetoothThread(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        bluetoothAdapter = adapter;
        bluetoothDevice = device;

        BluetoothSocket tempSocket = null;
        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;
        this.handler = handler;

        // tries to create a socket
        try {
            tempSocket = device.createInsecureRfcommSocketToServiceRecord(ATTEND_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        bluetoothSocket = tempSocket;

        // creating the streams for reading and writing data to the socket
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

    /**
     * tries to connect to the Attend desktop app socket
     * sends the string "*ID*" to the Attend desktop app to request the list of IDs
     * listens for the list of IDs to be returned and passes it to the handler
     */
    @Override
    @SuppressWarnings("MissingPermission")
    public void run() {
        bluetoothAdapter.cancelDiscovery();
        int numBytes = 0;

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            bluetoothSocket.connect();
            Log.d("Run", " Thread is connected");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e("ConnectionError", connectException.toString());
        }

        // message sent to Attend desktop app to request list of IDs
        String initialMessage = "*ID*";
        write(initialMessage.getBytes());

        // listening to Attend Desktop's socket until a list of IDs is returned
        while(true) {
            try {
                streamBuffer = new byte[1024];
                numBytes = inputStream.read(streamBuffer);
                int counter = 0;

                // get the index of the last non-null character in the byte stream
                for(counter = 0; streamBuffer[counter] != 0; counter++);

                // only alert the handler if there is at least one byte of data in the stream
                if(counter != 0) {
                    // cloned the buffer in order to prevent it being overwritten while in the handler
                    byte[] bufferClone = Arrays.copyOfRange(streamBuffer, 0, counter);

                    // send data to handler
                    Message readMsg = handler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1,
                            bufferClone);
                    readMsg.setTarget(handler);
                    readMsg.sendToTarget();
                }

                // if the read failed, then there was an issue with connecting to the Attend Desktop's socket
                // generally this means that Attend Desktop's socket is closed because of the Desktop app not being open
                // or the application was open for too long/already connected to once
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                Message readMsg = handler.obtainMessage(MessageConstants.CONNECT_ERROR, numBytes, -1, errorBuffer);
                readMsg.setTarget(handler);
                readMsg.sendToTarget();
                break;
            }

        }
    }

    /**
     * writing bytes to the desktop application (the *ID* string, and the IDs that have been scanned in on the phone app)
     * @param bytes
     */
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }
    }


    /**
     * cancels the thread, which cancels the socket connection to the Attend Desktop app
     */
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

}
