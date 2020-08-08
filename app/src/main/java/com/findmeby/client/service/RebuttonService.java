package com.findmeby.client.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.findmeby.client.receiver.ConnectionChangeReceiver;
import com.findmeby.client.receiver.LocationPermissionsReceiver;
import com.findmeby.client.receiver.RedButtonAlarmReceiver;
import com.findmeby.client.util.FindMeHttpUtil;
import com.findmeby.client.util.SharedPreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class RebuttonService extends Service implements LocationListener {
    private RedButtonAlarmReceiver mScreenReceiver;
    private ConnectionChangeReceiver mNetworkReceiver;
    private LocationPermissionsReceiver mLocationPermissionsReceiver;
    private LocationManager locationManager;
    private Context context;
    private Location location;
    private static int minDistance = 0;
    private static int minMillisecondsPassed = 600000;

    public static String getName(){
        return "rebuttonservice";
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        Log.d("RebuttonSer", "I've started");
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "your_channel_id";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Notification Channel Title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
        registerScreenStatusReceiver();

        locationManager  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("RebuttonSer", "GeolocationServiceStarted");
            getLocation();
        }
    }

    @Override
    public void onDestroy() {
        SharedPreferencesHelper.setBackGroundRun(context,false);
        unregisterScreenStatusReceiver();
    }

    private void registerScreenStatusReceiver() {
        mScreenReceiver = new RedButtonAlarmReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenReceiver, filter);

        mNetworkReceiver = new ConnectionChangeReceiver(this);
        registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        mLocationPermissionsReceiver = new LocationPermissionsReceiver(this);
        registerReceiver(mLocationPermissionsReceiver, new IntentFilter("find.me.org.loction.permissions.changed"));
    }

    public void tryToRegisterLocationsReceiver(){
        Log.d("RebuttonSer", "TryToRegisterLocation");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("RebuttonSer", "AccessGrunted");
            getLocation();
        }
    }

    private void unregisterScreenStatusReceiver() {
        try {
            if (mScreenReceiver != null) {
                unregisterReceiver(mScreenReceiver);
            }
            if (mNetworkReceiver != null) {
                unregisterReceiver(mScreenReceiver);
            }
        } catch (IllegalArgumentException e) {}
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("RebuttonSer","locationChanged");

        Log.d("RebuttonSer",SharedPreferencesHelper.getGeoProvided(this)+"");
        Log.d("RebuttonSer",SharedPreferencesHelper.getDangerMode(this)+"");
        if(SharedPreferencesHelper.getGeoProvided(this) && SharedPreferencesHelper.getDangerMode(this)) {
            Log.d("RebuttonSer", "locationProceed");

            JSONObject object = new JSONObject();
            String userId = SharedPreferencesHelper.generateUserId(this);
            try {
                object
                        .put("accountToken", userId)
                        .put("location", new JSONObject()
                                .put("lat", location == null ? null : location.getLatitude())
                                .put("lng", location == null ? null : location.getLongitude())
                                .put("timestamp", new Date().getTime()))
                        .put("currentTimestamp", new Date().getTime());
            } catch (JSONException e) {
                Log.d("BackgroundTrigger", "JSONFailed");
            }
            Log.d("BackgroundTriggerBody", object.toString());


            new AsyncRequest().execute(object.toString());
        }
    }

    class AsyncRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            Log.d("LocationService","Location Got");
            return FindMeHttpUtil.sendRequestAlarm("POST", "/api/v1/updateLocation", arg[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("RedButtonAsyncRequest","onPostExecute");
            super.onPostExecute(s);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                boolean canGetLocation = true;
                if (isGPSEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                minMillisecondsPassed,
                                minDistance, this);
                        Log.d("GPS", "GPS Enabled");
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isNetworkEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                minMillisecondsPassed,
                                minDistance, this);
                        Log.d("Network", "Network enabld");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}
