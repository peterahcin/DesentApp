package com.example.desent.desent.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.desent.desent.activities.LoginActivity;
import com.example.desent.desent.activities.WelcomeActivity;
import com.example.desent.desent.models.DatabaseHelper;

import java.util.HashMap;

/**
 * Created by ragnhlar on 23.04.2018.
 */

public class SessionManagement {

    //Shared Preferences
    SharedPreferences sharedPreferences;
    //Shared Preferences Editor
    SharedPreferences.Editor editor;
    //Shared Preference Mode
    int PRIVATE_MODE = 0;
    //Shared Preference File Name
    private  static final String PREF_NAME = "AndroidPref";
    //All Shared Preference Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    //Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "pref_key_personal_email";

    DatabaseHelper myDB;

    Context context;

    public SessionManagement(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        myDB = new DatabaseHelper(context);
    }

    //Create login session
    public void createLoginSession(String email){
        //Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        //storing email in Shared Preferences
        editor.putString(KEY_EMAIL, email);

        //commit changes
        editor.commit();
    }

    //Get stored session data
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        //user email
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));

        //return user
        return user;
    }

    //checkLogin() will check user login status
    //if false it will redirect user to login page
    //else won't do anything
    public void checkLogin() {
        //check login status
        String email = myDB.getUserEmail();

        if (email == null){
            Intent i = new Intent(context, WelcomeActivity.class);
            //closing all the activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //add new flag to start new activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //starting login activity
            context.startActivity(i);
        }
        if (!this.isLoggedIn() && email != null){
            //user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, LoginActivity.class);
            //closing all the activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //add new flag to start new activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //starting login activity
            context.startActivity(i);
        }
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    //clear session details
    public void logoutUser() {
        //clearing all data from shared preferences
        editor.clear();
        editor.commit();

        //after logout redirect user to login activity
        Intent i = new Intent(context, LoginActivity.class);
        //closing all the activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //add new flag to start new activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //starting login activity
        context.startActivity(i);
    }

}
