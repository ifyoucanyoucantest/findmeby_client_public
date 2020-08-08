package com.findmeby.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.findmeby.client.service.RebuttonService;

public class LocationPermissionsReceiver extends BroadcastReceiver {
    private Context context = null;

    public LocationPermissionsReceiver(Context context) {
        this.context = context;
    }


    public LocationPermissionsReceiver(){
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("LocationPermisChgd", "Received");
        ((RebuttonService)context).tryToRegisterLocationsReceiver();
    }
}
