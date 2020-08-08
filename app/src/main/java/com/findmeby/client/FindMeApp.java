package com.findmeby.client;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.util.Log;

import com.findmeby.client.receiver.ConnectionChangeReceiver;
import com.findmeby.client.receiver.LocationReceiver;

import org.jetbrains.annotations.NotNull;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.findmeby.client.receiver.LocationReceiver.ACTION_LOCATION_CHANGED;


/**
 * User: Sergey Zhmura
 * Date: 19.02.14
 * Time: 20:43
 */
public class FindMeApp extends Application {

    private static final String TAG = FindMeApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceivers();
    }

    private void registerReceivers() {
        //registerCheckConnectionAfterBootReceiver();
        //registerCheckerConnectionReceiver();
        registerLocationReceiver();
        registerAlarmReceivers();
        registerConnectionChangerReceiver();
    }

    private void registerAlarmReceivers() {
       // IntentFilter intentFilter = new IntentFilter(GetNewMessagesAlarmManager.ACTION);
       // LocalBroadcastManager.getInstance(this).registerReceiver(new GetNewMessagesAlarmManager(), intentFilter);
    }

    private void registerConnectionChangerReceiver() {
        //IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        //registerReceiver(new ConnectionChangeReceiver(), intentFilter);
   }


    private void registerLocationReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_LOCATION_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(new LocationReceiver(), intentFilter);
    }

    private void turnOnStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
    }

    private void permitDiskReads() {
        StrictMode.ThreadPolicy oldThreadPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(oldThreadPolicy).permitDiskWrites().permitDiskReads().build());
        StrictMode.setThreadPolicy(oldThreadPolicy);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @NonNull
    public static FindMeApp get(@NotNull Context context) {
        return ((FindMeApp) context.getApplicationContext());
    }
}

