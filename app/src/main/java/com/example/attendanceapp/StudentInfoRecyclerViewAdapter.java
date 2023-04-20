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
public class StudentInfoRecyclerViewAdapter extends RecyclerView.Adapter<StudentInfoRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> studentInfo = new ArrayList<String>();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.UTD_ID);
        }

        public TextView getTextView() {
            return textView;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_info, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(studentInfo.get(position));
    }

    @Override
    public int getItemCount() {
        return studentInfo.size();
    }


}
