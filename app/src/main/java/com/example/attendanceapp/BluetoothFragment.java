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


    /**
    * takes the Message object from the bluetooth thread once it reads from the stream,
    * populates the id list/cleans the data from the stream if the flag is MESSAGE_READ
    * Handler expects the message to have ids of 10 characters separated by the newline character
    * Begins the sign in activity once an asterisk is read
    * If the flag is CONNECT_ERROR, it displays a custom toast notifying the user of a broken pipe error
     */
    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == BluetoothThread.MessageConstants.MESSAGE_READ) {
                String readMessage = new String((byte []) msg.obj);

                String[] result = readMessage.split("\\n");
                for (int x=0; x<result.length; x++) {
                    if(result[x].length() == 10 && !idList.contains(result[x])) {
                        idList.add(result[x]);
                    }
                    else if(result[x].equals("*")) {
                        Intent i = new Intent(getActivity(), StudentSignInActivity.class);
                        getActivity().startActivity(i);
                    }
                }
            }
            else if(msg.what == BluetoothThread.MessageConstants.CONNECT_ERROR) {
                showCustomToast(getString(R.string.broken_pipe_error_msg), R.drawable.baseline_cancel_24);
            }
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BluetoothFragment() {

    }

    /**
     * Overlaoded constructor
     * @param devices - list of devices found by the adapter
     * @param mBluetoothAdapter
     */
    public BluetoothFragment(List<String> devices, BluetoothAdapter mBluetoothAdapter) {
        BluetoothFragment.devices = devices;
        BluetoothFragment.mBluetoothAdapter = mBluetoothAdapter;
    }

    /**
     * creates a new instance of the bluetooth fragment
     * @param columnCount - number of columns that the recyclerview will have in its display
     * @return newly created fragment instance
     */
    @SuppressWarnings("unused")
    public static BluetoothFragment newInstance(int columnCount) {
        BluetoothFragment fragment = new BluetoothFragment(devices, mBluetoothAdapter);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * setting up the fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting the number of columns for the recyclerview
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        // create an action bar that will have a back button
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Sets up the menu bar for the fragment
     */
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bluetooth_discovery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * setting up behavior for the back button on the actionbar
     * @param item The menu item that was selected.
     *
     * @return
     */
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
                        // remove the back button from the actionbar
                        actionBar.setDisplayHomeAsUpEnabled(false);
                        requireActivity().setTitle(getString(R.string.app_name));
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * removing the icons from the actionbar on the main activity
     * so they cannot be pressed when the fragment is open
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     *
     */
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem devices_item = menu.findItem(R.id.menu_search_devices);
        devices_item.setVisible(false);

        MenuItem bluetooth_item = menu.findItem(R.id.menu_enable_bluetooth);
        bluetooth_item.setVisible(false);
    }


    /**
     * sets up the recyclerview and its behaviors when it is created
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
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
            // sets the layout based on the number of columns provided to the recyclerview
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // have each item in the recyclerview react to a press
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            String address = devices.get(position).split("\n")[1];
                            System.out.println(address);
                            // grabbing the device information that was pressed
                            BluetoothDevice testDevice = mBluetoothAdapter.getRemoteDevice(address);

                            // starting the thread to start connection with the computer and the Attend application
                            BluetoothThread thread = new BluetoothThread(testDevice, mBluetoothAdapter, handler);
                            btThread = thread;
                            thread.start();
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // nothing will happen if the user long clicks
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

    /**
     * creates a toast, inflates it, and sets the custom parameters for the
     * custom toast message
     * @param message - message that will be displayed in the toast
     * @param image - image that will be displayed in the toast
     */
    public void showCustomToast(String message, @DrawableRes int image) {
        Toast toast = new Toast(requireContext());
        // grab the current page that the function is being called from
        View view = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) requireView().findViewById(R.id.viewContainer));
        toast.setView(view);

        // set message and image to the toast

        ImageView toastImage = (ImageView) view.findViewById((R.id.toastImage));
        toastImage.setImageResource(image);

        TextView toastMessage = view.findViewById(R.id.toastMessage);
        toastMessage.setText(message);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}