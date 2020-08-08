package com.findmeby.client.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.findmeby.client.model.OfflineSendRequestType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FindMeHttpUtil {

    public static String sendRequestAlarm(String method, String path, String body, Context context, boolean vibroNeeded, boolean saveIfFailedNeeded) {
        return sendRequestInternal(method,path,body,context,vibroNeeded,saveIfFailedNeeded, OfflineSendRequestType.ALARM);
    }

    public static String sendRequestCancelAlarm(String method, String path, String body, Context context, boolean vibroNeeded, boolean saveIfFailedNeeded) {
        return sendRequestInternal(method,path,body,context,vibroNeeded,saveIfFailedNeeded, OfflineSendRequestType.CANCEL_ALARM);
    }

    public static String sendRequestAlarm(String method, String path, String body) {
        return sendRequestInternal(method,path,body,null,false, false, OfflineSendRequestType.NO_OFFLINE_SUPPORTED);
    }

    private static String sendRequestInternal(String method, String path, String body, Context context, boolean vibroNeeded, boolean saveIfFailedNeeded, OfflineSendRequestType offlineSendRequestType) {
        Log.d("bodyForSending",body);
        URL url = null;
        try {
            url = new URL("https://back.findme-by.org" + path);
        } catch (MalformedURLException e) {
            Log.d("test2","ERROR");
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            //urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "*/*");

            Log.d("HttpUtil", "Writing body");
            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            Log.d("HttpUtil", "Reading Response");
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if(vibroNeeded)
                {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        long[] amplitute = {0,300,100,300,100,300};
                        vibrator.vibrate(VibrationEffect.createWaveform(amplitute, -1));
                    } else {
                        vibrator.vibrate(500);
                    }
                }
                return response.toString();
            }
        }
        catch (Exception e)
        {
            if(context != null && saveIfFailedNeeded) {
                if(offlineSendRequestType == OfflineSendRequestType.ALARM) {
                    SharedPreferencesHelper.setAlarmBody(context, body);
                } else if (offlineSendRequestType == OfflineSendRequestType.CANCEL_ALARM) {
                    SharedPreferencesHelper.setCancelAlarmBody(context, body);
                }
            }
            //Log.d("Exception",e.getStackTrace().toString());
            e.printStackTrace();
        }finally {
            Log.d("OK", "Disconnect");
            urlConnection.disconnect();
        }
        return "";
    }

}
