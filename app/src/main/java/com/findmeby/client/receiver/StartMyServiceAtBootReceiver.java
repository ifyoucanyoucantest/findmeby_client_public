package com.findmeby.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.findmeby.client.service.BootCompletedJobIntentService;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BootCompletedJobIntentService.enqueueWork(context, new Intent());
    }
}
