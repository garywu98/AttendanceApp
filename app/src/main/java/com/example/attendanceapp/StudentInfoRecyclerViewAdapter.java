package com.example.attendanceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/*
    Written by Noah Johnson, Jocelyn Chen, Gary Wu, Elin Yang, and Laura Villarreal
     for CS4485.0w1, senior design project, started February 11 2023
    NetIDs: ntj200000, jpc180002, gyw200000, yxy190022, lmv180001
 */

/**
 * recyclerview list of the students who have been signed on our side
 */
public class StudentInfoRecyclerViewAdapter extends RecyclerView.Adapter<StudentInfoRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> studentInfo;

    /**
     * gets the TextView for the UTD signed in IDs
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView idContainerTextView;

        public ViewHolder(View view) {
            super(view);

            // Define click listener for the ViewHolder's View
            idContainerTextView = (TextView) view.findViewById(R.id.UTD_ID);
        }

        public TextView getTextView() {
            return idContainerTextView;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public StudentInfoRecyclerViewAdapter(ArrayList<String> dataSet) {
        studentInfo = dataSet;
    }

    /**
     * inflates the viewholder to the screen
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_info, parent, false);

        return new ViewHolder(view);
    }

    /**
     * binding data to the UI element
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(studentInfo.get(position));
    }

    /**
     * gets count of students being shown in the recycler view
     * @return
     */
    @Override
    public int getItemCount() {
        return studentInfo.size();
    }


}
