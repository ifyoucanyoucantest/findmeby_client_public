package com.findmeby.client.intro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.findmeby.client.R;
import com.findmeby.client.util.FindMeHttpUtil;
import com.findmeby.client.util.SharedPreferencesHelper;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class IntroMainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, LocationListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static Location location;
    private static int minDistance = 0;
    private static int minMillisecondsPassed = 1000;
    private LocationManager locationManager;
    private Context context;
    private String triggerToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        context = this;
        //Background service


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, BackgroundOfflineService.class));
        } else {
            startService(new Intent(this, BackgroundOfflineService.class));
        }*/
        //Background service

        setContentView(R.layout.activity_intro_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.contactsFragment, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intro_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 5) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Granted");
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                sendBroadcast(new Intent("find.me.org.loction.permissions.changed"));
                //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Log.d("Location", locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude() + "");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return;
                //}
                //} else {
                //}
                SharedPreferencesHelper.setGeoProvided(this, true);
            } else {
                SharedPreferencesHelper.setGeoProvided(this, false);
            }
        }
    }

    public Location sendAlarmFromRedButton() {
        triggerToken = java.util.UUID.randomUUID().toString();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return getLocation();
        } else {
            sendAlarmWithoutGeolocation();
            return null;
        }
    }

    public String getTriggerToken() {
        return triggerToken;
    }

    public void sendAlarmWithoutGeolocation() {
        Log.d("onLocationChanged", "locationChanged");

        JSONObject object = new JSONObject();
        String userId = SharedPreferencesHelper.generateUserId(this);
        try {
            object
                    .put("accountToken", userId)
                    .put("location", new JSONObject()
                            .put("lat", null)
                            .put("lng", null)
                            .put("timestamp", new Date().getTime()))
                    .put("originalTimestamp",  new Date().getTime())
                    .put("currentTimestamp", new Date().getTime())
                    .put("triggerToken", triggerToken);
        } catch (JSONException e) {
            Log.d("BackgroundTrigger","JSONFailed");
        }
        Log.d("BackgroundTriggerBody", object.toString());

        new AsyncRequest().execute(object.toString());
        this.location = location;
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

    class AsyncRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            Log.d("RedButtonAsyncRequest","BackGroundStarted");
            return FindMeHttpUtil.sendRequestAlarm("POST", "/api/v1/triggerAlarm", arg[0], context, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("RedButtonAsyncRequest","onPostExecute");
            super.onPostExecute(s);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "locationChanged");

        JSONObject object = new JSONObject();
        String userId = SharedPreferencesHelper.generateUserId(this);
        try {
            object
                    .put("accountToken", userId)
                    .put("location", new JSONObject()
                            .put("lat", location == null ? null : location.getLatitude())
                            .put("lng", location == null ? null : location.getLongitude())
                            .put("timestamp", new Date().getTime()))
                    .put("originalTimestamp",  new Date().getTime())
                    .put("currentTimestamp", new Date().getTime())
                    .put("triggerToken", triggerToken);
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

}