package com.example.attendanceapp;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

/*
    Written by Noah Johnson, Jocelyn Chen, Gary Wu, Elin Yang, and Laura Villarreal
     for CS4485.0w1, senior design project, started February 11 2023
    NetIDs: ntj200000, jpc180002, gyw200000, yxy190022, lmv180001
 */

/**
 * displays number of students signed in during the session out of total students in class
 * reads in card ID from card scanner and validates the student is in the class list
 * sends data to the Attend app if ID is found
 * handles the behaviour of the StudentInfoRecyclerView by displaying 5 most recent sign ins
 */
public class StudentSignInActivity extends MainActivity {

    ArrayList<String> mostRecentIDsSignedIn = new ArrayList<>();
    EditText IDInputBox;
    String cardInfo;
    TextView numStudentSignedIn;
    String formattedString;
    int signInStudents = 0;
    String[] idList = {};
    ArrayList<String> signedInList = new ArrayList<>();
    BluetoothThread btThread;

    /**
     * gets the student class id list data collected by the bluetooth thread
     * sets up the number of students signed in
     * listens to card scans
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_in);

        btThread = BluetoothFragment.threadGetter();

        numStudentSignedIn = findViewById(R.id.numStudentsSignedIn);
        formattedString = numStudentSignedIn.getText().toString();

        //get the ID list from fragment
        idList = BluetoothFragment.idListGetter().toArray(new String[0]);

        // setting the number of students signed in to reflect the total number of students in the class
        String newFormattedString;
        idList = BluetoothFragment.idListGetter().toArray(new String[0]);
        newFormattedString = String.format(formattedString, signInStudents, idList.length);
        numStudentSignedIn.setText(newFormattedString);

        // invalidate action bar inherited from main activity
        this.invalidateOptionsMenu();

        IDInputBox = findViewById(R.id.input_box);

        // set up list values received from Attend app
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
    }




    /**
     * hides the devices button and bluetooth button from the action bar when we reach the sign in page
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student_sign_in, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * hiding the search devices and bluetooth activation button
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     *
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem devices_item = menu.findItem(R.id.menu_search_devices);
        devices_item.setVisible(false);

        MenuItem bluetooth_item = menu.findItem(R.id.menu_enable_bluetooth);
        bluetooth_item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * compares ID inputted with IDs from Attend app
     * sends ID to Attend desktop app if ID is validated
     * adds value to most recent list of signed in students
     * @param view
     * @param adapter
     */
    private void validateIDFromTextBox(View view, StudentInfoRecyclerViewAdapter adapter) {
        String newFormattedString;
        cardInfo = IDInputBox.getText().toString();
        // close keyboard on phone
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.ding_36029);

        boolean isFound = false;
        boolean isDuplicate = false;

        newFormattedString = String.format(formattedString, signInStudents, idList.length);
        numStudentSignedIn.setText(newFormattedString);

        String idShort = "";

        //check if scanned ID is in the list of IDs provided from the Attend desktop app
        for(String id : idList) {
            //checks if the scanned ID has already been signed in
            if(signedInList.contains(cardInfo)){
                isDuplicate = true;
                break;
            }

            //checks if an ID provided by the Attend desktop app contains the scanned in ID
            if(cardInfo.contains(id)) {
                signedInList.add(cardInfo);
                idShort = id;
                isFound = true;
                break;
            }
        }

        //if the ID has been found in the list
        if(isFound) {
            signInStudents++;
            newFormattedString = String.format(formattedString, signInStudents, idList.length);
            numStudentSignedIn.setText(newFormattedString);


            showCustomToast("You have been signed in", R.drawable.baseline_check_circle_24);

            // add id to the arraylist
            if (mostRecentIDsSignedIn.size() < 5) {
                mostRecentIDsSignedIn.add(0, idShort);
                adapter.notifyItemInserted(0);
            } else if (mostRecentIDsSignedIn.size() == 5) {
                //remove the oldest ID in the list
                mostRecentIDsSignedIn.remove(mostRecentIDsSignedIn.size() - 1);
                adapter.notifyItemRemoved(mostRecentIDsSignedIn.size());

                //add the new ID
                mostRecentIDsSignedIn.add(0, idShort);
                adapter.notifyItemInserted(0);
            }

            //write card info to thread
            btThread.write(cardInfo.getBytes());

            //play a verification sound to the user
            mp.start();
        }
        else if(isDuplicate) {
            showCustomToast(getString(R.string.already_signed_in), R.drawable.baseline_cancel_24);
        }
        else {
            //if student is not found in the class list
            showCustomToast(getString(R.string.not_found_in_class_list_provided), R.drawable.baseline_cancel_24);
        }
    }



    /**
     * creates a toast, inflates it, and sets the custom parameters for the
     * custom toast message
     * @param message
     * @param image
     */
    public void showCustomToast(String message, @DrawableRes int image) {
        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.viewContainer));
        toast.setView(view);

        ImageView toastImage = (ImageView) view.findViewById((R.id.toastImage));
        toastImage.setImageResource(image);

        TextView toastMessage = view.findViewById(R.id.toastMessage);
        toastMessage.setText(message);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Cancelling the bluetooth connection between our app and the Attend Desktop app
     */
    @Override
    public void onBackPressed()
    {
        btThread.cancel();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}