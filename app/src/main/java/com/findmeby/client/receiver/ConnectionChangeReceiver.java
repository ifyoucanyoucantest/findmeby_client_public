package com.findmeby.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.findmeby.client.util.FindMeHttpUtil;
import com.findmeby.client.util.SharedPreferencesHelper;

import java.util.concurrent.TimeUnit;

public class ConnectionChangeReceiver extends BroadcastReceiver {

    public static final long MIN_NETWORK_CHANGE_NOTIFICATION_INTERVAL = TimeUnit.MINUTES.toMillis(15);
    private Context context = null;

    public ConnectionChangeReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Offline", "NetworkChagned");
        if (haveNetworkConnection(context)) {
            String alarmBody = SharedPreferencesHelper.getAlarmBody(context);
            if(alarmBody != null && !alarmBody.isEmpty())
            {
                SharedPreferencesHelper.setAlarmBody(context,"");
                new AsyncRequest().execute(alarmBody,context);
            }
        }
    }
    private static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();
        for (Network network: networks) {
            NetworkInfo netInfo = cm.getNetworkInfo(network);
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                if (netInfo.isConnected()) {
                    haveConnectedWifi = true;
                }
            }
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (netInfo.isConnected()) {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    class AsyncRequest extends AsyncTask<Object, Integer, String> {
        @Override
        protected String doInBackground(Object... arg) {
            Log.d("RedButtonAsyncRequest","BackGroundStarted");
            return FindMeHttpUtil.sendRequest("POST", "/api/v1/triggerAlarm", (String)arg[0], (Context) arg[1], true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("RedButtonAsyncRequest","onPostExecute");
            super.onPostExecute(s);
        }
    }
}