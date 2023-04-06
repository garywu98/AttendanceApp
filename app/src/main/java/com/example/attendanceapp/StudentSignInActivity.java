package com.example.attendanceapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class StudentSignInActivity extends MainActivity {

    ArrayList<String> mostRecentIDsSignedIn = new ArrayList<>(Arrays.asList("12345", "54321", "43321"));
    EditText IDInputBox;
    String cardInfo;
    String[] idList = {"1234567890", "9876543210"};
    ArrayList<String> signedInList = new ArrayList<>();
    BluetoothThread btThread;
    int totalStudents = idList.length;
    int numSignedIn = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_in);

//        idList = getIntent().getExtras().getStringArray("idList");
//        btThread = (BluetoothThread) getIntent().getExtras().getSerializable("thread");

        // invalidate action bar inherited from main activity
        this.invalidateOptionsMenu();

//        testButton = findViewById(R.id.test_button);
        IDInputBox = findViewById(R.id.input_box);

        // set up list values received from Attend app here

        RecyclerView recyclerView = findViewById(R.id.studentIDRecyclerview);

        StudentInfoRecyclerViewAdapter adapter = new StudentInfoRecyclerViewAdapter(mostRecentIDsSignedIn);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // on keyboard action, check if a newline (enter) key is pressed
       IDInputBox.setOnKeyListener(new View.OnKeyListener() {
           public boolean onKey(View v, int keyCode, KeyEvent event) {

               if (event.getAction() == KeyEvent.ACTION_DOWN) {
                   switch (keyCode) {
                       case KeyEvent.KEYCODE_DPAD_CENTER:
                           case KeyEvent.KEYCODE_ENTER:
                               validateIDFromTextBox(v, adapter);
                               IDInputBox.getText().clear();
                               return true;
                               default:
                                   break;
                   }
               }
               return false;
           }
       });

//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                cardInfo = IDInputBox.getText().toString();
//                // close keyboard on phone
//                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
//
//                // add id to the arraylist
//                if(testList.size() < 5) {
//                    testList.add(0, cardInfo);
//                    adapter.notifyItemInserted(0);
//                }
//                else if (testList.size() == 5){
//                    testList.remove(testList.size() - 1);
//                    adapter.notifyItemRemoved(testList.size());
//                    testList.add(0, cardInfo);
//                    adapter.notifyItemInserted(0);
//                }
//
//                System.out.println(testList.toString());
//            }
//        });
    }


    // hides the devices button and bluetooth button from the action bar
    // when we reach the sign in page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student_sign_in, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem devices_item = menu.findItem(R.id.menu_search_devices);
        devices_item.setVisible(false);

        MenuItem bluetooth_item = menu.findItem(R.id.menu_enable_bluetooth);
        bluetooth_item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    // compares ID inputted with IDs from Attend app, sends confirmation, and adds
    // value to most recent list of signed in students
    private void validateIDFromTextBox(View view, StudentInfoRecyclerViewAdapter adapter) {
        cardInfo = IDInputBox.getText().toString();
        // close keyboard on phone
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.ding_36029);
        int firstDelimiter = cardInfo.indexOf('=');
        cardInfo = cardInfo.substring(firstDelimiter + 1, firstDelimiter + 11);

        boolean isFound = false;
        boolean isDuplicate = false;

        for(String id : idList) {
            if(signedInList.contains(cardInfo)){
                isDuplicate = true;
                break;
            }

            if(id.equals(cardInfo)) {
                signedInList.add(cardInfo);
                isFound = true;
                break;
            }
        }

        if(isFound) {
            Toast.makeText(this, "You have been signed in", Toast.LENGTH_SHORT).show();
            // add id to the arraylist
            if (mostRecentIDsSignedIn.size() < 5) {
                mostRecentIDsSignedIn.add(0, cardInfo);
                adapter.notifyItemInserted(0);
            } else if (mostRecentIDsSignedIn.size() == 5) {
                mostRecentIDsSignedIn.remove(mostRecentIDsSignedIn.size() - 1);
                adapter.notifyItemRemoved(mostRecentIDsSignedIn.size());
                mostRecentIDsSignedIn.add(0, cardInfo);
                adapter.notifyItemInserted(0);
            }

            //write card info to thread
//            btThread.write(cardInfo.getBytes());

            mp.start();
        }
        else if(isDuplicate) {
            Toast.makeText(this, "You have already been signed in", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Not found in the list, please speak with the professor", Toast.LENGTH_SHORT).show();
        }

        System.out.println(mostRecentIDsSignedIn.toString());
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