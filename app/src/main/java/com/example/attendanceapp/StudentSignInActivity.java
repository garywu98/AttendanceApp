package com.example.attendanceapp;

import androidx.annotation.DrawableRes;
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

    int totalStudents = idList.length;
    int numSignedIn = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_in);

        btThread = BluetoothFragment.threadGetter();

        numStudentSignedIn = findViewById(R.id.numStudentsSignedIn);
        formattedString = numStudentSignedIn.getText().toString();

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
        String newFormattedString;
        cardInfo = IDInputBox.getText().toString();
        // close keyboard on phone
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.ding_36029);

        boolean isFound = false;
        boolean isDuplicate = false;
        idList = BluetoothFragment.idListGetter().toArray(new String[0]);
        Log.d("StudentSignInActivity", Arrays.toString(idList));
        newFormattedString = String.format(formattedString, signInStudents, idList.length);
        numStudentSignedIn.setText(newFormattedString);

        String idShort = "";

        for(String id : idList) {
            if(signedInList.contains(cardInfo)){
                isDuplicate = true;
                break;
            }

            if(cardInfo.contains(id)) {
                signedInList.add(cardInfo);
                idShort = id;
                isFound = true;
                break;
            }
        }

        if(isFound) {
            signInStudents++;
            newFormattedString = String.format(formattedString, signInStudents, idList.length);
            numStudentSignedIn.setText(newFormattedString);
            showCustomToast("You have been signed in", R.drawable.baseline_check_circle_24);
//            Toast.makeText(this, "You have been signed in", Toast.LENGTH_SHORT).show();
            // add id to the arraylist
            if (mostRecentIDsSignedIn.size() < 5) {
                mostRecentIDsSignedIn.add(0, idShort);
                adapter.notifyItemInserted(0);
            } else if (mostRecentIDsSignedIn.size() == 5) {
                mostRecentIDsSignedIn.remove(mostRecentIDsSignedIn.size() - 1);
                adapter.notifyItemRemoved(mostRecentIDsSignedIn.size());
                mostRecentIDsSignedIn.add(0, idShort);
                adapter.notifyItemInserted(0);
            }

            //write card info to thread
            btThread.write(cardInfo.getBytes());

            mp.start();
        }
        else if(isDuplicate) {
            showCustomToast("You have already been signed in", R.drawable.baseline_cancel_24);
//            Toast.makeText(this, "You have already been signed in", Toast.LENGTH_SHORT).show();
        }
        else {
            showCustomToast("Not found in the list, please speak with the professor", R.drawable.baseline_cancel_24);
//            Toast.makeText(this, "Not found in the list, please speak with the professor", Toast.LENGTH_SHORT).show();
        }

        System.out.println(mostRecentIDsSignedIn.toString());

    }

    // creates a toast, inflates it, and sets the custom parameters for the
    // custom toast message
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
        Log.d("StudentSignInActivity", "onDestroy is called");
        super.onDestroy();
    }

}