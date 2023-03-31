package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

public class StudentSignInActivity extends MainActivity {

    ArrayList<String> testList = new ArrayList<>(Arrays.asList("12345", "54321", "43321"));
    Button testButton;
    EditText IDInputBox;
    String cardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_in);

        testButton = findViewById(R.id.test_button);
        IDInputBox = findViewById(R.id.input_box);

        // set up dynamic list values here

        RecyclerView recyclerView = findViewById(R.id.studentIDRecyclerview);

        //****figure out why the list is not printing correctly on screen
        StudentInfoRecyclerViewAdapter adapter = new StudentInfoRecyclerViewAdapter(testList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardInfo = IDInputBox.getText().toString();
                // close keyboard on phone
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);

                // add id to the arraylist
                if(testList.size() < 5) {
                    testList.add(0, cardInfo);
                    adapter.notifyItemInserted(0);
                }
                else if (testList.size() == 5){
                    testList.remove(testList.size() - 1);
                    adapter.notifyItemRemoved(testList.size());
                    testList.add(0, cardInfo);
                    adapter.notifyItemInserted(0);
                }

                System.out.println(testList.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d("StudentSignInActivity", "onDestroy is called");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        Log.d("StudentSignInActivity", "OnBackPressed is called");
    }
}