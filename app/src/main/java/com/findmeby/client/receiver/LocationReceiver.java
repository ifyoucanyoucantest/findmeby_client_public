package com.findmeby.client.receiver;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.api.support.content.AbstractBroadcastReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

@EReceiver
public class LocationReceiver extends AbstractBroadcastReceiver {

    public static final String ACTION_LOCATION_CHANGED = "com.findmeby.receivers.LOCATION_CHANGED";


    /**
     * @param timeMs 0 means turn off GPS
     */
    public static void requestLocationUpdates(@NonNull Context context, long timeMs) {
        Intent intent = new Intent(context, LocationReceiver_.class);
        intent.setAction(ACTION_LOCATION_CHANGED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (timeMs <= 0L) {
            if (pendingIntent != null) {
                pendingIntent.cancel();
            }
            return;
        }
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final Criteria locationCriteria = new Criteria();

        locationCriteria.setAccuracy(Criteria.ACCURACY_LOW);
        locationCriteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        if (pendingIntent != null) {
            //Starting location updates
            try {
                if (locationManager != null) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(timeMs, 0f, locationCriteria, pendingIntent);
                    }
                }
            } catch (Exception e) {
                Timber.e(e, "Location update failed");
            }
        }
    }

    @ReceiverAction(actions = ACTION_LOCATION_CHANGED)
    void onLocation(@Nullable @org.androidannotations.annotations.ReceiverAction.Extra(LocationManager.KEY_LOCATION_CHANGED) Location location,
                    @NonNull Context context) {
        Log.d("LocationReceiver", location.getLatitude() + "");
        if (location != null) {
            Log.d("LocationReceiver", location.getLatitude() + "");
            //store location here
        }
    }

}
