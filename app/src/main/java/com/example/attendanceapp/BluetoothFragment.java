package com.example.attendanceapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class BluetoothFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private static List<String> devices;
    private static BluetoothAdapter mBluetoothAdapter;

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
                            BluetoothThread thread = new BluetoothThread(testDevice, mBluetoothAdapter, new Handler());
                            thread.run();
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
}