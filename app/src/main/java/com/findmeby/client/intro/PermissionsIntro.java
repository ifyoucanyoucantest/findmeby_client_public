package com.findmeby.client.intro;

import android.Manifest;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.findmeby.client.HomeActivity_;
import com.findmeby.client.R;


public class PermissionsIntro extends AppIntro2 {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);

        addSlide(AppIntroFragment.newInstance(
                "Welcome!",
                "This is an intro for FindMeBY app!",
                R.drawable.back_slide1));

        addSlide(AppIntroFragment.newInstance(
                "Welcome!",
                "In order to access your location, you must give permissions.",
                R.drawable.back_slide2));

        addSlide(AppIntroFragment.newInstance(
                "Instruction",
                "Some instructions here",
                R.drawable.back_slide3));

        // Request mandatory camera and location permission
        askForPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                2, true);
    }

    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        HomeActivity_.intent(this).start();
        finish();
    }
}