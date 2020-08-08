package com.findmeby.client.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.findmeby.client.util.FindMeHttpUtil;
import com.findmeby.client.util.SharedPreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class RedButtonAlarmReceiver extends BroadcastReceiver implements LocationListener {
    private static int tapsInRowCount = 0;
    private static Date lastTapDate = null;
    private static int tapsInRowNeeded = 4;
    private static long miliSecondsBetweenInRowTaps = 1500;
    private static Location location;
    private Context context = null;
    private static int minDistance = 0;
    private static int minMillisecondsPassed = 1000;
    private LocationManager locationManager;
    private String bodyForSending;

    public RedButtonAlarmReceiver() {
        Log.d("redButtonAlarmReceiver", "I'was created");
    }

    public RedButtonAlarmReceiver(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Date currentDate = Calendar.getInstance().getTime();
            if(lastTapDate == null) {
                tapsInRowCount = 1;
            } else {
                long diffInMicroSeconds = currentDate.getTime() - lastTapDate.getTime();

                if(diffInMicroSeconds > miliSecondsBetweenInRowTaps)
                {
                    tapsInRowCount = 1;
                } else {
                    tapsInRowCount++;
                    if(tapsInRowCount == tapsInRowNeeded) {
                        sendAlarm();
                    }
                }
            }

            lastTapDate = currentDate;
        }
    }

    public void sendAlarm() {
        Log.d("FindMe", "Alarm " + tapsInRowCount);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            getCurrentLocation();
        } else {
            sendAlarmWithoutGeolocation();
        }
    }

    public void sendAlarmWithoutGeolocation() {
        Log.d("onLocationChanged", "locationChanged");

        JSONObject object = new JSONObject();
        String userId = SharedPreferencesHelper.generateUserId(context);
        try {
            object
                    .put("accountToken", userId)
                    .put("location", new JSONObject()
                            .put("lat", null)
                            .put("lng", null)
                            .put("timestamp", new Date().getTime()))
                    .put("originalTimestamp",  new Date().getTime())
                    .put("currentTimestamp", new Date().getTime());
        } catch (JSONException e) {
            Log.d("BackgroundTrigger","JSONFailed");
        }
        Log.d("BackgroundTriggerBody", object.toString());
        new AsyncRequest().execute(object.toString());
    }

    public Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return getLocation();
        } else {
            return null;
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "locationChanged");

        JSONObject object = new JSONObject();
        String userId = SharedPreferencesHelper.generateUserId(context);
        try {
            object
                    .put("accountToken", userId)
                    .put("location", new JSONObject()
                            .put("lat", location == null ? null : location.getLatitude())
                            .put("lng", location == null ? null : location.getLongitude())
                            .put("timestamp", new Date().getTime()))
                    .put("originalTimestamp",  new Date().getTime())
                    .put("currentTimestamp", new Date().getTime());
        } catch (JSONException e) {
            Log.d("BackgroundTrigger","JSONFailed");
        }
        Log.d("BackgroundTriggerBody", object.toString());
        locationManager.removeUpdates(this);

        new AsyncRequest().execute(object.toString());

        this.location = location;
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

    class AsyncRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            Log.d("RedButtonAsyncRequest","BackGroundStarted");
            return FindMeHttpUtil.sendRequest("POST", "/api/v1/triggerAlarm", arg[0], context, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("RedButtonAsyncRequest","onPostExecute");
            super.onPostExecute(s);
        }
    }
}
