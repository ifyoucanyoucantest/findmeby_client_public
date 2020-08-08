package com.findmeby.client;

import android.content.Intent;
import android.widget.Button;

import com.findmeby.client.intro.PermissionsIntro;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import androidx.appcompat.app.AppCompatActivity;

@EActivity(R.layout.activity_home)
public class HomeActivity extends AppCompatActivity {

    @ViewById(R.id.cancel)
    Button cancel;
    @ViewById(R.id.alarm)
    Button alarm;
    @ViewById(R.id.settings)
    Button settings;
    @ViewById(R.id.contacts)
    Button contacts;

    @Click(R.id.contacts)
    void onContacts() {
        ContactsActivity_.intent(HomeActivity.this).start();
    }

    @Click(R.id.settings)
    void onSettings() {
        Intent intent = new Intent(this, PermissionsIntro.class);
        this.startActivity(intent);
    }
}
