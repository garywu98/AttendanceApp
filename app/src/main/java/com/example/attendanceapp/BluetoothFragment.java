package com.example.attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.attendanceapp.placeholder.PlaceholderContent;

import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

/**
 * A fragment representing a list of Items.
 */
public class BluetoothFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private static List<String> devices;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BluetoothFragment(List<String> devices) {
        this.devices = devices;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BluetoothFragment newInstance(int columnCount) {
        BluetoothFragment fragment = new BluetoothFragment(devices);
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
            recyclerView.setAdapter(new DeviceListRecyclerViewAdapter(devices));
        }
        return view;
    }
}