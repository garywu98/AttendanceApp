package com.example.attendanceapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/*
    Written by Noah Johnson, Jocelyn Chen, Gary Wu, Elin Yang, and Laura Villarreal
     for CS4485.0w1, senior design project, started February 11 2023
    NetIDs: ntj200000, jpc180002, gyw200000, yxy190022, lmv180001
 */

/**
 * Displays the list of possible devices to connect to,
 * starts the thread to connect to the Attend application,
 * redirects to the StudentSignInActivity
 */
public class BluetoothFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    // holds the devices that are discoverable by the phone
    private static List<String> devices;

    // acts as the bridge between the recyclerview and the data
    private static BluetoothAdapter mBluetoothAdapter;

    // holds the list of student IDs grabbed from the thread
    private static ArrayList<String> idList = new ArrayList<>();

    // thread that runs the bluetooth processes in the background
    public static BluetoothThread btThread;


    /*
    * takes the Message object from the bluetooth thread once it reads from the stream,
    * populates the id list/cleans the data from the stream
    * Handler expects the message to have ids of 10 characters separated by the newline character
     */
    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d("Handler", "Inside handler");
            Log.d("Handler", "msg.what = " + msg.what);
            if(msg.what == BluetoothThread.MessageConstants.MESSAGE_READ) {
                String readMessage = new String((byte []) msg.obj);

                String[] result = readMessage.split("\\n");
                for (int x=0; x<result.length; x++) {
                    if(result[x].length() == 10 && !idList.contains(result[x])) {
                        idList.add(result[x]);
                    }
                    else if(result[x].equals("*")) {
                        Intent i = new Intent(getActivity(), StudentSignInActivity.class);

                        i.putExtra("idList", idList.toArray());
                        getActivity().startActivity(i);
                    }
                }
            }
            else if(msg.what == BluetoothThread.MessageConstants.CONNECT_ERROR) {
                Log.d("ErrorMessage", "Inside error message handler");
                showCustomToast(getString(R.string.broken_pipe_error_msg), R.drawable.baseline_cancel_24);

            }

        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BluetoothFragment(List<String> devices, BluetoothAdapter mBluetoothAdapter) {
        this.devices = devices;
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BluetoothFragment newInstance(int columnCount) {
        BluetoothFragment fragment = new BluetoothFragment(devices, mBluetoothAdapter);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        // create an action bar that will have a back button
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /*
    Sets up the menu bar for the fragment
     */
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bluetooth_discovery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // moves back to the main activity
            case android.R.id.home:
                FragmentManager fm = requireActivity().getSupportFragmentManager();

                // pops every fragment off the stack to get back to the main page
                while(fm.getBackStackEntryCount() > 0) {
                    fm.popBackStackImmediate();
                    // set the action bar back to its original values from the main activity
                    if(fm.getBackStackEntryCount() == 1) {
                        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                        actionBar.setDisplayHomeAsUpEnabled(false);
                        requireActivity().setTitle(getString(R.string.app_name));
                    }
                    Log.d("fragmentPop", String.valueOf(fm.getBackStackEntryCount()));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem devices_item = menu.findItem(R.id.menu_search_devices);
        devices_item.setVisible(false);

        MenuItem bluetooth_item = menu.findItem(R.id.menu_enable_bluetooth);
        bluetooth_item.setVisible(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth_list, container, false);
        setHasOptionsMenu(true);
        requireActivity().setTitle(getString(R.string.bluetooth_fragment_title));

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            String address = devices.get(position).split("\n")[1];
                            System.out.println(address);
                            BluetoothDevice testDevice = mBluetoothAdapter.getRemoteDevice(address);
                            BluetoothThread thread = new BluetoothThread(testDevice, mBluetoothAdapter, handler);
                            btThread = thread;
                            thread.start();
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );
            recyclerView.setAdapter(new DeviceListRecyclerViewAdapter(devices));

        }
        return view;
    }

    public static BluetoothThread threadGetter() {
        return btThread;
    }

    public static ArrayList<String> idListGetter() {
        return idList;
    }

    // creates a toast, inflates it, and sets the custom parameters for the
    // custom toast message
    public void showCustomToast(String message, @DrawableRes int image) {
        Toast toast = new Toast(requireContext());
        View view = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) requireView().findViewById(R.id.viewContainer));
        toast.setView(view);

        ImageView toastImage = (ImageView) view.findViewById((R.id.toastImage));
        toastImage.setImageResource(image);

        TextView toastMessage = view.findViewById(R.id.toastMessage);
        toastMessage.setText(message);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}