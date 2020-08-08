package com.findmeby.client.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {
    private static String userIdPath = "com.example.myapplication.userId";
    private static String contactsPath = "com.example.myapplication.contactsList";
    private static String userNamePath = "com.example.myapplication.userName";
    private static String geoProvidedPath = "com.example.myapplication.geoProvided";
    private static String navigateToPath = "com.example.myapplication.navigateTo";
    private static String alarmBodyPath = "com.example.myapplication.alarmBody";
    private static String cancelAlarmBodyPath = "com.example.myapplication.cancelAlarmBody";
    private static String dangerModePath = "com.example.myapplication.dangerMode";
    private static String backGroundRunPath = "com.example.myapplication.backGroundRun";

    public static String generateUserId(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = sharedPref.getString(userIdPath,null);
        if(userId == null || userId.isEmpty()){
            userId = java.util.UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(userIdPath, userId);
            editor.commit();
        }
        return userId;
    }

    public static void setUserName(Context context, String userName) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(userNamePath, userName);
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPref.getString(userNamePath,null);
        return userName;
    }

    public static void setContacts(Context context, Set<String> contactsSet) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(contactsPath, contactsSet);
        editor.commit();
    }

    public static Set<String> getContacts(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        HashSet<String> setOfContacts = (HashSet<String>) sharedPref.getStringSet(contactsPath,null);

        /*String[] arrayOfContacts = null;
        if(setOfContacts != null) {
            int contactsSize = setOfContacts.size();

            arrayOfContacts = new String[contactsSize];
            if (contactsSize != 0) {

                setOfContacts.toArray(arrayOfContacts);
            }
        }*/
        return setOfContacts;
    }

    public static void setGeoProvided(Context context, boolean isGeoProvided) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(geoProvidedPath, isGeoProvided);
        editor.commit();
    }

    public static boolean getGeoProvided(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isGeoProvided = sharedPref.getBoolean(geoProvidedPath,false);
        return isGeoProvided;
    }

    public static int getNavigateTo(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int navigateTo = sharedPref.getInt(navigateToPath,-1);
        return navigateTo;
    }

    public static void setNavigateTo(Context context, int navigateTo) {
        Log.d("Shared",navigateTo + "");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(navigateToPath, navigateTo);
        editor.commit();
    }

    public static String getAlarmBody(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String alarmBody = sharedPref.getString(alarmBodyPath,null);
        return alarmBody;
    }

    public static void setAlarmBody(Context context, String alarmBody) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(alarmBodyPath, alarmBody);
        editor.commit();
    }

    public static String getCancelAlarmBody(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String cancelAlarmBody = sharedPref.getString(cancelAlarmBodyPath,null);
        return cancelAlarmBody;
    }

    public static void setCancelAlarmBody(Context context, String cancelAlarmBody) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(cancelAlarmBodyPath, cancelAlarmBody);
        editor.commit();
    }

    public static void setDangerMode(Context context, boolean dangerMode) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(dangerModePath, dangerMode);
        editor.commit();
    }

    public static boolean getDangerMode(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean dangerMode = sharedPref.getBoolean(dangerModePath,false);
        return dangerMode;
    }

    public static void setBackGroundRun(Context context, boolean backGroundRun) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(backGroundRunPath, backGroundRun);
        editor.commit();
    }

    public static boolean getBackGroundRun(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean backGroundRun = sharedPref.getBoolean(backGroundRunPath,false);
        return backGroundRun;
    }
}
