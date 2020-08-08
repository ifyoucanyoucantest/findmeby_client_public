package com.findmeby.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.findmeby.client.intro.IntroMainActivity;
import com.findmeby.client.intro.PermissionsIntro;

import org.androidannotations.annotations.EActivity;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by sergeyz on 4/14/2017.
 */

@EActivity(R.layout.activity_splash)
public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isActive()) {
            HomeActivity_.intent(this).start();
        } else {
            Intent intent = new Intent(this, IntroMainActivity.class);
            this.startActivity(intent);
        }
    }

    private boolean isActive() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("INTRO_DONE", 0) != 0;
    }
}
