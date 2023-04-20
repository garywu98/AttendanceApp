package com.example.attendanceapp;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.attendanceapp.databinding.FragmentBluetoothBinding;
import java.util.List;

/*
    Written by Noah Johnson, Jocelyn Chen, Gary Wu, Elin Yang, and Laura Villarreal
     for CS4485.0w1, senior design project, started February 11 2023
    NetIDs: ntj200000, jpc180002, gyw200000, yxy190022, lmv180001
 */
/**
 * {@link RecyclerView.Adapter} that can display discoverable device information.
 * Recyclerview for the Bluetooth Fragment
 */
public class DeviceListRecyclerViewAdapter extends RecyclerView.Adapter<DeviceListRecyclerViewAdapter.ViewHolder> {

    private final List<String> mDevices;

    /**
     * overloaded constructor
     * @param items - devices to display
     */
    public DeviceListRecyclerViewAdapter(List<String> items) {
        mDevices = items;
    }

    /**
     * inflates the viewholder to the screen
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentBluetoothBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    /**
     * sets the contents of the recyclerview to the devices
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mDevices.get(position);
        holder.mIdView.setText(mDevices.get(position));
    }

    /**
     * Gets the number of devices that are currently being displayed on the recyclerview
     * @return
     */
    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    /**
     * class that grabs the information from an individual device item displayed in the recyclerview
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final CardView mCardView;
        public String mItem;

        public ViewHolder(FragmentBluetoothBinding binding) {
            super(binding.getRoot());
            // get content displayed in an item of the recyclerview
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            mCardView = binding.bluetoothCard;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}